package com.mballem.curso.security.web.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mballem.curso.security.domain.Agendamento;
import com.mballem.curso.security.domain.Horario;
import com.mballem.curso.security.domain.PerfilTipo;
import com.mballem.curso.security.service.AgendamentoService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("agendamentos")
public class AgendamentoController {
	@Autowired
	private AgendamentoService service;
	@PreAuthorize("hasAnyAuthority('PACIENTE', 'MEDICO')")
	@GetMapping("/agendar")
	public String agendarConsulta(Agendamento agendamento) {
		return "agendamento/cadastro";
	}
	@GetMapping("/horario/medico/{idMedico}/data/{data}")
	@PreAuthorize("hasAnyAuthority('PACIENTE', 'MEDICO')")
	public ResponseEntity<List<Horario>> horariosAgendamento(
			@PathVariable("idMedico") Long id, 
			@PathVariable("data") @DateTimeFormat(iso = ISO.DATE)LocalDate data){
		return ResponseEntity.ok(service.disponibilidadeMedicoHorario(id, data));
	}
	@PostMapping("/salvar")
	@PreAuthorize("hasAuthority('PACIENTE')")
	public String agendarHorario(Agendamento agendamento, 
			RedirectAttributes attr,
			@AuthenticationPrincipal User user) {
		
		service.salvar(agendamento, user);
		attr.addFlashAttribute("sucesso", "Agendamento realizado com sucesso!");
		return "redirect:/agendamentos/agendar";
	}
	@GetMapping({"/historico/paciente", "/historico/consultas"})
	@PreAuthorize("hasAnyAuthority('PACIENTE', 'MEDICO')")
	public String historico() {
		return "agendamento/historico-paciente";
	}
	
	@GetMapping("/datatables/server/historico")
	@PreAuthorize("hasAnyAuthority('PACIENTE', 'MEDICO')")
	public ResponseEntity mostrarAgendamentos( HttpServletRequest req, @AuthenticationPrincipal User user) {
		if (user.getAuthorities().contains( new SimpleGrantedAuthority(PerfilTipo.PACIENTE.getDesc()))) {
			return ResponseEntity.ok(service.buscarHistoricoPorPacienteEmail(user.getUsername(), req));
		}
		if (user.getAuthorities().contains( new SimpleGrantedAuthority(PerfilTipo.MEDICO.getDesc()))) {
			return ResponseEntity.ok(service.buscarHistoricoPorMedicoEmail(user.getUsername(), req));
		}
		return ResponseEntity.notFound().build();
	}
	@GetMapping("/editar/consulta/{id}")
	@PreAuthorize("hasAnyAuthority('PACIENTE', 'MEDICO')")
	public String preEditarConsultaPaciente(
			         @PathVariable("id") Long id,
			         ModelMap model, @AuthenticationPrincipal User user) {
		Agendamento agendamento = service.findByIdAndEmail(id, user.getUsername());
		model.addAttribute("agendamento", agendamento);
		return "agendamento/cadastro";
	}
	
	@PostMapping("/editar")
	@PreAuthorize("hasAnyAuthority('PACIENTE', 'MEDICO')")
	public String editarConsulta(Agendamento agendamento, 
			                    RedirectAttributes attr,
			                    @AuthenticationPrincipal User user) {
		System.out.println(agendamento.getMedico().getId());
		service.editar(agendamento, user);
		attr.addFlashAttribute("sucesso", "Agendamento editado com sucesso!");
		return "redirect:/agendamentos/agendar";
	}
	
	@GetMapping("/excluir/consulta/{id}")
	@PreAuthorize("hasAuthority('PACIENTE')")
	public String excluirConsulta(@PathVariable("id") Long id, RedirectAttributes attr) {
		service.removeConsulta(id);
		attr.addFlashAttribute("sucesso", "Consulta realizada com sucesso.");
		return "redirect:/agendamentos/historico/paciente";
	}
}
