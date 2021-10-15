package br.com.springboot.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
public class BookPost {
	@NotEmpty(message = "The name cannot be null or empty")
	@Schema(description = "This is the book's name", example = "Harry Potter e a Câmara Secreta")
	private String name;
	
	@NotEmpty(message = "The autor cannot be null or empty")
	@Schema(description = "This is the book's autor", example = "J.K. Rowling")
	private String autor;
}
