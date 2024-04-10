package com.mballem.curso.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.mail.javamail.JavaMailSender;

import com.mballem.curso.security.service.EmailService;

@SpringBootApplication
public class DemoSecurityApplication implements CommandLineRunner{
	@Autowired
	JavaMailSender sender;
	@Autowired
	EmailService emailService;
	public static void main(String[] args) {
		SpringApplication.run(DemoSecurityApplication.class, args);
	}
	@Override
	public void run(String... args) throws Exception {
		//emailService.enviarPedidoConfirmacaoDeCadastro("nevesdev.ti@gmail.com", "0024922");
	}

	
}
