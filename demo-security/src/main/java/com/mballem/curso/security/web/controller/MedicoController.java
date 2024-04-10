package com.mballem.curso.security.web.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mballem.curso.security.domain.Medico;
import com.mballem.curso.security.service.MedicoService;
import com.mballem.curso.security.service.UsuarioService;

@Controller
@RequestMapping("/medicos")
public class MedicoController {
	@Autowired
	private MedicoService service;
	@Autowired
	private UsuarioService usuarioService;
	
	@GetMapping({"/dados"})
	public String abrirPorMedico(Medico medico , ModelMap model, @AuthenticationPrincipal User user) {
		if (medico.hasNotId() ) {
			medico = service.findByUsuarioEmail(user.getUsername());
		}
		model.addAttribute(medico);
		return "medico/cadastro";
	}
	@PostMapping("/salvar")
	public String salvarMedico(Medico medico, RedirectAttributes attr, @AuthenticationPrincipal User user){
		if(medico.hasNotId() && medico.getUsuario().hasNotId()) 
			medico.setUsuario(usuarioService.findByEmail(user.getUsername()));
		medico = service.salvar(medico);
		attr.addFlashAttribute("sucesso", "Operaçõa realizada com sucesso!");
		attr.addFlashAttribute("medico", medico);
		return "redirect:/medicos/dados";
	}
	@PostMapping("/editar")
	public String editarMedico(Medico medico, RedirectAttributes attr){
		service.editar(medico);
		attr.addFlashAttribute("sucesso", "Operaçõa realizada com sucesso!");
		attr.addFlashAttribute("medico", medico);
		return "redirect:/medicos/dados";
	}
	
	@GetMapping("/id/{idMedico}/excluir/especializacao/{idEspecialidade}")
	public String removerMedico(Medico medico, @PathVariable("idMedico") Long idMedico, @PathVariable("idEspecialidade") Long idEspecialidade, RedirectAttributes attr){
		String[] array = service.removerEspecialidadeMedico(idMedico, idEspecialidade);
		attr.addFlashAttribute(array[0], array[1]);
		attr.addAttribute("medico", medico);
		return "redirect:/medicos/dados";
	}
	
	@GetMapping("/especialidade/titulo/{titulo}")
	public ResponseEntity<List<Medico>> buscarMedicosPorEspecialidade(@PathVariable("titulo") String titulo){
		return ResponseEntity.ok(service.procurarMedicoAtivoComEspecialidade(titulo));
	}
}
