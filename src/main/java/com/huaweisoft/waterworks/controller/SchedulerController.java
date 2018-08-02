package com.huaweisoft.waterworks.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.huaweisoft.waterworks.job.AlarmJob;
import com.huaweisoft.waterworks.model.GetJobResp;
import com.huaweisoft.waterworks.service.JobService;

@RestController
@RequestMapping("/timer-api/schedule")
public class SchedulerController {

    // 加入Qulifier注解，通过名称注入bean
    @Autowired
    @Qualifier("scheduler")
    private Scheduler scheduler;

    @Autowired
    private JobService jobService;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SchedulerController.class);

    /**
     * 添加任务
     * @param jobClassName
     * @param jobGroupName
     * @param cronExpression
     * @throws Exception
     */
    @PostMapping(value = "/add_job")
    public void addJob(@RequestParam(value = "jobClassName") String jobClassName, 
                                               @RequestParam(value = "jobGroupName") String jobGroupName,
                                               @RequestParam(value = "cronExpression") String cronExpression) {

        LOGGER.info("进入添加任务接口");
        
        // 启动调度器
        try {
            scheduler.start();
        }
        catch (SchedulerException e1) {
            LOGGER.error("调度器启动失败，异常：{}",e1);
        }
        
        // 构建job信息
        JobDetail jobDetail = JobBuilder.newJob(AlarmJob.class).withIdentity(jobClassName, jobGroupName).build();
        
        // 表达式调度构建器(即任务执行的时间)
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);
        
        // 按新的cronExpression表达式构建一个新的trigger
        CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(jobClassName, jobGroupName).withSchedule(scheduleBuilder).build();
        
        try {
            scheduler.scheduleJob(jobDetail, trigger);
            
            LOGGER.info("任务添加成功，jobClassName:{}，cronExpression:{}",jobClassName,cronExpression);
            
        }
        catch (SchedulerException e) {
            LOGGER.error("任务创建失败，异常:{}",e);
        }
    }

    /**
     * 暂停任务
     * @param jobClassName
     * @param jobGroupName
     * @throws Exception
     */
    @PostMapping(value = "/pause_job")
    public void pauseJob(@RequestParam(value = "jobClassName") String jobClassName,
                                                    @RequestParam(value = "jobGroupName") String jobGroupName) throws Exception {
        scheduler.pauseJob(JobKey.jobKey(jobClassName, jobGroupName));
        LOGGER.info("暂停任务成功，jobClassName：{}",jobClassName);
    }

    /**
     * 恢复任务
     * @param jobClassName
     * @param jobGroupName
     * @throws Exception
     */
    @PostMapping(value = "/resume_job")
    public void resumeJob(@RequestParam(value = "jobClassName") String jobClassName,
                                                        @RequestParam(value = "jobGroupName") String jobGroupName) throws Exception {
        scheduler.resumeJob(JobKey.jobKey(jobClassName, jobGroupName));
        LOGGER.info("恢复任务成功，jobClassName：{}",jobClassName);
    }

   /**
    * 更改任务
    * @param jobClassName
    * @param jobGroupName
    * @param cronExpression
    * @throws Exception
    */
    @PostMapping(value = "/update_job")
    public void updateJob(@RequestParam(value = "jobClassName") String jobClassName,
                                                              @RequestParam(value = "jobGroupName") String jobGroupName,
                                                              @RequestParam(value = "cronExpression") String cronExpression) {
        try {
            TriggerKey triggerKey = TriggerKey.triggerKey(jobClassName, jobGroupName);
            // 表达式调度构建器
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);
            
            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
            
            // 按新的cronExpression表达式重新构建trigger
            trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();
            
            // 按新的trigger重新设置job执行
            scheduler.rescheduleJob(triggerKey, trigger);
            
            LOGGER.info("更改任务成功，jobClassName：{}",jobClassName);
        }
        catch (SchedulerException e) {
            LOGGER.error("更改任务失败，异常：{}",e);
        }
    }

    /**
     * 删除任务
     * @param jobClassName
     * @param jobGroupName
     * @throws Exception
     */
    @PostMapping(value = "/delete_job")
    public void deleteJob(@RequestParam(value = "jobClassName") String jobClassName,
                                                    @RequestParam(value = "jobGroupName") String jobGroupName) throws Exception {
        scheduler.pauseTrigger(TriggerKey.triggerKey(jobClassName, jobGroupName));
        scheduler.unscheduleJob(TriggerKey.triggerKey(jobClassName, jobGroupName));
        scheduler.deleteJob(JobKey.jobKey(jobClassName, jobGroupName));
        LOGGER.info("删除任务成功，jobClassName：{}",jobClassName);
    }

    @GetMapping(value="/query_job")
    public Map<String, Object> queryJob(@RequestParam(value="pageNum")Integer pageNum, 
                                                                                      @RequestParam(value="pageSize")Integer pageSize) 
    {           
        List<GetJobResp> list = jobService.getJobAndTriggerDetails(pageNum, pageSize);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("JobAndTrigger", list);
        map.put("number", list.size());
        return map;
    }
}
