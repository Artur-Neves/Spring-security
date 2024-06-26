package com.mballem.curso.security.web.conversor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.mballem.curso.security.domain.Perfil;
@Component
public class PerfisConverter implements Converter<String[], List<Perfil>>{

	@Override
	public List<Perfil> convert(String[] source) {
		List <Perfil> perfis = new ArrayList<>();
	 for (String id : source) {
		 perfis.add(new Perfil(Long.parseLong(id)));
	 }
	 perfis.removeIf(p -> p.getId()==Long.parseLong(""+0));
		return perfis;
	}

}
	