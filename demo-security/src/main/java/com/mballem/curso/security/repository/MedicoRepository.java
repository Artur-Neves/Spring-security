package com.mballem.curso.security.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.mballem.curso.security.domain.Medico;
@Repository
public interface MedicoRepository extends JpaRepository<Medico, Long> {
	Optional<Medico> findByUsuarioId(Long id);

	Optional<Medico> findByUsuarioEmail(String username);

	List<Medico> findByEspecialidadesTituloContainingIgnoreCaseAndUsuarioAtivoTrue(String titulo);
}
