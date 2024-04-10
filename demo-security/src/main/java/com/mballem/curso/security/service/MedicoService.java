package com.mballem.curso.security.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mballem.curso.security.domain.Medico;
import com.mballem.curso.security.domain.Usuario;
import com.mballem.curso.security.repository.MedicoRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class MedicoService {
	@Autowired
	private MedicoRepository repository;
	@Autowired
	private AgendamentoService serviceAgendamento;
	public Medico findByUsuarioId(Long id) {
		return repository.findByUsuarioId(id).orElse(new Medico(new Usuario(id)));
	}
	@Transactional()
	public Medico salvar(Medico medico) {
		return repository.save(medico);
	}
	@Transactional
	public void editar(Medico medico) {
	  Medico m2 = repository.getReferenceById(medico.getId());
	  if(medico.getEspecialidades()!=null)
	  medico.getEspecialidades().addAll(m2.getEspecialidades());
	  salvar(medico);
	  
	}
	public Medico findByUsuarioEmail(String username) {
		return repository.findByUsuarioEmail(username).orElse(new Medico());
	}
	@Transactional
	public String[] removerEspecialidadeMedico(Long idMedico, Long idEspecialidade) {
		String[] array = new String[2];
		Medico medico = repository.findById(idMedico).orElseThrow(() -> new EntityNotFoundException("Médico não encontrado"));
		if (serviceAgendamento.medicoComEspecialidade(idMedico, idEspecialidade)) {
			System.out.println("caiu aqui !!!!");
			array[0] ="falha";
			array[1] = "Você não pode remover esta especialidade, pois possui consultas agendadas com ela";}
		else {
			medico.getEspecialidades().removeIf(e -> e.getId()==idEspecialidade);	
			array[0] ="sucesso";
			array[1]= "Especialidade removida com sucesso!";
		}
		return array;
	}
	public List<Medico> procurarMedicoAtivoComEspecialidade(String titulo) {
		return repository.findByEspecialidadesTituloContainingIgnoreCaseAndUsuarioAtivoTrue(titulo);
	}
}
