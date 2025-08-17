package com.jp.Kintai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Tomcat起動時にコントローラー等をスキャンするクラス
 */
@SpringBootApplication
@ComponentScan({ "com.jp.Kintai.controller", "com.jp.Kintai.service", "com.jp.Kintai.util" })
@EnableJpaRepositories(basePackages = "com.jp.Kintai.repository")
public class KintaiApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(KintaiApplication.class, args);
	}

}