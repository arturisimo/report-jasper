package org.apz.report;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {"org.apz.report.service","org.apz.report.controller"})
@EntityScan(basePackages = {"org.apz.report.model"})
@EnableJpaRepositories(basePackages = {"org.apz.report.repository"})
public class ReportApp {

	public static void main(String[] args) {
		SpringApplication.run(ReportApp.class, args);
	}

}
