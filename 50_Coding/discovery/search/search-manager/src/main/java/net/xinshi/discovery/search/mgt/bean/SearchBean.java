package net.xinshi.discovery.search.mgt.bean;

import javax.validation.constraints.NotNull;

/**
 * Created with IntelliJ IDEA.
 * User: benzhao
 * Date: 1/4/13
 * Time: 3:46 PM
 */
public class SearchBean {

    public SearchBean() {
    }

    public SearchBean(String search, Integer auto_cancel, Integer auto_finalize_ec, Integer auto_pause, String earliest_time, Boolean enable_lookups, Enum exec_mode, Boolean force_bundle_replication, String id, String index_earliest, String index_latest, String latest_time, Integer max_count, Integer max_time, String namespace, String now, Number reduce_freq, Boolean reload_macros, String remote_server_list, String required_field_list, String rf, Boolean rt_blocking, String search_listener, Enum search_mode, Boolean spawn_process, Integer status_buckets, Boolean sync_bundle_replication, String time_format, Integer timeout) {
        this.search = search;
        this.auto_cancel = auto_cancel;
        this.auto_finalize_ec = auto_finalize_ec;
        this.auto_pause = auto_pause;
        this.earliest_time = earliest_time;
        this.enable_lookups = enable_lookups;
        this.exec_mode = exec_mode;
        this.force_bundle_replication = force_bundle_replication;
        this.id = id;
        this.index_earliest = index_earliest;
        this.index_latest = index_latest;
        this.latest_time = latest_time;
        this.max_count = max_count;
        this.max_time = max_time;
        this.namespace = namespace;
        this.now = now;
        this.reduce_freq = reduce_freq;
        this.reload_macros = reload_macros;
        this.remote_server_list = remote_server_list;
        this.required_field_list = required_field_list;
        this.rf = rf;
        this.rt_blocking = rt_blocking;
        this.search_listener = search_listener;
        this.search_mode = search_mode;
        this.spawn_process = spawn_process;
        this.status_buckets = status_buckets;
        this.sync_bundle_replication = sync_bundle_replication;
        this.time_format = time_format;
        this.timeout = timeout;
    }

    @NotNull
    private String search = "*";


    private Integer auto_cancel;


    private Integer auto_finalize_ec;


    private Integer auto_pause;


    private String earliest_time;


    private Boolean enable_lookups;


    private Enum exec_mode;


    private Boolean force_bundle_replication;


    private String id;


    private String index_earliest;


    private String index_latest;


    private String latest_time;


    private Integer max_count = 10000;


    private Integer max_time;


    private String namespace;


    private String now;


    private Number reduce_freq;


    private Boolean reload_macros = true;


    private String remote_server_list;


    private String required_field_list;


    private String rf;


    private Boolean rt_blocking;


    private String search_listener;


    private Enum search_mode;


    private Boolean spawn_process = true;


    private Integer status_buckets;


    private Boolean sync_bundle_replication;


    private String time_format;


    private Integer timeout = 86400;


    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public Integer getAuto_cancel() {
        return auto_cancel;
    }

    public void setAuto_cancel(Integer auto_cancel) {
        this.auto_cancel = auto_cancel;
    }

    public Integer getAuto_finalize_ec() {
        return auto_finalize_ec;
    }

    public void setAuto_finalize_ec(Integer auto_finalize_ec) {
        this.auto_finalize_ec = auto_finalize_ec;
    }

    public Integer getAuto_pause() {
        return auto_pause;
    }

    public void setAuto_pause(Integer auto_pause) {
        this.auto_pause = auto_pause;
    }

    public String getEarliest_time() {
        return earliest_time;
    }

    public void setEarliest_time(String earliest_time) {
        this.earliest_time = earliest_time;
    }

    public Boolean getEnable_lookups() {
        return enable_lookups;
    }

    public void setEnable_lookups(Boolean enable_lookups) {
        this.enable_lookups = enable_lookups;
    }

    public Enum getExec_mode() {
        return exec_mode;
    }

    public void setExec_mode(Enum exec_mode) {
        this.exec_mode = exec_mode;
    }

    public Boolean getForce_bundle_replication() {
        return force_bundle_replication;
    }

    public void setForce_bundle_replication(Boolean force_bundle_replication) {
        this.force_bundle_replication = force_bundle_replication;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIndex_earliest() {
        return index_earliest;
    }

    public void setIndex_earliest(String index_earliest) {
        this.index_earliest = index_earliest;
    }

    public String getIndex_latest() {
        return index_latest;
    }

    public void setIndex_latest(String index_latest) {
        this.index_latest = index_latest;
    }

    public String getLatest_time() {
        return latest_time;
    }

    public void setLatest_time(String latest_time) {
        this.latest_time = latest_time;
    }

    public Integer getMax_count() {
        return max_count;
    }

    public void setMax_count(Integer max_count) {
        this.max_count = max_count;
    }

    public Integer getMax_time() {
        return max_time;
    }

    public void setMax_time(Integer max_time) {
        this.max_time = max_time;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getNow() {
        return now;
    }

    public void setNow(String now) {
        this.now = now;
    }

    public Number getReduce_freq() {
        return reduce_freq;
    }

    public void setReduce_freq(Number reduce_freq) {
        this.reduce_freq = reduce_freq;
    }

    public Boolean getReload_macros() {
        return reload_macros;
    }

    public void setReload_macros(Boolean reload_macros) {
        this.reload_macros = reload_macros;
    }

    public String getRemote_server_list() {
        return remote_server_list;
    }

    public void setRemote_server_list(String remote_server_list) {
        this.remote_server_list = remote_server_list;
    }

    public String getRequired_field_list() {
        return required_field_list;
    }

    public void setRequired_field_list(String required_field_list) {
        this.required_field_list = required_field_list;
    }

    public String getRf() {
        return rf;
    }

    public void setRf(String rf) {
        this.rf = rf;
    }

    public Boolean getRt_blocking() {
        return rt_blocking;
    }

    public void setRt_blocking(Boolean rt_blocking) {
        this.rt_blocking = rt_blocking;
    }

    public String getSearch_listener() {
        return search_listener;
    }

    public void setSearch_listener(String search_listener) {
        this.search_listener = search_listener;
    }

    public Enum getSearch_mode() {
        return search_mode;
    }

    public void setSearch_mode(Enum search_mode) {
        this.search_mode = search_mode;
    }

    public Boolean getSpawn_process() {
        return spawn_process;
    }

    public void setSpawn_process(Boolean spawn_process) {
        this.spawn_process = spawn_process;
    }

    public Integer getStatus_buckets() {
        return status_buckets;
    }

    public void setStatus_buckets(Integer status_buckets) {
        this.status_buckets = status_buckets;
    }

    public Boolean getSync_bundle_replication() {
        return sync_bundle_replication;
    }

    public void setSync_bundle_replication(Boolean sync_bundle_replication) {
        this.sync_bundle_replication = sync_bundle_replication;
    }

    public String getTime_format() {
        return time_format;
    }

    public void setTime_format(String time_format) {
        this.time_format = time_format;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }
}
