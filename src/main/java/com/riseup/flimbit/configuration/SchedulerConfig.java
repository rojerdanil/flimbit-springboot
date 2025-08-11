package com.riseup.flimbit.configuration;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import com.riseup.flimbit.constant.EntityName;
import com.riseup.flimbit.constant.SchedularNames;
import com.riseup.flimbit.constant.StatusEnum;
import com.riseup.flimbit.service.SystemSettingsService;
import com.riseup.flimbit.workers.MovieMoneyInitiationFailedJob;
import com.riseup.flimbit.workers.MovieMoneyPayoutInitiationJob;

import jakarta.persistence.Entity;

@Configuration
@EnableScheduling
public class SchedulerConfig {
	
	
	Logger logger = LoggerFactory.getLogger(SchedulerConfig.class);

	
	@Autowired
	MovieMoneyPayoutInitiationJob movieDistJob;
	
	@Autowired
	MovieMoneyInitiationFailedJob moneyDistributorFailedJob;
	
	@Autowired
	SystemSettingsService  systemSetting;
	
	 @Bean
	    public ThreadPoolTaskScheduler taskScheduler() {
	        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
	        scheduler.setPoolSize(5);
	        scheduler.setThreadNamePrefix("scheduler-");
	        scheduler.initialize();
	        return scheduler;
	    }
	 
	 @Scheduled(cron = "0 */2 * * * *")
	    public void runPayoutInitiation() {
		 logger.info("Running payout initiation scheduler..." + canRun(SchedularNames.PAYMENT_INITIALIZATION));
	        if(canRun(SchedularNames.PAYMENT_INITIALIZATION))
	           movieDistJob.runPayoutBatch();
	        
	    }

	    // Runs every 5 minutes
	    @Scheduled(cron = "0 */2 * * * *")
	    public void runCheckFailedPayouts() {
	    	logger.info("Running failed payout check scheduler..." +  canRun(SchedularNames.PAYMENT_INITIALIZATION_FAILLED_RECALL));
	       if(canRun(SchedularNames.PAYMENT_INITIALIZATION_FAILLED_RECALL))
	        moneyDistributorFailedJob.runPayoutInitationFailedBatch();
	        // already uses executor inside
	    }
	
	    public boolean canRun(SchedularNames jobName) {
	        String globalStatus = systemSetting.getValue("GLOBAL_SCHEDULER_STATUS", EntityName.SCHEDULER.name());
	        
	        logger.info("GLOBAL_SCHEDULER_STATUS text : " + globalStatus);
	
	        if (globalStatus == null || StatusEnum.ACTIVE.name().equalsIgnoreCase(globalStatus)) {
	            return true;
	        }
	        else if(StatusEnum.INACTIVE.name().equalsIgnoreCase(globalStatus))
	        {
	        	return false;
	        }

	        return Arrays.stream(globalStatus.split(","))
	                 .map(String::trim)
	                 .filter(s -> !s.isEmpty())
	                 .anyMatch(allowed -> allowed.equalsIgnoreCase(jobName.name()));
	    }

}
