package com.nttdata.nge.example.server.control;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Profile("prod")
public class ProdConfiguration {

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry config) {
				
				config.addMapping("/**").allowedOrigins("https://sample.nge.altemista.cloud")
						.allowedMethods("GET", "POST", "PUT", "DELETE").allowCredentials(false).maxAge(3600);
			}
		};
	}

}