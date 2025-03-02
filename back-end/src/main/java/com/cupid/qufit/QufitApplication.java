package com.cupid.qufit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
//@EnableElasticsearchRepositories(basePackages = "com.cupid.qufit.global.utils.elasticsearch")
@EnableJpaRepositories(basePackages = "com.cupid.qufit.domain")
public class QufitApplication {

	public static void main(String[] args) {
		SpringApplication.run(QufitApplication.class, args);
	}

}
