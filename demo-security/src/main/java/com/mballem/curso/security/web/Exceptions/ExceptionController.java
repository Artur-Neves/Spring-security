package com.mballem.curso.security.web.Exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.ModelAndView;

import com.mballem.curso.security.service.PaginaErrorService;

import jakarta.persistence.EntityNotFoundException;

@RestControllerAdvice
public class ExceptionController {
@ExceptionHandler(EntityNotFoundException.class)
ModelAndView nullponterException() {
	ModelAndView model = new ModelAndView("error");
	model.addAllObjects(PaginaErrorService.paginaDeErro("404", "Operação não pode ser realizada", "Entidade não encontrada"));
	return model;
}
@ExceptionHandler(AcessoNegadoException.class)
ModelAndView acessoNegadoExcetion( AcessoNegadoException e) {
	ModelAndView model = new ModelAndView("error");
	model.addAllObjects(PaginaErrorService.paginaDeErro("403", "Acesso Negado!",e.getMessage()));
	return model;
}
}
