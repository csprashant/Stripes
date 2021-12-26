package com.nt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ScheduleingApplication {

	public static void main(String[] args) {
		SpringApplication.run(ScheduleingApplication.class, args);
	}

}
