package net.xinshi.discovery.search.mgt.bean;



/**
 * Created with IntelliJ IDEA.
 * User: benzhao
 * Date: 1/24/13
 * Time: 3:58 PM
 */
public class SavedSearch {
    private String search;
    private String description;

    private DispatchBean dispatch = new DispatchBean();
    private ActionBean action = new ActionBean();
    private AlertBean alert = new AlertBean();
    private UIBean ui = new UIBean();
    private AutoSummarizeBean auto_summarize = new AutoSummarizeBean();


    private int is_disabled = 0;
    private String cron_schedule;
    private String displayview;
    private String vsid;

    private String alert_comparator;
    private String alert_condition;
    private String alert_threshold;
    private String alert_type;

    public String getVsid() {
        return vsid;
    }

    public void setVsid(String vsid) {
        this.vsid = vsid;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public DispatchBean getDispatch() {
        return dispatch;
    }

    public void setDispatch(DispatchBean dispatch) {
        this.dispatch = dispatch;
    }

    public ActionBean getAction() {
        return action;
    }

    public void setAction(ActionBean action) {
        this.action = action;
    }

    public AlertBean getAlert() {
        return alert;
    }

    public void setAlert(AlertBean alert) {
        this.alert = alert;
    }

    public UIBean getUi() {
        return ui;
    }

    public void setUi(UIBean ui) {
        this.ui = ui;
    }

    public int getIs_disabled() {
        return is_disabled;
    }

    public void setIs_disabled(int is_disabled) {
        this.is_disabled = is_disabled;
    }

    public AutoSummarizeBean getAuto_summarize() {
        return auto_summarize;
    }

    public void setAuto_summarize(AutoSummarizeBean auto_summarize) {
        this.auto_summarize = auto_summarize;
    }

    public String getCron_schedule() {
        return cron_schedule;
    }

    public void setCron_schedule(String cron_schedule) {
        this.cron_schedule = cron_schedule;
    }

    public String getDisplayview() {
        return displayview;
    }

    public void setDisplayview(String displayview) {
        this.displayview = displayview;
    }

    public String getAlert_comparator() {
        return alert_comparator;
    }

    public void setAlert_comparator(String alert_comparator) {
        this.alert_comparator = alert_comparator;
    }

    public String getAlert_condition() {
        return alert_condition;
    }

    public void setAlert_condition(String alert_condition) {
        this.alert_condition = alert_condition;
    }

    public String getAlert_threshold() {
        return alert_threshold;
    }

    public void setAlert_threshold(String alert_threshold) {
        this.alert_threshold = alert_threshold;
    }

    public String getAlert_type() {
        return alert_type;
    }

    public void setAlert_type(String alert_type) {
        this.alert_type = alert_type;
    }
}
