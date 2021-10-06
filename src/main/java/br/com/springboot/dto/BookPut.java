package br.com.springboot.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookPut {
	private Long id;
	private String name;
	private String autor;
}
