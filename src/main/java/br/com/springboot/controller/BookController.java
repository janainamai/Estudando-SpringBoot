package br.com.springboot.controller;

import br.com.springboot.domain.Book;
import br.com.springboot.dto.BookPost;
import br.com.springboot.dto.BookPut;
import br.com.springboot.service.BookService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/books")
@AllArgsConstructor
@Log4j2
public class BookController {
	 
	private BookService bookService;

	@GetMapping( path = "/listPageable")
	public ResponseEntity<Page<Book>> listPageable(Pageable pageable) {
		return ResponseEntity.ok(bookService.listPageable(pageable));
	}

	@GetMapping()
	public ResponseEntity<List<Book>> list() {
		return ResponseEntity.ok(bookService.listAll());
	}
	
	@GetMapping(path = "/find/{name}")
	public ResponseEntity<List<Book>> findByName(@PathVariable String name) {
		return ResponseEntity.ok(bookService.findByName(name));
	}
	
	@GetMapping(path = "/find")
	public ResponseEntity<List<Book>> findByAutor(@RequestParam String autor) {
		return ResponseEntity.ok(bookService.findByAutor(autor));
	}
	
	@GetMapping(path = "/{id}")
	public ResponseEntity<Book> findById(@PathVariable Long id) {
		return ResponseEntity.ok(bookService.findByIdOrThrowBadRequest(id));
	}

	@GetMapping(path = "by-id/{id}")
	public ResponseEntity<Book> findByIdAuthenticationPrincipal(@PathVariable Long id,
																@AuthenticationPrincipal UserDetails userDetails) {
		log.info(userDetails);
		return ResponseEntity.ok(bookService.findByIdOrThrowBadRequest(id));
	}
	
	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Book> save(@RequestBody @Valid BookPost book) {
		return new ResponseEntity<>(bookService.save(book), HttpStatus.CREATED);
	}
	
	@DeleteMapping(path = "/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		bookService.delete(id);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	@PutMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Void> replace(@RequestBody BookPut book) {
		bookService.replace(book);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}
