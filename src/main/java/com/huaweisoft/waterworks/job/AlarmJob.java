package com.huaweisoft.waterworks.job;

import java.util.Date;

import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AlarmJob implements Job {

    private static final Logger LOGGER = LoggerFactory.getLogger(AlarmJob.class);
    
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDetail jobDetail = context.getJobDetail();
        LOGGER.info("******节点一*******定时任务开始执行执行，name:{}，time:{}",jobDetail.getKey().getName(),new Date());
        
        
    }

}
