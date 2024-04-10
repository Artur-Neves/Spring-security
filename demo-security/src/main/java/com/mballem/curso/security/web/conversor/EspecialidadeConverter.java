package com.mballem.curso.security.web.conversor;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.mballem.curso.security.domain.Especialidade;
import com.mballem.curso.security.service.EspecialidadeService;

@Component
public class EspecialidadeConverter implements Converter<String [], Set<Especialidade>>{
	@Autowired
	private EspecialidadeService service;

	@Override
	public Set<Especialidade> convert(String[] source) {
		Set<Especialidade> especialidade = new HashSet<>();
		System.out.println(source.length);
		if(source!=null && source.length>0) {
			especialidade.addAll(service.findByTitulos(source));
			especialidade.forEach(System.out::println);
		}
		
		return especialidade;
	}
}
