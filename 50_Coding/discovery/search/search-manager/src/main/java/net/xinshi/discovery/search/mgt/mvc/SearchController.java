package net.xinshi.discovery.search.mgt.mvc;

import com.google.common.base.Stopwatch;
import net.xinshi.discovery.search.mgt.bean.SearchJob;
import net.xinshi.discovery.search.mgt.services.DQLParser;
import net.xinshi.discovery.search.mgt.services.JobsServices;
import net.xinshi.discovery.search.mgt.util.MgtUtils;
import org.apache.solr.core.SolrCore;
import org.apache.solr.params.InsightParams;
import org.apache.solr.services.DiscoveryServices;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;


/**
 * Created with IntelliJ IDEA.
 * User: benzhao
 * Date: 1/4/13
 * Time: 2:58 PM
 */
@Controller
public class SearchController {
    private final MgtUtils mgtUtils = new MgtUtils();
    private DQLParser dqlParser = new DQLParser();


    @RequestMapping(value = "servicesNS/{owner}/{app}/search/typeahead", method = RequestMethod.GET)
    public
    @ResponseBody
    String typeAhead(@PathVariable String owner, @PathVariable String app, @RequestParam String prefix, @RequestParam Integer count) {
        SolrCore core = DiscoveryServices.coreContainer.getCore("insight_admin");
//        TODO performance
        Stopwatch watch = new Stopwatch();
        watch.start();
        Collection<String> fields = core.getSearcher().get().getFieldNames();
        System.out.println("fields: " + fields.size());
        StringBuilder result = new StringBuilder();
        result.append("{\"results\":[");
        int i = 0;
        for (String field : fields) {
            if (field.startsWith(prefix)) {
                i++;
                if (i > 1) {
                    result.append(",");
                }
                result.append("{\"content\":");
                result.append("\"");
                result.append(field);
                result.append("\"");
                result.append(", \"count\": 1}");
            }
            if (i >= count) {
                break;
            }
        }
        result.append("]}");

        watch.stop();
        System.out.println("prefix : " + prefix + "  elapsed time: " + watch.elapsed(TimeUnit.MILLISECONDS) + "ms");
        return result.toString();
    }

    @RequestMapping(value = "servicesNS/{owner}/{app}/search/jobs", method = RequestMethod.POST)
    public
    @ResponseBody
    String createJobs(@PathVariable String owner, @PathVariable String app, @RequestBody String body) {

        Map<String, List<String>> params = null;
        try {
            params = mgtUtils.parsePostBody(body);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String sid = UUID.randomUUID().toString();
        SearchJob job = new SearchJob();
        MgtUtils.fillValues(job, params);
        if (job.getQuery()==null || "".equals(job.getQuery().trim())) {
        }
        job.setId(sid);
        job.setOwner(owner);
        job.setParams(this.dqlParser.parse(job, owner));
        job.setDispatchState(SearchJob.STATE_QUEUED);
        JobsServices.getInstance().addJob(job);

        return "<response>\n" +
                "  <sid>" + sid + "</sid>\n" +
                "</response>";
    }

    /**
     * 实时返回，而不是异步返回的job
     * @param owner
     * @param search
     * @return
     */
    @RequestMapping(value = "servicesNS/search/syncJob", method = RequestMethod.POST,produces = "application/json; charset=utf-8")
    public
    @ResponseBody
    String syncJob(@RequestParam String owner,  @RequestParam String search,@RequestParam int from,int num) throws JSONException {
        try {
            SearchJob job = new SearchJob();
            if (job.getQuery()==null || "".equals(job.getQuery().trim())) {
            }
            job.setOwner(owner);
            job.setSearch(search);
            job.setParams(this.dqlParser.parse(job, owner));
            job.setDispatchState(SearchJob.STATE_QUEUED);
            job.getParams().put(InsightParams.INSIGHT_OFFSET, String.valueOf(from));
            job.getParams().put(InsightParams.INSIGHT_LIMIT, String.valueOf(num));
            JobsServices.runJob(job);
            if(job.getResults()!=null){
                String output =  job.getResults().output("json_cols", 0, num);
                JSONObject json = new JSONObject(output);
                JSONArray jsonRows = json.optJSONArray("columns");
                JSONObject jsonInsight = json.optJSONObject("insight");
                long total = jsonInsight.optLong("total");
                JSONObject result = new JSONObject();
                result.put("total",total);
                result.put("rows",jsonRows);
                return result.toString();
            }
            else{
                long total = 0;
                JSONObject result = new JSONObject();
                result.put("total",total);
                result.put("rows",new JSONArray());
                return result.toString();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }




    @RequestMapping(value = "services/search/jobs/{sid}", method = RequestMethod.GET)
    public String job(@PathVariable String sid, Model model) {
        SearchJob searchJob = parseQuery(sid);

        //TODO marshalling   refactor me later
        SearchJob temp = new SearchJob();
        temp.setId(searchJob.getId());
        temp.setSearch(searchJob.getSearch());
        temp.setCreateTime(searchJob.getCreateTime());
        temp.setModifiedTime(searchJob.getModifiedTime());
        temp.setQuery(searchJob.getQuery());
        temp.setEarliest_time(searchJob.getEarliest_time());
        temp.setLatest_time(searchJob.getLatest_time());
        temp.setResultCount(searchJob.getResultCount());
        temp.setEventCount(searchJob.getEventCount());
        temp.setDispatchState(searchJob.getDispatchState());
        temp.setCurrent_hierarchy(searchJob.getCurrent_hierarchy());
        temp.setNext_hierarchy(searchJob.getNext_hierarchy());
        temp.setDone(searchJob.getDone());

        model.addAttribute(temp);

        return "xmlView";
    }

    private SearchJob parseQuery(String sid) {
        SearchJob searchJob = null;
        try {
            searchJob = JobsServices.getInstance().getJob(sid);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        if (searchJob == null) {
            searchJob = new SearchJob();
        }
        return searchJob;
    }


    @RequestMapping(value = "services/search/jobs/{sid}/events", method = RequestMethod.GET, produces = "application/xml; charset=utf-8")
    public
    @ResponseBody
    String jobEvents(@PathVariable String sid, @RequestParam Integer count, @RequestParam(required = false, defaultValue = "0") Integer offset) {
        SearchJob searchJob = parseQuery(sid);

        if (searchJob.getDispatchState().equals(SearchJob.STATE_DONE) && searchJob.getResults() != null) {
            return searchJob.getResults().output("xml", offset, count);
        } else {
            return "";
        }
    }

    @RequestMapping(value = "services/search/jobs/{sid}/results", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    public
    @ResponseBody
    String jobResults(@PathVariable String sid, @RequestParam Integer count,
                      @RequestParam(required = false, defaultValue = "0") Integer offset,
                      @RequestParam String output_mode) {

        SearchJob searchJob = parseQuery(sid);

        if (searchJob.getDispatchState().equals(SearchJob.STATE_DONE) && searchJob.getResults() != null) {
            return searchJob.getResults().output(output_mode, offset, count);
        } else {
            return "";
        }
    }

    @RequestMapping(value = "servicesNS/{owner}/{app}/search/jobs/{sid}/results/export", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    public
    @ResponseBody
    String jobResultsExport(@PathVariable String owner, @PathVariable String app, @PathVariable String sid, @RequestParam(required = false, defaultValue = "10000") Integer count,
                      @RequestParam(required = false, defaultValue = "0") Integer offset,
                      @RequestParam String output_mode) {

        SearchJob searchJob = parseQuery(sid);

        if (searchJob.getDispatchState().equals(SearchJob.STATE_DONE) && searchJob.getResults() != null) {
            return searchJob.getResults().export();
        } else {
            return "";
        }
    }



    @RequestMapping(value = "services/search/jobs/{sid}/results_preview", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    public
    @ResponseBody
    String jobResultsPreview(@PathVariable String sid, @RequestParam Integer count,
                      @RequestParam(required = false, defaultValue = "0") Integer offset,
                      @RequestParam String output_mode) {

        SearchJob searchJob = parseQuery(sid);

        if (searchJob.getDispatchState().equals(SearchJob.STATE_DONE) && searchJob.getResults() != null) {
            return searchJob.getResults().output(output_mode, offset, count);
        } else {
            return "";
        }
    }


    @RequestMapping(value = "services/search/jobs/{sid}/summary", method = RequestMethod.GET, produces = "application/xml; charset=utf-8")
    public
    @ResponseBody
    String jobSummary(@PathVariable String sid) {
        SearchJob searchJob = parseQuery(sid);

        return "not implemented yet!";
    }


    @RequestMapping(value = "services/search/timeparser/tz", method = RequestMethod.GET)
    public
    @ResponseBody
    String timeParser() {
        return "### SERIALIZED TIMEZONE FORMAT 1.0;Y29152 NW 4C 4D 54;Y32400 YW 43 44 54;Y28800 NW 43 53 54;@-1325491552 2;@-933494400 1;@-923130000 2;@-908784000 1;@-891594000 2;@515520000 1;@527007600 2;@545155200 1;@558457200 2;@576604800 1;@589906800 2;@608659200 1;@621961200 2;@640108800 1;@653410800 2;@671558400 1;@684860400 2;";
    }


    @RequestMapping(value = "services/search/test/feed", method = RequestMethod.GET)
    public ModelAndView testFeed() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("atomView");
        modelAndView.addObject("job", new SearchJob());

        return modelAndView;
    }

    @RequestMapping(value = "services/search/parser", method = RequestMethod.GET)
    public
    @ResponseBody
    String searchParser(@RequestParam String q) {
        System.out.println("Search Parser : " + q);
        String result = "<response>\n" +
                "  <dict>\n" +
                "    <key name=\"remoteSearch\">litsearch *:* | fields  keepcolorder=t \"_bkt\" \"_cd\" \"_si\" \"host\" \"index\" \"linecount\" \"source\" \"sourcetype\" \"splunk_server\"</key>\n" +
                "    <key name=\"remoteTimeOrdered\">1</key>\n" +
                "    <key name=\"eventsSearch\">search *:*</key>\n" +
                "    <key name=\"eventsTimeOrdered\">1</key>\n" +
                "    <key name=\"eventsStreaming\">1</key>\n" +
                "    <key name=\"reportsSearch\"></key>\n" +
                "    <key name=\"canSummarize\">0</key>\n" +
                "  </dict>\n" +
                "  <list>\n" +
                "    <item>\n" +
                "      <dict>\n" +
                "        <key name=\"command\">search</key>\n" +
                "        <key name=\"rawargs\">*:*</key>\n" +
                "        <key name=\"pipeline\">streaming</key>\n" +
                "        <key name=\"args\">\n" +
                "          <dict>\n" +
                "            <key name=\"search\">\n" +
                "              <list>\n" +
                "                <item>*:*</item>\n" +
                "              </list>\n" +
                "            </key>\n" +
                "          </dict>\n" +
                "        </key>\n" +
                "        <key name=\"isGenerating\">1</key>\n" +
                "        <key name=\"streamType\">SP_STREAM</key>\n" +
                "      </dict>\n" +
                "    </item>\n" +
                "  </list>\n" +
                "</response>";
        return result;
    }
}