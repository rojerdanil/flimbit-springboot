package com.riseup.flimbit.configuration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExecutorConfig {
	@Bean(name = "sharedExecutor")
    public ExecutorService payoutExecutorService() {
        return Executors.newFixedThreadPool(2); // 5 threads
    }
	

}
