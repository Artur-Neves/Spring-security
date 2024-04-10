package com.mballem.curso.security.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class PaginaErrorService {
	public static Map<String, String> paginaDeErro(String status, String error, String message){
		Map<String , String> map = new HashMap<String, String>();
		map.put("status", status);
		map.put("error", error);
		map.put("message", message);
		return map;
	}
}
