package net.xinshi.discovery.search.mgt.bean;

/**
 * Created with IntelliJ IDEA.
 * User: benzhao
 * Date: 1/24/13
 * Time: 4:18 PM
 */
public class RSSBean {
    private String command;
    private String hostname;
    private int maxresults = 10000;
    private String maxtime = "1m";
    private int track_alert = 0;
    private int ttl = 86400;

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public int getMaxresults() {
        return maxresults;
    }

    public void setMaxresults(int maxresults) {
        this.maxresults = maxresults;
    }

    public String getMaxtime() {
        return maxtime;
    }

    public void setMaxtime(String maxtime) {
        this.maxtime = maxtime;
    }

    public int getTrack_alert() {
        return track_alert;
    }

    public void setTrack_alert(int track_alert) {
        this.track_alert = track_alert;
    }

    public int getTtl() {
        return ttl;
    }

    public void setTtl(int ttl) {
        this.ttl = ttl;
    }
}
