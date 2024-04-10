package com.mballem.curso.security.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mballem.curso.security.datatables.Datatables;
import com.mballem.curso.security.datatables.DatatablesColunas;
import com.mballem.curso.security.domain.Especialidade;
import com.mballem.curso.security.repository.EspecialidadeRepository;

import jakarta.servlet.http.HttpServletRequest;



@Service
public class EspecialidadeService {
	@Autowired
	private EspecialidadeRepository repository;
	@Autowired
	private Datatables dataTables;
	
	@Transactional(readOnly = false)
	public void salvar(Especialidade especialidade) {
		repository.save(especialidade);
	}
	@Transactional
	public Map<String, Object> buscarEspecialidades(HttpServletRequest req){
		dataTables.setRequest(req);
		dataTables.setColunas(DatatablesColunas.ESPECIALIDADES);
		Page<?> page = dataTables.getSearch().isEmpty() 
				? repository.findAll(dataTables.getPageable())
				: repository.findAllByTituloContainingIgnoreCase(dataTables.getSearch(), dataTables.getPageable());
		return dataTables.getResponse(page);
	}
	public Especialidade findById(Long id) {
		Optional<Especialidade> especialidade = repository.findById(id);
		return especialidade.isPresent() ? especialidade.get() : null;
	}
	public void deleteById(Long id) {
		repository.deleteById(id);
	}
	public List<String> buscarEspecialidadePorTermo(String termo) {
		return repository.findByTermo(termo);
	}
	public Set<Especialidade> findByTitulos(String[] source) {
		return repository.findByTitulos(source);
	}
	@Transactional
	public Map<String, Object> exibirEspecialidadePorUsuario(Long id, HttpServletRequest req) {
		dataTables.setRequest(req);
		dataTables.setColunas(DatatablesColunas.ESPECIALIDADES);
		Page<Especialidade> especialidade = repository.findAllByMedicosId(id, dataTables.getPageable());
		return dataTables.getResponse(especialidade);
	}
	

}
