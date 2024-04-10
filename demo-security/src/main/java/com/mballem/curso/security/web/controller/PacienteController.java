package com.mballem.curso.security.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mballem.curso.security.domain.Paciente;
import com.mballem.curso.security.domain.Usuario;
import com.mballem.curso.security.service.PacienteService;
import com.mballem.curso.security.service.UsuarioService;

@Controller
@RequestMapping("/pacientes")
public class PacienteController {
	@Autowired
	private PacienteService service;
	@Autowired
	private UsuarioService usuarioService;
	@GetMapping("/dados")
	public String dados (Paciente paciente, ModelMap map, @AuthenticationPrincipal User user) {
		if(paciente.hasNotId())	
		paciente = service.findByUsuarioEmail(user.getUsername());
		map.addAttribute("paciente", paciente);
		return "/paciente/cadastro";
	}
	@PostMapping("/salvar")
	public String salvar(Paciente paciente, 
						RedirectAttributes attr, 
						@AuthenticationPrincipal User user,
						@RequestParam("senha") String senha) {
		Usuario usuario = usuarioService.findByEmail(user.getUsername());
	
		if (usuarioService.isSenhaCorreta(senha, usuario.getPassword())) {
		paciente.setUsuario(usuario);
		paciente = service.savePaciente(paciente);
		attr.addFlashAttribute("sucesso","paciente cadastrado com sucesso");}
		else {
		attr.addFlashAttribute("falha","A senha do usuario não é a mesma digitada");}
		attr.addFlashAttribute("paciente", paciente);
		return "redirect:/pacientes/dados";
	}
	@PostMapping("/editar")
	public String editar(Paciente paciente, 
						RedirectAttributes attr, 
						@AuthenticationPrincipal User user,
						@RequestParam("senha") String senha) {
		Usuario usuario = usuarioService.findByEmail(user.getUsername());
	
		if (usuarioService.isSenhaCorreta(senha, usuario.getPassword())) {
		paciente = service.editPaciente(paciente);
		attr.addFlashAttribute("sucesso","paciente atualizado com sucesso");}
		else {
		attr.addFlashAttribute("falha","A senha do usuario não é a mesma digitada");}
		attr.addFlashAttribute("paciente", paciente);
		return "redirect:/pacientes/dados";
	}
	
}
