package com.mballem.curso.security.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mballem.curso.security.datatables.Datatables;
import com.mballem.curso.security.datatables.DatatablesColunas;
import com.mballem.curso.security.domain.Agendamento;
import com.mballem.curso.security.domain.Horario;
import com.mballem.curso.security.repository.AgendamentoRepository;
import com.mballem.curso.security.repository.EspecialidadeRepository;
import com.mballem.curso.security.repository.projection.HistoricoPaciente;
import com.mballem.curso.security.web.Exceptions.AcessoNegadoException;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class AgendamentoService {
	@Autowired
	private AgendamentoRepository repository;
	@Autowired
	private EspecialidadeRepository especialidadeRepository;
	@Autowired
	private PacienteService pacienteService;
	@Autowired
	private Datatables datatables;
	public List<Horario> disponibilidadeMedicoHorario(Long id, LocalDate data) {

		return 	 repository.findHorariosDisponiveis(id, data);
	}
	@Transactional
	public void salvar(Agendamento agendamento, User user) {
		agendamento.setEspecialidade(especialidadeRepository.findByTitulo(agendamento.getEspecialidade().getTitulo()));
		agendamento.setPaciente(pacienteService.findByUsuarioEmail(user.getUsername()));
		repository.save(agendamento);
	}
	@Transactional
	public Map<String, Object> buscarHistoricoPorPacienteEmail(String email, HttpServletRequest req) {
		datatables.setRequest(req);
		datatables.setColunas(DatatablesColunas.AGENDAMENTOS);
		Page<HistoricoPaciente> page = repository.findHistoricoByPacientEmail(email, datatables.getPageable());
		return datatables.getResponse(page);
	}
	@Transactional
	public Object buscarHistoricoPorMedicoEmail(String email, HttpServletRequest req) {
		datatables.setRequest(req);
		datatables.setColunas(DatatablesColunas.AGENDAMENTOS);
		Page<HistoricoPaciente> page = repository.findHistoricoByMedicoEmail(email, datatables.getPageable());
		return datatables.getResponse(page);
	}
	@Transactional
	public Agendamento findById(Long id) {
		return repository.getReferenceById(id);
	}
	@Transactional
	public void editar(Agendamento agendamento, User user) {
		agendamento.setEspecialidade(especialidadeRepository.findByTitulo(agendamento.getEspecialidade().getTitulo()));
		Agendamento ag = findByIdAndEmail(agendamento.getId(), user.getUsername());
		ag.setMedico(agendamento.getMedico());
		ag.setDataConsulta(agendamento.getDataConsulta());
		ag.setHorario(agendamento.getHorario());
		ag.setEspecialidade(agendamento.getEspecialidade());
	}
	public Agendamento findByIdAndEmail(Long id, String email) {
		return repository.buscarUsuarioporsenhaEEmail(id, email)
				.orElseThrow(() -> new AcessoNegadoException("Acesso negado ao usuário: "+email));
	}
	@Transactional
	public void removeConsulta(Long id) {
		repository.deleteById(id);
	}
	
	public boolean medicoComEspecialidade(Long idMedico, Long idEspecialidade ) {
		return repository.existsByMedicoIdAndEspecialidadeId(idMedico, idEspecialidade);
	}

}
