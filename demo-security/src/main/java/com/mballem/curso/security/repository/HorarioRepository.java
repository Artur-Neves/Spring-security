package com.mballem.curso.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mballem.curso.security.domain.Horario;
@Repository
public interface HorarioRepository extends JpaRepository<Horario, Long> {

}
