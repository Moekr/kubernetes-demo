package com.moekr.kubernetes.demo;

import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

@CommonsLog
@SpringBootApplication
@EnableScheduling
public class Application extends SpringApplication {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public KubernetesClient kubernetesClient() {
		KubernetesClient client = new DefaultKubernetesClient();
		log.info("Connected to kubernetes api server with api version: " + client.getApiVersion());
		return client;
	}

	@Bean
	public ScheduledExecutorService scheduledExecutor() {
		return Executors.newScheduledThreadPool(2 * Runtime.getRuntime().availableProcessors());
	}

	@Bean
	public TaskExecutor taskExecutor() {
		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
		taskExecutor.setCorePoolSize(2 * Runtime.getRuntime().availableProcessors());
		taskExecutor.setMaxPoolSize(2 * Runtime.getRuntime().availableProcessors());
		taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
		taskExecutor.initialize();
		return taskExecutor;
	}
}
