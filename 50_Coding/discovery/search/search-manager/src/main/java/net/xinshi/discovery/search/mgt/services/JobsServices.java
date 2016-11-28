package net.xinshi.discovery.search.mgt.services;

import au.com.bytecode.opencsv.CSVReader;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.xinshi.discovery.search.mgt.bean.ResultSet;
import net.xinshi.discovery.search.mgt.bean.SearchJob;
import org.apache.solr.core.CoreDescriptor;
import org.apache.solr.services.ConfigSaver;
import org.apache.solr.services.DiscoveryServices;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.core.SolrCore;
import org.apache.solr.params.InsightParams;
import org.apache.solr.request.LocalSolrQueryRequest;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.ResultContext;
import org.apache.solr.response.SolrQueryResponse;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: benzhao
 * Date: 1/15/13
 * Time: 4:03 PM
 */
@Service
public class JobsServices {
    private static JobsServices instance;
    private LoadingCache<String, SearchJob> jobs;
    private PriorityBlockingQueue<SearchJob> queue;

    public synchronized static JobsServices getInstance() {
        if (instance == null) {
            instance = new JobsServices();
        }
        return instance;
    }

    private JobsServices() {
        jobs = CacheBuilder.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .build(
                        new CacheLoader<String, SearchJob>() {
                            public SearchJob load(String key) throws Exception {
                                return null;
                            }
                        });

        queue = new PriorityBlockingQueue<SearchJob>(1000);

        new Thread(new JobWorker(queue)).start();
    }


    public SearchJob addJob(SearchJob job) {
        jobs.put(job.getId(), job);
        queue.add(job);
        return job;
    }


    public SearchJob getJob(String jobId) throws ExecutionException {
        return jobs.get(jobId);
    }

    public static SearchJob runJob(SearchJob job){
        SolrCore core = DiscoveryServices.coreContainer.getCore("insight_admin");
        long a = System.currentTimeMillis();
        int start = 0;
        int limit = 100;
        if(!job.getParams().containsKey(InsightParams.INSIGHT_LIMIT)) {
            job.getParams().put(InsightParams.INSIGHT_OFFSET, String.valueOf(0));
            job.getParams().put(InsightParams.INSIGHT_LIMIT, String.valueOf(100));
        }
        else{
             start = Integer.parseInt(job.getParams().get(InsightParams.INSIGHT_OFFSET));
             limit =Integer.parseInt(job.getParams().get(InsightParams.INSIGHT_LIMIT));
        }
        SolrQueryRequest req = new LocalSolrQueryRequest(core, job.getParams().get("q"), null, start, limit, job.getParams());
        SolrQueryResponse rsp = new SolrQueryResponse();
        //PigeonServices.getInstance().getSaasEngine().setCurrentMerchantId(job.getOwner());

        core.execute(core.getRequestHandler("/select"), req, rsp);

        ResultContext resultContext = (ResultContext) rsp.getValues().get("response");

        NamedList insight = (NamedList) rsp.getValues().get("insight");

        if (insight != null) {
            ResultSet results = new ResultSet();
            results.setValues(rsp.getValues());
            results.setTotal((Long) insight.get("total"));
            results.setSchema(req.getSchema());
            results.setParams(req.getParams());
            results.setReturnFields(rsp.getReturnFields());
            CSVReader csvReader = (CSVReader)insight.get("export");
            if (csvReader != null) {
                results.setCsvReader(csvReader);
                insight.remove("export");
            }
            job.setResults(results);
            job.setResultCount(String.valueOf(results.getTotal()));
            job.setEventCount(job.getResultCount());

        } else {
            ResultSet events = new ResultSet();
            events.setResultContext(resultContext);
            events.setTotal(0);
            events.setSchema(req.getSchema());
            events.setParams(req.getParams());
            events.setValues(rsp.getValues());
            events.setReturnFields(rsp.getReturnFields());
            job.setEvents(events);
            job.setEventCount(String.valueOf(events.getTotal()));
            job.setResultCount(job.getEventCount());
        }
        job.setDone("1");
        job.setDispatchState(SearchJob.STATE_DONE);
        long b =  System.currentTimeMillis();
        long elapsed = (b-a);
        return job;
    }

    protected ConfigSaver configSaver = new ConfigSaver();

    class JobWorker implements Runnable {
        private PriorityBlockingQueue<SearchJob> queue;

        JobWorker(PriorityBlockingQueue<SearchJob> queue) {
            this.queue = queue;
        }

        @Override
        public void run() {
            SolrCore core = DiscoveryServices.coreContainer.getCore("insight_admin");
            if (core == null) {
                synchronized (DiscoveryServices.coreContainer) {
//                                //copy the configs from a template
                    configSaver.save("insight_admin", configSaver.getConfigTemplate("insight_admin"));

                    //create the core
                    CoreDescriptor dcore = new CoreDescriptor(DiscoveryServices.coreContainer, "insight_admin", "insight_admin");

                    SolrCore newCore = DiscoveryServices.coreContainer.create(dcore);

                    DiscoveryServices.coreContainer.register("insight_admin", newCore, false);
                }
                core = DiscoveryServices.coreContainer.getCore("insight_admin");
            }



            while (true) {
                SearchJob job = null;
                try {
                    job = this.queue.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (job != null) {
                    try {
                        System.out.println("running " + job.getId() +  " job");
                        long a = System.currentTimeMillis();

                        job.getParams().put(InsightParams.INSIGHT_OFFSET, String.valueOf(0));
                        job.getParams().put(InsightParams.INSIGHT_LIMIT, String.valueOf(100));
                        SolrQueryRequest req = new LocalSolrQueryRequest(core, job.getParams().get("q"), null, 0, 100, job.getParams());
                        SolrQueryResponse rsp = new SolrQueryResponse();
//                        PigeonServices.getInstance().getSaasEngine().setCurrentMerchantId(job.getOwner());

                        core.execute(core.getRequestHandler("/select"), req, rsp);

                        ResultContext resultContext = (ResultContext) rsp.getValues().get("response");

                        NamedList insight = (NamedList) rsp.getValues().get("insight");

                        if (insight != null) {
                            ResultSet results = new ResultSet();
                            results.setValues(rsp.getValues());
                            results.setTotal((Long) insight.get("total"));
                            results.setSchema(req.getSchema());
                            results.setParams(req.getParams());
                            results.setReturnFields(rsp.getReturnFields());
                            CSVReader csvReader = (CSVReader)insight.get("export");
                            if (csvReader != null) {
                                results.setCsvReader(csvReader);
                                insight.remove("export");
                            }
                            job.setResults(results);
                            job.setResultCount(String.valueOf(results.getTotal()));
                            job.setEventCount(job.getResultCount());

                        } else {
                            ResultSet events = new ResultSet();
                            events.setResultContext(resultContext);
                            events.setTotal(resultContext.docs.matches());
                            events.setSchema(req.getSchema());
                            events.setParams(req.getParams());
                            events.setValues(rsp.getValues());
                            events.setReturnFields(rsp.getReturnFields());
                            job.setEvents(events);
                            job.setEventCount(String.valueOf(events.getTotal()));
                            job.setResultCount(job.getEventCount());
                        }
                        job.setDone("1");
                        job.setDispatchState(SearchJob.STATE_DONE);
                        System.out.println("finished " + job.getId() +  " job " + (System.currentTimeMillis() - a ) + "ms");
                    } catch (Exception e) {
                        e.printStackTrace();
                        job.setDispatchState(SearchJob.STATE_FAILED);
                    }
                } else {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }
}


