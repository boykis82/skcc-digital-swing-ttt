package org.caltech.miniswing.plmserver;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.Executors;

@SpringBootApplication
@Slf4j
@ComponentScan("org.caltech")
public class PlmServerApplication {
	private final Integer connectionPoolSize;

	@Autowired
	public PlmServerApplication(@Value("${spring.datasource.maximum-pool-size:5}") Integer connectionPoolSize) {
		this.connectionPoolSize = connectionPoolSize;
	}

	@Bean
	public Scheduler jdbcScheduler() {
		log.info("Creates a jdbcScheduler with connectionPoolSize = " + connectionPoolSize);
		return Schedulers.fromExecutor(
				Executors.newFixedThreadPool(connectionPoolSize)
		);
	}

	public static void main(String[] args) {
		SpringApplication.run(PlmServerApplication.class, args);
	}

}
