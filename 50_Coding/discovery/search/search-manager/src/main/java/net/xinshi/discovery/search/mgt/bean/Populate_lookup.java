package net.xinshi.discovery.search.mgt.bean;

/**
 * Created with IntelliJ IDEA.
 * User: benzhao
 * Date: 1/24/13
 * Time: 4:18 PM
 */
public class Populate_lookup {
    private String command;
    private String dest;
    private String hostname;
    private int maxresults = 10000;
    private String maxtime = "5m";
    private int track_alert = 0;
    private int ttl = 120;

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getDest() {
        return dest;
    }

    public void setDest(String dest) {
        this.dest = dest;
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
