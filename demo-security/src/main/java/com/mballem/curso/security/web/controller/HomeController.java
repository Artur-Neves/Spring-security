package com.mballem.curso.security.web.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mballem.curso.security.service.PaginaErrorService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping({"/",  "/home"})
public class HomeController {

	// abrir pagina home
	@GetMapping()
	public String home() {
		return "home";
	}	
	@GetMapping("/login")
	public String login() {
		return "login";
	}
	@GetMapping("/login-error")
	public String loginError(ModelMap model, HttpServletRequest req) {
	    HttpSession session = req.getSession();
	    // recupera a classe com a exceção que foi lançada
	    String lastException = String.valueOf(session.getAttribute("SPRING_SECURITY_LAST_EXCEPTION"));
	    Map<String , String> b = new HashMap<String, String>();
	    if(lastException.contains(SessionAuthenticationException.class.getName())) {
	    	b.put("alerta", "erro");
			b.put("titulo", "Acesso recusado!");
			b.put("texto", "Você já esta logado em outro dispositivo.");
			b.put("subtexto", "Faça o logout ou espere a sua sessão expirar.");
	    }
	    else {
	    b.put("alerta", "erro");
		b.put("titulo", "credenciais inválidas");
		b.put("texto", "Login ou senha incorretos, tente novamente.");
		b.put("subtexto", "Acesso permitido apenas para cadastros já ativados.");}
		model.addAllAttributes(b);
		return "login";
	}
	@GetMapping("/expired")
	public String sessaoExpirada(ModelMap model, HttpServletRequest req) {
	   
	    Map<String , String> b = new HashMap<String, String>();
	    
	    b.put("alerta", "erro");
		b.put("titulo", "Acesso recusado");
		b.put("texto", "Sua sessão expirou");
		b.put("subtexto", "Você logou em outro dispositivo");
		model.addAllAttributes(b);
		return "login";
	}
	@GetMapping("/acesso-negado")
	public String acessoNegado(ModelMap model, HttpServletResponse response) {
		model.addAllAttributes(PaginaErrorService.paginaDeErro(""+response.getStatus(), "Acesso Negado", "Você não tem acesso a essa parte do sistema."));
		return "error";
	}
}
