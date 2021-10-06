package br.com.springboot.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
public class BookPost {
	@NotEmpty(message = "The name cannot be null or empty")
	private String name;
	
	@NotEmpty(message = "The autor cannot be null or empty") 
	private String autor;
}
