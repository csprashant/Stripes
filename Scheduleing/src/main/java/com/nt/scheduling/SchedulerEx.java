package com.nt.scheduling;

import java.util.Date;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SchedulerEx {
	
	@Scheduled(cron="*/5 * * * * *")
	public void generateReport() {
		System.out.println("Report -->"+System.currentTimeMillis());	
	}

}
