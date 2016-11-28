package net.xinshi.discovery.search.mgt.bean;

/**
 * Created with IntelliJ IDEA.
 * User: benzhao
 * Date: 1/24/13
 * Time: 4:04 PM
 */
public class AlertBean {
    private SuppressBean suppress = new SuppressBean();


    private int digest_mode = 1;
    private String expires = "24h";
    private int severity = 3;
    private int track = 0;

    public SuppressBean getSuppress() {
        return suppress;
    }

    public void setSuppress(SuppressBean suppress) {
        this.suppress = suppress;
    }

    public int getDigest_mode() {
        return digest_mode;
    }

    public void setDigest_mode(int digest_mode) {
        this.digest_mode = digest_mode;
    }

    public String getExpires() {
        return expires;
    }

    public void setExpires(String expires) {
        this.expires = expires;
    }

    public int getSeverity() {
        return severity;
    }

    public void setSeverity(int severity) {
        this.severity = severity;
    }

    public int getTrack() {
        return track;
    }

    public void setTrack(int track) {
        this.track = track;
    }
}
