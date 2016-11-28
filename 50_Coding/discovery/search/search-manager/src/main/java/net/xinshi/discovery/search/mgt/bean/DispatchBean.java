package net.xinshi.discovery.search.mgt.bean;

/**
 * Created with IntelliJ IDEA.
 * User: benzhao
 * Date: 1/24/13
 * Time: 4:04 PM
 */
public class DispatchBean {
    private int buckets = 0;
    private String earliest_time;
    private String latest_time;
    private int lookups = 1;
    private int max_count = 500000;
    private int max_time = 0;
    private int reduce_freq = 10;
    private int rt_backfill = 0;
    private int spawn_process = 1;
    private String time_format = "%FT%T.%Q%:z";
    private String ttl = "2p";

    public int getBuckets() {
        return buckets;
    }

    public void setBuckets(int buckets) {
        this.buckets = buckets;
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

    public int getLookups() {
        return lookups;
    }

    public void setLookups(int lookups) {
        this.lookups = lookups;
    }

    public int getMax_count() {
        return max_count;
    }

    public void setMax_count(int max_count) {
        this.max_count = max_count;
    }

    public int getMax_time() {
        return max_time;
    }

    public void setMax_time(int max_time) {
        this.max_time = max_time;
    }

    public int getReduce_freq() {
        return reduce_freq;
    }

    public void setReduce_freq(int reduce_freq) {
        this.reduce_freq = reduce_freq;
    }

    public int getRt_backfill() {
        return rt_backfill;
    }

    public void setRt_backfill(int rt_backfill) {
        this.rt_backfill = rt_backfill;
    }

    public int getSpawn_process() {
        return spawn_process;
    }

    public void setSpawn_process(int spawn_process) {
        this.spawn_process = spawn_process;
    }

    public String getTime_format() {
        return time_format;
    }

    public void setTime_format(String time_format) {
        this.time_format = time_format;
    }

    public String getTtl() {
        return ttl;
    }

    public void setTtl(String ttl) {
        this.ttl = ttl;
    }
}
