package com.mballem.curso.security.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import com.mballem.curso.security.domain.Usuario;
@Repository
public interface RepositoryUser extends JpaRepository<Usuario, Long> {

	Usuario findByEmail(String username);

	Page<?> findAllByEmailContainingIgnoreCaseOrPerfisDescContainingIgnoreCase(String email,String perfil,  Pageable pageable);

	Optional<Usuario> findByEmailAndAtivoTrue(String email);

}
