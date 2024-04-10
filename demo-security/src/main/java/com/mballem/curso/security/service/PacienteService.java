package com.mballem.curso.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mballem.curso.security.domain.Paciente;
import com.mballem.curso.security.domain.Usuario;
import com.mballem.curso.security.repository.PacienteRepository;

@Service
public class PacienteService {
	@Autowired
	private PacienteRepository repository;
	
	@Transactional(readOnly = true)
	public Paciente findByUsuarioEmail(String email) {
		Paciente paciente = repository.findByUsuarioEmail(email).orElse(new Paciente());
		if(paciente.getUsuario() == null) {
			paciente.setUsuario(new Usuario( email));
		}
		
	return paciente;
		
	}
	@Transactional
	public Paciente savePaciente(Paciente paciente) {
		return repository.save(paciente);
		
	}
	@Transactional
	public Paciente editPaciente(Paciente paciente) {
		Paciente pacienteCadastrado = repository.getReferenceById(paciente.getId());
		paciente.setUsuario(pacienteCadastrado.getUsuario());
		pacienteCadastrado = paciente; 
		return pacienteCadastrado;
		
	}
}
