package com.example.filter;

import com.example.filter.web.filter.LoggingWebFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.server.WebFilter;

@SpringBootApplication
@EnableWebFlux
public class FilterApplication {

	public static void main(String[] args) {
		SpringApplication.run(FilterApplication.class, args);
	}

      @Bean
      @Order(Ordered.HIGHEST_PRECEDENCE)
      public WebFilter loggingWebFilter() {
        return new LoggingWebFilter();
      }

}
