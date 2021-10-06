package br.com.springboot.dto;

import br.com.springboot.domain.Book;

public class ConvertDTO {
	public static Book toEntity(BookPost dto) {
		if (dto == null) {
			return null;
		}

		return Book.builder().name(dto.getName()).autor(dto.getAutor()).build();
	}
	
	public static Book toEntity(BookPut dto) {
		if (dto == null) {
			return null;
		}

		return Book.builder().id(dto.getId()).name(dto.getName()).autor(dto.getAutor()).build();
	}
}
