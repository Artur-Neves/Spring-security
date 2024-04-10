package com.mballem.curso.security.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.mballem.curso.security.domain.Especialidade;
@Repository
public interface EspecialidadeRepository extends JpaRepository<Especialidade, Long> {


	Page<?> findAllByTituloContainingIgnoreCase(String search, Pageable pageable);
	@Query("select e.titulo from Especialidade e where Lower(e.titulo) like :termo%")
	List<String> findByTermo(String termo);
	
	@Query("select e from Especialidade e where e.titulo IN :source")
	Set<Especialidade> findByTitulos(String[] source);
	Page<Especialidade> findAllByMedicosId(Long id, Pageable pageable);
	Especialidade findByTitulo(String titulo);

}
