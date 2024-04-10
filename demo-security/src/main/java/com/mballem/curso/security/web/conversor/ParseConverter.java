package com.mballem.curso.security.web.conversor;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;


public class ParseConverter {
	public static LocalDate transformarEmLocalDate(String data) {
		try {
		return LocalDate.parse(data);
		}
		catch (DateTimeParseException e) {
			 throw new RuntimeException(e.getMessage());
		}
	}
}
