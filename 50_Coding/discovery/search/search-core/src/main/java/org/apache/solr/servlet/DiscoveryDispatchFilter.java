package org.apache.solr.servlet;

import org.apache.commons.io.IOUtils;
import org.apache.solr.common.SolrException;
import org.apache.solr.common.cloud.*;
import org.apache.solr.common.params.CommonParams;
import org.apache.solr.common.params.MapSolrParams;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.ContentStreamBase;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.common.util.SimpleOrderedMap;
import org.apache.solr.common.util.StrUtils;
import org.apache.solr.core.CoreContainer;
import org.apache.solr.core.CoreDescriptor;
import org.apache.solr.core.SolrConfig;
import org.apache.solr.core.SolrCore;
import org.apache.solr.handler.ContentStreamHandlerBase;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.request.SolrQueryRequestBase;
import org.apache.solr.request.SolrRequestHandler;
import org.apache.solr.request.SolrRequestInfo;
import org.apache.solr.response.BinaryQueryResponseWriter;
import org.apache.solr.response.QueryResponseWriter;
import org.apache.solr.response.SolrQueryResponse;
import org.apache.solr.schedule.SearchScheduler;
import org.apache.solr.services.ConfigSaver;
import org.apache.solr.services.DiscoveryServices;
import org.apache.solr.servlet.cache.HttpCacheHeaderUtil;
import org.apache.solr.servlet.cache.Method;
import org.apache.solr.util.FastWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.*;
import java.util.Map.Entry;

/**
 * Created with IntelliJ IDEA.
 * User: benzhao
 * Date: 1/15/13
 * Time: 11:00 AM
 */
public class DiscoveryDispatchFilter implements Filter {
    final Logger log;
    //protected volatile CoreContainer cores;
    protected String pathPrefix = null; // strip this from the beginning of a path
    protected String abortErrorMessage = null;
    protected final Map<SolrConfig, SolrRequestParsers> parsers = new WeakHashMap<SolrConfig, SolrRequestParsers>();

    //Discovery
    protected ConfigSaver configSaver = new ConfigSaver();
    //protected boolean ipDistinction = false;


    private static final Charset UTF8 = Charset.forName("UTF-8");

    public DiscoveryDispatchFilter() {
        try {
            log = LoggerFactory.getLogger(SolrDispatchFilter.class);
        } catch (NoClassDefFoundError e) {
            throw new SolrException(
                    SolrException.ErrorCode.SERVER_ERROR,
                    "Could not find necessary SLF4j logging jars. If using Jetty, the SLF4j logging jars need to go in "
                            +"the jetty lib/ext directory. For other containers, the corresponding directory should be used. "
                            +"For more information, see: http://wiki.apache.org/solr/SolrLogging",
                    e);
        }
    }

    @Override
    public void init(FilterConfig config) throws ServletException
    {
        log.info("SolrDispatchFilter.init()");

        try {
            // web.xml configuration
            this.pathPrefix = config.getInitParameter( "path-prefix" );

            //TODO Discovery
            String home = this.configSaver.getPath();
            if(home != null) {
                home = home + File.separator + "solr";
            }

            System.getProperties().put("solr.solr.home", home);
            DiscoveryServices.init();

            //this.cores = createCoreContainer();

            //Discovery
//            String ipdist = System.getProperty("discovery.ipDistinction");
//            if (ipdist != null && "true".equals(ipdist)) {
//                this.ipDistinction = true;
//            }
//            log.info("discovery.ipDistinction=" + this.ipDistinction);

            log.info("user.dir=" + System.getProperty("user.dir"));
        }
        catch( Throwable t ) {
            // catch this so our filter still works
            log.error( "Could not start Solr. Check solr/home property and the logs");
            SolrCore.log( t );
        }

        //Discovery
        SearchScheduler.getInstance().optimizeIndex(DiscoveryServices.coreContainer);


        log.info("SolrDispatchFilter.init() done");
    }

    /**
     * Override this to change CoreContainer initialization
     * @return a CoreContainer to hold this server's cores
     */
   /* protected CoreContainer createCoreContainer() {
        CoreContainer cores = new CoreContainer();
        cores.load();
        return cores;
    }*/

    /*public CoreContainer getCores() {
        return cores;
    }*/

    @Override
    public void destroy() {
        DiscoveryServices.coreContainer.shutdown();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        doFilter(request, response, chain, false);
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain, boolean retry) throws IOException, ServletException {
        if( abortErrorMessage != null ) {
            ((HttpServletResponse)response).sendError( 500, abortErrorMessage );
            return;
        }

        if (DiscoveryServices.coreContainer == null) {
            ((HttpServletResponse)response).sendError( 503, "Server is shutting down" );
            return;
        }

        CoreContainer cores = DiscoveryServices.coreContainer;
        SolrCore core = null;
        SolrQueryRequest solrReq = null;
        Aliases aliases = null;

        if( request instanceof HttpServletRequest) {
            HttpServletRequest req = (HttpServletRequest)request;
            HttpServletResponse resp = (HttpServletResponse)response;
            SolrRequestHandler handler = null;
            String corename = "";
            String origCorename = null;
            try {
                // put the core container in request attribute
                req.setAttribute("org.apache.solr.CoreContainer", cores);
                String path = req.getServletPath();
                if( req.getPathInfo() != null ) {
                    // this lets you handle /update/commit when /update is a servlet
                    path += req.getPathInfo();
                }
                if( pathPrefix != null && path.startsWith( pathPrefix ) ) {
                    path = path.substring( pathPrefix.length() );
                }
                // check for management path
                String alternate = cores.getManagementPath();
                if (alternate != null && path.startsWith(alternate)) {
                    path = path.substring(0, alternate.length());
                }
                // unused feature ?
                int idx = path.indexOf( ':' );
                if( idx > 0 ) {
                    // save the portion after the ':' for a 'handler' path parameter
                    path = path.substring( 0, idx );
                }

                // Check for the core admin page
                if( path.equals( cores.getAdminPath() ) ) {
                    handler = cores.getMultiCoreHandler();
                    solrReq =  SolrRequestParsers.DEFAULT.parse(null,path, req);
                    handleAdminRequest(req, response, handler, solrReq);
                    return;
                }
                boolean usingAliases = false;
                List<String> collectionsList = null;
                // Check for the core admin collections url
                if( path.equals( "/admin/collections" ) ) {
                    handler = cores.getCollectionsHandler();
                    solrReq =  SolrRequestParsers.DEFAULT.parse(null,path, req);
                    handleAdminRequest(req, response, handler, solrReq);
                    return;
                }
                else {
                    //otherwise, we should find a core from the path
                    idx = path.indexOf( "/", 1 );
                    if( idx > 1 ) {
                        // try to get the corename as a request parameter first
                        corename = path.substring( 1, idx );

                        // look at aliases
                        if (cores.isZooKeeperAware()) {
                            origCorename = corename;
                            ZkStateReader reader = cores.getZkController().getZkStateReader();
                            aliases = reader.getAliases();
                            if (aliases != null && aliases.collectionAliasSize() > 0) {
                                usingAliases = true;
                                String alias = aliases.getCollectionAlias(corename);
                                if (alias != null) {
                                    collectionsList = StrUtils.splitSmart(alias, ",", true);
                                    corename = collectionsList.get(0);
                                }
                            }
                        }

                        //Discovery
                        //ip distinction
//                        if (!this.ipDistinction && !"all_in_one".equals(corename)) {
//                            int ipidx = corename.lastIndexOf("_");
//                            if (ipidx > 1) {
//                                corename = corename.substring(0, ipidx);
//                            }
//                        }

                        core = cores.getCore(corename);

                        //Discovery
                        //create
                        if (core == null) {

                            synchronized (cores) {
//                                //copy the configs from a template
                                this.configSaver.save(corename, this.configSaver.getConfigTemplate(corename));

                                //create the core
                                CoreDescriptor dcore = new CoreDescriptor(cores, corename, corename);

                                SolrCore newCore = cores.create(dcore);

                                cores.register(corename, newCore, false);
                            }
                            core = cores.getCore(corename);
                        }

                        if (core != null) {
                            path = path.substring( idx );
                        }
                    }
                    if (core == null) {
                        if (!cores.isZooKeeperAware() ) {
                            core = cores.getCore("");
                        }
                    }
                }

                if (core == null && cores.isZooKeeperAware()) {
                    // we couldn't find the core - lets make sure a collection was not specified instead
                    core = getCoreByCollection(cores, corename, path);

                    if (core != null) {
                        // we found a core, update the path
                        path = path.substring( idx );
                    }

                    // if we couldn't find it locally, look on other nodes
                    if (core == null && idx > 0) {
                        String coreUrl = getRemotCoreUrl(cores, corename, origCorename);
                        if (coreUrl != null) {
                            path = path.substring( idx );
                            remoteQuery(coreUrl + path, req, solrReq, resp);
                            return;
                        } else {
                            if (!retry) {
                                // we couldn't find a core to work with, try reloading aliases
                                // TODO: it would be nice if admin ui elements skipped this...
                                ZkStateReader reader = cores.getZkController()
                                        .getZkStateReader();
                                reader.updateAliases();
                                doFilter(request, response, chain, true);
                                return;
                            }
                        }
                    }

                    // try the default core
                    if (core == null) {
                        core = cores.getCore("");
                    }
                }

                // With a valid core...
                if( core != null ) {
                    final SolrConfig config = core.getSolrConfig();
                    // get or create/cache the parser for the core
                    SolrRequestParsers parser = null;
                    parser = parsers.get(config);
                    if( parser == null ) {
                        parser = new SolrRequestParsers(config);
                        parsers.put(config, parser );
                    }

                    // Handle /schema/* paths via Restlet
                    if( path.startsWith("/schema") ) {
                        solrReq = parser.parse(core, path, req);
                        SolrRequestInfo.setRequestInfo(new SolrRequestInfo(solrReq, new SolrQueryResponse()));
                        if( path.equals(req.getServletPath()) ) {
                            // avoid endless loop - pass through to Restlet via webapp
                            chain.doFilter(request, response);
                        } else {
                            // forward rewritten URI (without path prefix and core/collection name) to Restlet
                            req.getRequestDispatcher(path).forward(request, response);
                        }
                        return;
                    }

                    // Determine the handler from the url path if not set
                    // (we might already have selected the cores handler)
                    if( handler == null && path.length() > 1 ) { // don't match "" or "/" as valid path
                        handler = core.getRequestHandler( path );
                        // no handler yet but allowed to handle select; let's check
                        if( handler == null && parser.isHandleSelect() ) {
                            if( "/select".equals( path ) || "/select/".equals( path ) ) {
                                solrReq = parser.parse( core, path, req );
                                String qt = solrReq.getParams().get( CommonParams.QT );
                                handler = core.getRequestHandler( qt );
                                if( handler == null ) {
                                    throw new SolrException( SolrException.ErrorCode.BAD_REQUEST, "unknown handler: "+qt);
                                }
                                if( qt != null && qt.startsWith("/") && (handler instanceof ContentStreamHandlerBase)) {
                                    //For security reasons it's a bad idea to allow a leading '/', ex: /select?qt=/update see SOLR-3161
                                    //There was no restriction from Solr 1.4 thru 3.5 and it's not supported for update handlers.
                                    throw new SolrException( SolrException.ErrorCode.BAD_REQUEST, "Invalid Request Handler ('qt').  Do not use /select to access: "+qt);
                                }
                            }
                        }
                    }

                    // With a valid handler and a valid core...
                    if( handler != null ) {
                        // if not a /select, create the request
                        if( solrReq == null ) {
                            solrReq = parser.parse( core, path, req );
                        }

                        if (usingAliases) {
                            processAliases(solrReq, aliases, collectionsList);
                        }

                        final Method reqMethod = Method.getMethod(req.getMethod());
                        HttpCacheHeaderUtil.setCacheControlHeader(config, resp, reqMethod);
                        // unless we have been explicitly told not to, do cache validation
                        // if we fail cache validation, execute the query
                        if (config.getHttpCachingConfig().isNever304() ||
                                !HttpCacheHeaderUtil.doCacheHeaderValidation(solrReq, req, reqMethod, resp)) {
                            SolrQueryResponse solrRsp = new SolrQueryResponse();
                /* even for HEAD requests, we need to execute the handler to
                 * ensure we don't get an error (and to make sure the correct
                 * QueryResponseWriter is selected and we get the correct
                 * Content-Type)
                 */
                            SolrRequestInfo.setRequestInfo(new SolrRequestInfo(solrReq, solrRsp));
                            this.execute( req, handler, solrReq, solrRsp );
                            HttpCacheHeaderUtil.checkHttpCachingVeto(solrRsp, resp, reqMethod);
                            // add info to http headers
                            //TODO: See SOLR-232 and SOLR-267.
                /*try {
                  NamedList solrRspHeader = solrRsp.getResponseHeader();
                 for (int i=0; i<solrRspHeader.size(); i++) {
                   ((javax.servlet.http.HttpServletResponse) response).addHeader(("Solr-" + solrRspHeader.getName(i)), String.valueOf(solrRspHeader.getVal(i)));
                 }
                } catch (ClassCastException cce) {
                  log.log(Level.WARNING, "exception adding response header log information", cce);
                }*/
                            QueryResponseWriter responseWriter = core.getQueryResponseWriter(solrReq);
                            writeResponse(solrRsp, response, responseWriter, solrReq, reqMethod);
                        }
                        return; // we are done with a valid handler
                    }
                }
                log.debug("no handler or core retrieved for " + path + ", follow through...");
            }
            catch (Throwable ex) {
                sendError( core, solrReq, request, (HttpServletResponse)response, ex );
                return;
            }
            finally {
                if( solrReq != null ) {
                    log.debug("Closing out SolrRequest: {}", solrReq);
                    solrReq.close();
                }
                if (core != null) {
                    core.close();
                }
                SolrRequestInfo.clearRequestInfo();
            }
        }

        // Otherwise let the webapp handle the request
        chain.doFilter(request, response);
    }

    private void processAliases(SolrQueryRequest solrReq, Aliases aliases,
                                List<String> collectionsList) {
        String collection = solrReq.getParams().get("collection");
        if (collection != null) {
            collectionsList = StrUtils.splitSmart(collection, ",", true);
        }
        if (collectionsList != null) {
            Set<String> newCollectionsList = new HashSet<String>(
                    collectionsList.size());
            for (String col : collectionsList) {
                String al = aliases.getCollectionAlias(col);
                if (al != null) {
                    List<String> aliasList = StrUtils.splitSmart(al, ",", true);
                    newCollectionsList.addAll(aliasList);
                } else {
                    newCollectionsList.add(col);
                }
            }
            if (newCollectionsList.size() > 0) {
                StringBuilder collectionString = new StringBuilder();
                Iterator<String> it = newCollectionsList.iterator();
                int sz = newCollectionsList.size();
                for (int i = 0; i < sz; i++) {
                    collectionString.append(it.next());
                    if (i < newCollectionsList.size() - 1) {
                        collectionString.append(",");
                    }
                }
                ModifiableSolrParams params = new ModifiableSolrParams(
                        solrReq.getParams());
                params.set("collection", collectionString.toString());
                solrReq.setParams(params);
            }
        }
    }

    private void remoteQuery(String coreUrl, HttpServletRequest req,
                             SolrQueryRequest solrReq, HttpServletResponse resp) throws IOException {
        try {
            String urlstr = coreUrl;

            String queryString = req.getQueryString();

            urlstr += queryString == null ? "" : "?" + queryString;

            URL url = new URL(urlstr);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod(req.getMethod());
            con.setUseCaches(false);

            con.setDoOutput(true);
            con.setDoInput(true);
            for (Enumeration e = req.getHeaderNames(); e.hasMoreElements();) {
                String headerName = e.nextElement().toString();
                con.setRequestProperty(headerName, req.getHeader(headerName));
            }
            try {
                con.connect();

                InputStream is;
                OutputStream os;
                if ("POST".equals(req.getMethod())) {
                    is = req.getInputStream();
                    os = con.getOutputStream(); // side effect: method is switched to POST
                    try {
                        IOUtils.copyLarge(is, os);
                        os.flush();
                    } finally {
                        IOUtils.closeQuietly(os);
                        IOUtils.closeQuietly(is);  // TODO: I thought we weren't supposed to explicitly close servlet streams
                    }
                }

                resp.setStatus(con.getResponseCode());

                for (Iterator i = con.getHeaderFields().entrySet().iterator(); i
                        .hasNext();) {
                    Map.Entry mapEntry = (Map.Entry) i.next();
                    if (mapEntry.getKey() != null) resp.setHeader(mapEntry.getKey()
                            .toString(), ((List) mapEntry.getValue()).get(0).toString());
                }

                resp.setCharacterEncoding(con.getContentEncoding());
                resp.setContentType(con.getContentType());

                is = con.getInputStream();
                os = resp.getOutputStream();
                try {
                    IOUtils.copyLarge(is, os);
                    os.flush();
                } finally {
                    IOUtils.closeQuietly(os);   // TODO: I thought we weren't supposed to explicitly close servlet streams
                    IOUtils.closeQuietly(is);
                }
            } finally {
                con.disconnect();
            }
        } catch (IOException e) {
            sendError(null, solrReq, req, resp, new SolrException(
                    SolrException.ErrorCode.SERVER_ERROR,
                    "Error trying to proxy request for url: " + coreUrl, e));
        }

    }

    private String getRemotCoreUrl(CoreContainer cores, String collectionName, String origCorename) {
        ClusterState clusterState = cores.getZkController().getClusterState();
        Collection<Slice> slices = clusterState.getActiveSlices(collectionName);
        boolean byCoreName = false;
        if (slices == null) {
            // look by core name
            byCoreName = true;
            Set<String> collections = clusterState.getCollections();
            for (String collection : collections) {
                slices = new ArrayList<Slice>();
                slices.addAll(clusterState.getActiveSlices(collection));
            }
        }

        if (slices == null || slices.size() == 0) {
            return null;
        }

        Set<String> liveNodes = clusterState.getLiveNodes();
        Iterator<Slice> it = slices.iterator();
        while (it.hasNext()) {
            Slice slice = it.next();
            Map<String,Replica> sliceShards = slice.getReplicasMap();
            for (ZkNodeProps nodeProps : sliceShards.values()) {
                ZkCoreNodeProps coreNodeProps = new ZkCoreNodeProps(nodeProps);
                if (liveNodes.contains(coreNodeProps.getNodeName())
                        && coreNodeProps.getState().equals(ZkStateReader.ACTIVE)) {
                    if (byCoreName && !collectionName.equals(coreNodeProps.getCoreName())) {
                        // if it's by core name, make sure they match
                        continue;
                    }
                    if (coreNodeProps.getBaseUrl().equals(cores.getZkController().getBaseUrl())) {
                        // don't count a local core
                        continue;
                    }
                    String coreUrl;
                    if (origCorename != null) {
                        coreUrl = coreNodeProps.getBaseUrl() + "/" + origCorename;
                    } else {
                        coreUrl = coreNodeProps.getCoreUrl();
                        if (coreUrl.endsWith("/")) {
                            coreUrl = coreUrl.substring(0, coreUrl.length() - 1);
                        }
                    }

                    return coreUrl;
                }
            }
        }
        return null;
    }

    private SolrCore getCoreByCollection(CoreContainer cores, String corename, String path) {
        String collection = corename;
        ZkStateReader zkStateReader = cores.getZkController().getZkStateReader();

        ClusterState clusterState = zkStateReader.getClusterState();
        Map<String,Slice> slices = clusterState.getActiveSlicesMap(collection);
        if (slices == null) {
            return null;
        }
        // look for a core on this node
        Set<Entry<String,Slice>> entries = slices.entrySet();
        SolrCore core = null;
        done:
        for (Entry<String,Slice> entry : entries) {
            // first see if we have the leader
            ZkNodeProps leaderProps = clusterState.getLeader(collection, entry.getKey());
            if (leaderProps != null) {
                core = checkProps(cores, path, leaderProps);
            }
            if (core != null) {
                break done;
            }

            // check everyone then
            Map<String,Replica> shards = entry.getValue().getReplicasMap();
            Set<Entry<String,Replica>> shardEntries = shards.entrySet();
            for (Entry<String,Replica> shardEntry : shardEntries) {
                Replica zkProps = shardEntry.getValue();
                core = checkProps(cores, path, zkProps);
                if (core != null) {
                    break done;
                }
            }
        }
        return core;
    }

    private SolrCore checkProps(CoreContainer cores, String path,
                                ZkNodeProps zkProps) {
        String corename;
        SolrCore core = null;
        if (cores.getZkController().getNodeName().equals(zkProps.getStr(ZkStateReader.NODE_NAME_PROP))) {
            corename = zkProps.getStr(ZkStateReader.CORE_NAME_PROP);
            core = cores.getCore(corename);
        }
        return core;
    }

    private void handleAdminRequest(HttpServletRequest req, ServletResponse response, SolrRequestHandler handler,
                                    SolrQueryRequest solrReq) throws IOException {
        SolrQueryResponse solrResp = new SolrQueryResponse();
        SolrCore.preDecorateResponse(solrReq, solrResp);
        handler.handleRequest(solrReq, solrResp);
        SolrCore.postDecorateResponse(handler, solrReq, solrResp);
        if (log.isInfoEnabled() && solrResp.getToLog().size() > 0) {
            log.info(solrResp.getToLogAsString("[admin] "));
        }
        QueryResponseWriter respWriter = SolrCore.DEFAULT_RESPONSE_WRITERS.get(solrReq.getParams().get(CommonParams.WT));
        if (respWriter == null) respWriter = SolrCore.DEFAULT_RESPONSE_WRITERS.get("standard");
        writeResponse(solrResp, response, respWriter, solrReq, Method.getMethod(req.getMethod()));
    }

    private void writeResponse(SolrQueryResponse solrRsp, ServletResponse response,
                               QueryResponseWriter responseWriter, SolrQueryRequest solrReq, Method reqMethod)
            throws IOException {

        // Now write it out
        final String ct = responseWriter.getContentType(solrReq, solrRsp);
        // don't call setContentType on null
        if (null != ct) response.setContentType(ct);

        if (solrRsp.getException() != null) {
            NamedList info = new SimpleOrderedMap();
            int code = ResponseUtils.getErrorInfo(solrRsp.getException(), info, log);
            solrRsp.add("error", info);
            ((HttpServletResponse) response).setStatus(code);
        }

        if (Method.HEAD != reqMethod) {
            if (responseWriter instanceof BinaryQueryResponseWriter) {
                BinaryQueryResponseWriter binWriter = (BinaryQueryResponseWriter) responseWriter;
                binWriter.write(response.getOutputStream(), solrReq, solrRsp);
            } else {
                String charset = ContentStreamBase.getCharsetFromContentType(ct);
                Writer out = (charset == null || charset.equalsIgnoreCase("UTF-8"))
                        ? new OutputStreamWriter(response.getOutputStream(), UTF8)
                        : new OutputStreamWriter(response.getOutputStream(), charset);
                out = new FastWriter(out);
                responseWriter.write(out, solrReq, solrRsp);
                out.flush();
            }
        }
        //else http HEAD request, nothing to write out, waited this long just to get ContentType
    }

    protected void execute( HttpServletRequest req, SolrRequestHandler handler, SolrQueryRequest sreq, SolrQueryResponse rsp) {
        // a custom filter could add more stuff to the request before passing it on.
        // for example: sreq.getContext().put( "HttpServletRequest", req );
        // used for logging query stats in SolrCore.execute()
        sreq.getContext().put( "webapp", req.getContextPath() );
        sreq.getCore().execute( handler, sreq, rsp );
    }

    protected void sendError(SolrCore core,
                             SolrQueryRequest req,
                             ServletRequest request,
                             HttpServletResponse response,
                             Throwable ex) throws IOException {
        try {
            SolrQueryResponse solrResp = new SolrQueryResponse();
            if(ex instanceof Exception) {
                solrResp.setException((Exception)ex);
            }
            else {
                solrResp.setException(new RuntimeException(ex));
            }
            if(core==null) {
                core = DiscoveryServices.coreContainer.getCore(""); // default core
            }
            if(req==null) {
                final SolrParams solrParams;
                if (request instanceof HttpServletRequest) {
                    // use GET parameters if available:
                    solrParams = SolrRequestParsers.parseQueryString(((HttpServletRequest) request).getQueryString());
                } else {
                    // we have no params at all, use empty ones:
                    solrParams = new MapSolrParams(Collections.<String,String>emptyMap());
                }
                req = new SolrQueryRequestBase(core, solrParams) {};
            }
            QueryResponseWriter writer = core.getQueryResponseWriter(req);
            writeResponse(solrResp, response, writer, req, Method.GET);
        }
        catch( Throwable t ) { // This error really does not matter
            SimpleOrderedMap info = new SimpleOrderedMap();
            int code = ResponseUtils.getErrorInfo(ex, info, log);
            response.sendError( code, info.toString() );
        }
    }

    //---------------------------------------------------------------------
    //---------------------------------------------------------------------

    /**
     * Set the prefix for all paths.  This is useful if you want to apply the
     * filter to something other then /*, perhaps because you are merging this
     * filter into a larger web application.
     *
     * For example, if web.xml specifies:
     * <pre class="prettyprint">
     * {@code
     * <filter-mapping>
     *  <filter-name>SolrRequestFilter</filter-name>
     *  <url-pattern>/xxx/*</url-pattern>
     * </filter-mapping>}
     * </pre>
     *
     * Make sure to set the PathPrefix to "/xxx" either with this function
     * or in web.xml.
     *
     * <pre class="prettyprint">
     * {@code
     * <init-param>
     *  <param-name>path-prefix</param-name>
     *  <param-value>/xxx</param-value>
     * </init-param>}
     * </pre>
     */
    public void setPathPrefix(String pathPrefix) {
        this.pathPrefix = pathPrefix;
    }

    public String getPathPrefix() {
        return pathPrefix;
    }

}
