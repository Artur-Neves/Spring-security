package com.mballem.curso.security.infra.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SpringConfig {
	@Bean
    SecurityFilterChain configuration(HttpSecurity  http) throws Exception {
	  return http.csrf( csrf -> csrf.disable())
			  .authorizeHttpRequests(req ->{
				  req.requestMatchers("/").hasRole("USER");
			  })
			  .build();
			  
  }
}
