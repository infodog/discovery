package net.xinshi.discovery.search.mgt.bean;

/**
 * Created with IntelliJ IDEA.
 * User: benzhao
 * Date: 1/24/13
 * Time: 4:05 PM
 */
public class AutoSummarizeBean {
    private String command;
    private String cron_schedule = "*/10 * * * *";
//            dispatch.earliest_time
//            dispatch.latest_time
//            dispatch.ttl 60

    private int max_disabled_buckets = 2;
    private double max_summary_ratio = 0.1;
    private int max_summary_size = 52428800;
    private int max_time = 3600;
    private String suspend_period = "24h";
    private String timespan;

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getCron_schedule() {
        return cron_schedule;
    }

    public void setCron_schedule(String cron_schedule) {
        this.cron_schedule = cron_schedule;
    }

    public int getMax_disabled_buckets() {
        return max_disabled_buckets;
    }

    public void setMax_disabled_buckets(int max_disabled_buckets) {
        this.max_disabled_buckets = max_disabled_buckets;
    }

    public double getMax_summary_ratio() {
        return max_summary_ratio;
    }

    public void setMax_summary_ratio(double max_summary_ratio) {
        this.max_summary_ratio = max_summary_ratio;
    }

    public int getMax_summary_size() {
        return max_summary_size;
    }

    public void setMax_summary_size(int max_summary_size) {
        this.max_summary_size = max_summary_size;
    }

    public int getMax_time() {
        return max_time;
    }

    public void setMax_time(int max_time) {
        this.max_time = max_time;
    }

    public String getSuspend_period() {
        return suspend_period;
    }

    public void setSuspend_period(String suspend_period) {
        this.suspend_period = suspend_period;
    }

    public String getTimespan() {
        return timespan;
    }

    public void setTimespan(String timespan) {
        this.timespan = timespan;
    }
}
