package org.apache.solr.handler.component;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.apache.lucene.search.utils.StringUtil;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.core.SolrCore;
import org.apache.solr.params.TrackParams;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.update.AddUpdateCommand;
import org.apache.solr.update.CommitUpdateCommand;
import org.apache.solr.util.DefaultSolrThreadFactory;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class TrackComponent extends SearchComponent {

    public static final String COMPONENT_NAME = "track";
    public KeywordTracker tracker = null;

    private ScheduledExecutorService scheduler = null;

    @Override
    public void prepare(ResponseBuilder rb) throws IOException {

    }

    @Override
    public void process(ResponseBuilder rb) throws IOException {
        SolrParams params = rb.req.getParams();

        if (params.getBool(TrackParams.TRACK_STAT, false) && this.tracker != null) {
            String date = params.get(TrackParams.TRACK_STAT_DATE);
            try {
                this.IndexKeywords(rb.req.getCore(), rb.req, date);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (!params.getBool(COMPONENT_NAME, false)) return;

        tracker = getTraker(rb.req.getCore().getDataDir());


        String keyword = params.get(TrackParams.TRACK_KEYWORD);
        String category = params.get(TrackParams.TRACK_CATEGORY);

        long foundNum = rb.getResults().docList.matches();

        this.tracker.saveKeyword(keyword, category, foundNum);
    }

    private void IndexKeywords(SolrCore core, SolrQueryRequest req, String date) {
        try {
            System.out.println(core.getName() + " : IndexKeyword : " + date);
            List<KeywordStat> keywords = this.tracker.getKeywordStat(date);
            for (KeywordStat keyword : keywords) {

                try {
                    AddUpdateCommand add = new AddUpdateCommand(req);
                    SolrInputDocument doc = new SolrInputDocument();
                    doc.addField("id", keyword.getSolrKey());
                    doc.addField("stats_self_saas_type", "keyword");
                    doc.addField("keyword", keyword.getKeyword());
                    if (keyword.getFoundNum() > 0) {
                        doc.addField("keyword_freq", Joiner.on("::").join(keyword.getKeyword(), keyword.getCount()));
                    }
                    doc.addField("keyword_text", keyword.getKeyword());
                    doc.addField("pinyin", StringUtil.getPinYin(keyword.getKeyword()));
                    doc.addField("category", keyword.getCategory());
                    doc.addField("foundNum", keyword.getFoundNum());
                    doc.addField("count_i", keyword.getCount());
                    doc.addField("keyworddate", date);
                    add.solrDoc = doc;
                    core.getUpdateHandler().addDoc(add);
                } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
                    badHanyuPinyinOutputFormatCombination.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            CommitUpdateCommand cuc = new CommitUpdateCommand(null,true);
            core.getUpdateHandler().commit(cuc);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private synchronized KeywordTracker getTraker(String dataDir) {

        if (tracker == null) {
            System.out.println("new Tracker...");
            tracker = new KeywordTracker(dataDir);
            scheduler = Executors.newSingleThreadScheduledExecutor(new DefaultSolrThreadFactory("TrackScheduler"));
            this.scheduler.scheduleAtFixedRate(tracker, 2, 5, TimeUnit.MINUTES);
        }

        return tracker;
    }

    @Override
    public String getDescription() {
        return "tracking keywords";
    }

    @Override
    public String getSource() {
        return "$URL$";
    }

}
