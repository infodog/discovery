package net.xinshi.discovery.search.mgt.bean;

/**
 * Created with IntelliJ IDEA.
 * User: benzhao
 * Date: 1/24/13
 * Time: 4:18 PM
 */
public class EmailBean {
    private String auth_password;
    private String auth_username;

    private String bcc;
    private String cc;

    private String command;
    private String format = "html";
    private String from = "Discovery.Insight";
    private String hostname;
    private int inline = 0;
    private String mailserver;
    private int maxresults = 10000;
    private String maxtime = "5m";
    private Boolean pdfview;
    private String preprocess_results;
    //            reportCIDFontList  gb cns jp kor
//            reportIncludeSplunkLogo  1
//            reportPaperOrientation  portrait
//            reportPaperSize  letter
//            reportServerEnabled  0
//            reportServerURL
//
    private int sendpdf = 0;
    private int sendresults = 0;
    private String subject = "Insights Alert: $name$";
    private String to;
    private int track_alert = 1;
    private int ttl = 86400;
    private int use_ssl = 0;
    private int use_tls = 0;
    private int width_sort_columns = 1;

    public String getAuth_password() {
        return auth_password;
    }

    public void setAuth_password(String auth_password) {
        this.auth_password = auth_password;
    }

    public String getAuth_username() {
        return auth_username;
    }

    public void setAuth_username(String auth_username) {
        this.auth_username = auth_username;
    }

    public String getBcc() {
        return bcc;
    }

    public void setBcc(String bcc) {
        this.bcc = bcc;
    }

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public int getInline() {
        return inline;
    }

    public void setInline(int inline) {
        this.inline = inline;
    }

    public String getMailserver() {
        return mailserver;
    }

    public void setMailserver(String mailserver) {
        this.mailserver = mailserver;
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

    public Boolean getPdfview() {
        return pdfview;
    }

    public void setPdfview(Boolean pdfview) {
        this.pdfview = pdfview;
    }

    public String getPreprocess_results() {
        return preprocess_results;
    }

    public void setPreprocess_results(String preprocess_results) {
        this.preprocess_results = preprocess_results;
    }

    public int getSendpdf() {
        return sendpdf;
    }

    public void setSendpdf(int sendpdf) {
        this.sendpdf = sendpdf;
    }

    public int getSendresults() {
        return sendresults;
    }

    public void setSendresults(int sendresults) {
        this.sendresults = sendresults;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
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

    public int getUse_ssl() {
        return use_ssl;
    }

    public void setUse_ssl(int use_ssl) {
        this.use_ssl = use_ssl;
    }

    public int getUse_tls() {
        return use_tls;
    }

    public void setUse_tls(int use_tls) {
        this.use_tls = use_tls;
    }

    public int getWidth_sort_columns() {
        return width_sort_columns;
    }

    public void setWidth_sort_columns(int width_sort_columns) {
        this.width_sort_columns = width_sort_columns;
    }
}
