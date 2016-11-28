package org.apache.solr.handler.component;

import org.apache.solr.insight.filter.InsightFilter;
import org.apache.solr.insight.filter.InsightFilterFactory;
import org.apache.solr.insight.filter.InterResult;
import org.apache.solr.insight.PipeOperator;
import org.apache.solr.params.InsightParams;
import org.apache.solr.search.ReturnFields;

import java.io.IOException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: benzhao
 * Date: 6/24/13
 * Time: 2:30 PM
 */
public class InsightComponent extends SearchComponent {
    public static final String COMPONENT_NAME = "insight";

    @Override
    public void prepare(ResponseBuilder rb) throws IOException {
        if (rb.req.getParams().getBool(InsightParams.INSIGHT, false)) {
            rb.setNeedDocSet(true);
        }
    }

    @Override
    public void process(ResponseBuilder rb) throws IOException {
        if (rb.req.getParams().getBool(InsightParams.INSIGHT, false)) {
            FieldValueCollector fieldValueCollector = new FieldValueCollector(rb.req, rb.getResults().docSet, rb.req.getParams());
            int offset = rb.req.getParams().getInt(InsightParams.INSIGHT_OFFSET, 0);
            int limit = rb.req.getParams().getInt(InsightParams.INSIGHT_LIMIT, 10);
            try {
                List<InsightFilter> filters = InsightFilterFactory.parseFilters(rb.req.getParams().get(InsightParams.INSIGHT_PIPE));
                InterResult interResult;
                if (filters.size() > 0) {
                    interResult = fieldValueCollector.getResults();
                } else {
                    interResult = fieldValueCollector.getResults(offset,limit);
                }

                PipeOperator po = new PipeOperator(filters, interResult, offset, limit);

                rb.rsp.getValues().remove("response");
                rb.rsp.add("insight", po.getOutput());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getDescription() {
        return "Discovery Insight Component";
    }

    @Override
    public String getSource() {
        return null;
    }
}
