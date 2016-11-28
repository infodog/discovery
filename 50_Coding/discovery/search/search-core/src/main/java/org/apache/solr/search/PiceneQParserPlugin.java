package org.apache.solr.search;

import org.apache.lucene.search.Query;
import org.apache.solr.common.params.CommonParams;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.request.SolrQueryRequest;

/**
 * Created with IntelliJ IDEA.
 * User: benzhao
 * Date: 10/11/12
 * Time: 3:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class PiceneQParserPlugin extends QParserPlugin {
    public static String NAME = "picene";


    @Override
    public QParser createParser(String qstr, SolrParams localParams, SolrParams params, SolrQueryRequest req) {
        return new PiceneQParser(qstr, localParams, params, req);
    }

    @Override
    public void init(NamedList args) {
    }
}

class PiceneQParser extends QParser {
    PiceneQueryParser parser;

    /**
     * Constructor for the QParser
     *                                                                       i
     * @param qstr        The part of the query string specific to this parser－123
     * @param localParams The set of parameters that are specific to this QParser.  See http://wiki.apache.org/solr/LocalParams
     * @param params      The rest of the {@link org.apache.solr.common.params.SolrParams}
     * @param req         The original {@link org.apache.solr.request.SolrQueryRequest}.
     */
    public PiceneQParser(String qstr, SolrParams localParams, SolrParams params, SolrQueryRequest req) {
        super(qstr, localParams, params, req);
    }

    @Override
    public Query parse() throws SyntaxError {
        String qstr = getString();
        if (qstr == null || qstr.length() == 0) return null;

        String defaultField = getParam(CommonParams.DF);
        if (defaultField == null) {
            defaultField = getReq().getSchema().getDefaultSearchFieldName();
        }

        if (defaultField != null & getReq() != null) {
            parser = new PiceneQueryParser(defaultField, getReq().getSchema().getAnalyzer(), getReq().getSchema().getQueryAnalyzer());
        } else {
            parser = new PiceneQueryParser();
        }
        Query q = null;
        try {
            q = parser.parse(defaultField, qstr);
            System.out.println("Query : " + q);
        } catch (Exception e) {
            e.printStackTrace();
            throw new SyntaxError(e.getMessage());
        }

        return q;
    }
}

