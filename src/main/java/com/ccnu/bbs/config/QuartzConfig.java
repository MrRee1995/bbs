package com.ccnu.bbs.config;

import com.ccnu.bbs.task.BBSTask;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfig {

    private static final String BBS_TASK_IDENTITY = "BBSTaskQuartz";

    @Bean
    public JobDetail quartzDetail(){
        return JobBuilder.newJob(BBSTask.class).withIdentity(BBS_TASK_IDENTITY).storeDurably().build();
    }

    @Bean
    public Trigger quartzTrigger(){
        SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule()
//                .withIntervalInSeconds(10)  //设置时间周期单位秒
                  .withIntervalInSeconds(15)//15秒执行一次
                .repeatForever();
        return TriggerBuilder.newTrigger().forJob(quartzDetail())
                .withIdentity(BBS_TASK_IDENTITY)
                .withSchedule(scheduleBuilder)
                .build();
    }
}
