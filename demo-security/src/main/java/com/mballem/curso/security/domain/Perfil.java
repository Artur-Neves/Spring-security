package com.mballem.curso.security.domain;

import org.springframework.security.core.GrantedAuthority;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name = "perfis")
public class Perfil extends AbstractEntity implements GrantedAuthority{
	
	@Column(name = "descricao", nullable = false, unique = true)
	private String desc;
	
	public Perfil() {
		super();
	}

	public Perfil(Long id) {
		super.setId(id);
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	@Override
	public String getAuthority() {
		return desc;
	}
}
