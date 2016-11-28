package net.xinshi.discovery.search.mgt.bean;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: benzhao
 * Date: 1/4/13
 * Time: 6:57 PM
 */
@XmlRootElement(name = "job")
public class SearchJob implements Comparable {

    public static String STATE_QUEUED = "QUEUED";
    public static String STATE_PARSING = "PARSING";
    public static String STATE_RUNNING = "RUNNING";
    public static String STATE_FINALIZING = "FINALIZING";
    public static String STATE_DONE = "DONE";
    public static String STATE_FAILED = "FAILED";

    private  String id;
    private String search;
    private  String createTime;
    private  String modifiedTime;
    private  String namespace;
    private  String owner;
    private String query;
    private Map<String,String> params;
    private String earliest_time;
    private String latest_time;

    private String resultCount = "0";
    private String eventCount = "0";

    private String done = "0";
    private String dispatchState;
    private Integer priority;


    //drilldown
    private String current_hierarchy;
    private String next_hierarchy;

    @XmlTransient
    private ResultSet events;
    @XmlTransient
    private ResultSet results;

    public String getDone() {
        return done;
    }

    public void setDone(String done) {
        this.done = done;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public ResultSet getEvents() {
        return events;
    }

    public void setEvents(ResultSet events) {
        this.events = events;
    }

    public ResultSet getResults() {
        return results;
    }

    public void setResults(ResultSet results) {
        this.results = results;
    }

    public String getCurrent_hierarchy() {
        return current_hierarchy;
    }

    public void setCurrent_hierarchy(String current_hierarchy) {
        this.current_hierarchy = current_hierarchy;
    }

    public String getNext_hierarchy() {
        return next_hierarchy;
    }

    public void setNext_hierarchy(String next_hierarchy) {
        this.next_hierarchy = next_hierarchy;
    }

    public String getEarliest_time() {
        return earliest_time;
    }

    public void setEarliest_time(String earliest_time) {
        this.earliest_time = earliest_time;
    }

    public String getLatest_time() {
        return latest_time;
    }

    public void setLatest_time(String latest_time) {
        this.latest_time = latest_time;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getResultCount() {
        return resultCount;
    }

    public void setResultCount(String resultCount) {
        this.resultCount = resultCount;
    }

    public String getEventCount() {
        return eventCount;
    }

    public void setEventCount(String eventCount) {
        this.eventCount = eventCount;
    }

    public String getDispatchState() {
        return dispatchState;
    }

    public void setDispatchState(String dispatchState) {
        this.dispatchState = dispatchState;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(String modifiedTime) {
        this.modifiedTime = modifiedTime;
    }


    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}
