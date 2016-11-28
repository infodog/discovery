package org.apache.solr.schedule;

import org.apache.solr.core.CoreContainer;
import org.quartz.*;

import java.text.ParseException;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.CronScheduleBuilder.dailyAtHourAndMinute;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * User: benzhao
 * Date: 11/17/12
 * Time: 4:01 PM
 */
public class SearchScheduler {
    private SchedulerFactory schedFact = new org.quartz.impl.StdSchedulerFactory();
    private  Scheduler sched = null;
    private static SearchScheduler instance = null;

    private SearchScheduler() {
    }

    public synchronized static SearchScheduler getInstance() {
        if (instance == null) {
            instance = new SearchScheduler();
        }
        return instance;
    }

    public  void optimizeIndex(CoreContainer cores) {

        try {
            sched = schedFact.getScheduler();
            JobDataMap jobdata = new JobDataMap();
            jobdata.put("coreContainer",cores);
            JobDetail optimizeIndexJob = newJob(OptimizeIndexJob.class)
                    .withIdentity("optimizeIndex", "search")
                    .usingJobData(jobdata)
                    .build();

            Trigger t = newTrigger()
                    .withIdentity("OptimizeTrigger")
                    .forJob(optimizeIndexJob)
                    .withSchedule(dailyAtHourAndMinute(2, 22))
//                    .withSchedule(dailyAtHourAndMinute(17, 22))
                    .build();

            sched.scheduleJob(optimizeIndexJob,t);

            JobDetail keywordStatJob = newJob(KeywordStatJob.class)
                    .withIdentity("keywordStat", "search")
                    .usingJobData(jobdata)
                    .build();

            Trigger kt = newTrigger()
                    .withIdentity("keywordStatTrigger")
                    .forJob(keywordStatJob)
//                    .withSchedule(dailyAtHourAndMinute(17, 20))
                    .withSchedule(dailyAtHourAndMinute(1, 22))
                    .build();

            sched.scheduleJob(keywordStatJob,kt);
            sched.start();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {

            this.sched.shutdown(false);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
}
