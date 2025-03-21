package com.example.Kintai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan({ "com.example.Kintai.controller", "com.example.Kintai.service" })
@EnableJpaRepositories(basePackages = "com.example.Kintai.repository")
public class KintaiApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(KintaiApplication.class, args);
	}

}