package com.mballem.curso.security.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {
	@Autowired
	private JavaMailSender mailSender;
	@Autowired
	private SpringTemplateEngine template;
	
	public void enviarPedidoConfirmacaoDeCadastro(String destinatario, String codigo) throws MessagingException {
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, "UTF-8");
	    Context context = new Context();
	    
	    context.setVariable("titulo", "Bem vido a clinica Spring Security");
	    context.setVariable("texto", "Precisamos que confirmem seu cadastro, clicando no link abaixo");
	    context.setVariable("linkConfirmacao", "http://localhost:8080/u/confirmacao/cadastro?codigo="+codigo);
	    String html = template.process("email/confirmacao", context);
	    
	    helper.setTo(destinatario);
	    helper.setText(html, true);
	    helper.setSubject("Email de confirmação de cadastro");
	    helper.setFrom("nao-responder@gmail.com");
	    
	    helper.addInline("logo", new ClassPathResource("/static/image/spring-security.png"));
	    mailSender.send(message);
	}
	
	public void enviarPedidoRedefinirSenha(String destinatario, String verificador) throws MessagingException {
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, "UTF-8");
	    Context context = new Context();
	    
	    context.setVariable("titulo", "Bem vido a clinica Spring Security");
	    context.setVariable("texto", "Precisamos que confirmem seu cadastro, clicando no link abaixo");
	    context.setVariable("verificador", verificador);
	    String html = template.process("email/confirmacao", context);
	    
	    helper.setTo(destinatario);
	    helper.setText(html, true);
	    helper.setSubject("Redefinição de senha");
	    helper.setFrom("nao-responder@gmail.com");
	    
	    helper.addInline("logo", new ClassPathResource("/static/image/spring-security.png"));
	    mailSender.send(message);
	}
}
 