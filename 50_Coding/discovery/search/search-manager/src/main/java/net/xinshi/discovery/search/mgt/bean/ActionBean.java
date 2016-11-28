package net.xinshi.discovery.search.mgt.bean;

/**
 * Created with IntelliJ IDEA.
 * User: benzhao
 * Date: 1/24/13
 * Time: 4:04 PM
 */
public class ActionBean {
    private EmailBean email = new EmailBean();
    private Populate_lookup populate_lookup = new Populate_lookup();
    private SummaryBean summary = new SummaryBean();
    private ScriptBean script = new ScriptBean();
    private RSSBean rss = new RSSBean();

    public EmailBean getEmail() {
        return email;
    }

    public void setEmail(EmailBean email) {
        this.email = email;
    }

    public Populate_lookup getPopulate_lookup() {
        return populate_lookup;
    }

    public void setPopulate_lookup(Populate_lookup populate_lookup) {
        this.populate_lookup = populate_lookup;
    }

    public SummaryBean getSummary() {
        return summary;
    }

    public void setSummary(SummaryBean summary) {
        this.summary = summary;
    }

    public ScriptBean getScript() {
        return script;
    }

    public void setScript(ScriptBean script) {
        this.script = script;
    }

    public RSSBean getRss() {
        return rss;
    }

    public void setRss(RSSBean rss) {
        this.rss = rss;
    }
}
