package com.mballem.curso.security.infra.config;

import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.RememberMeAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices.RememberMeTokenAlgorithm;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import com.mballem.curso.security.domain.PerfilTipo;

@Configuration
@EnableMethodSecurity
public class SpringConfig {
	private static final String ADMIN = PerfilTipo.ADMIN.getDesc();
	private static final String MEDICOS = PerfilTipo.MEDICO.getDesc();
	private static final String PACIENTES = PerfilTipo.PACIENTE.getDesc();
	@Bean
    SecurityFilterChain configuration(HttpSecurity  http, RememberMeServices rememberMeServices) throws Exception {
	  return http
			  .authorizeHttpRequests(req ->{
				  req.requestMatchers("/webjars/**", "/css/**", "/image/**", "/js/**").permitAll();
				  req.requestMatchers("/", "/home", "/expired").permitAll();
				  req.requestMatchers("/u/novo/cadastro", "/u/cadastro/realizado", "/u/cadastro/paciente/salvar").permitAll();
				  req.requestMatchers("/u/confirmacao/cadastro", "/u/p/**").permitAll();
				  
				  //acessos privados admin
				  req.requestMatchers("/medicos/especialidade/titulo/*").hasAuthority(PACIENTES);
				  req.requestMatchers("/u/editar/senha", "/u/confirmar/senha").hasAnyAuthority(PACIENTES, MEDICOS);
				  req.requestMatchers("/u/**", "/especialidades").hasAuthority(ADMIN);
				  req.requestMatchers("/medicos/dados", "/medicos/salvar", "/medicos/editar", "/especialidades/datatables/server/medico/*").hasAnyAuthority(ADMIN, MEDICOS);
				  req.requestMatchers("/especialidade/titulo/**").hasAnyAuthority(ADMIN, MEDICOS, PACIENTES);
				  req.requestMatchers("/medicos/**").hasAuthority(MEDICOS);
				  
				  
				  req.requestMatchers("/pacientes/**").hasAuthority(PACIENTES);
				  req.anyRequest().authenticated();
				
			  })
			  
			  .formLogin(login->{
				login.loginPage("/login")
				.defaultSuccessUrl("/", true)
				.failureUrl("/login-error")
				.permitAll();  
			  })
			  .rememberMe(me ->{
				  me.rememberMeServices(rememberMeServices);
				
			  })
			  .logout(l ->{
				  l.logoutSuccessUrl("/")
				  .deleteCookies("JSESSIONID");
			  })
			  .exceptionHandling(e ->{
				  e.accessDeniedPage("/acesso-negado");
			  })
			  .sessionManagement(session -> {session
			            .maximumSessions(1)
			            .maxSessionsPreventsLogin(false)
			            .expiredUrl("/expired");
			            
			  }
			        )
			  .build()
			 
			  ;
			  
  }
	@Bean
	PasswordEncoder bcys() {
		return new BCryptPasswordEncoder();
	}
	@Bean
	RememberMeServices rememberMeServices(UserDetailsService userDetailsService) {
		RememberMeTokenAlgorithm encodingAlgorithm = RememberMeTokenAlgorithm.SHA256;
		TokenBasedRememberMeServices rememberMe = new TokenBasedRememberMeServices("123", userDetailsService, encodingAlgorithm);
		rememberMe.setMatchingAlgorithm(RememberMeTokenAlgorithm.MD5);
		return rememberMe;
	}
	@Bean
    HttpSessionEventPublisher httpSessionEventPublisher() {
	    return new HttpSessionEventPublisher();
	}
	
	
}
