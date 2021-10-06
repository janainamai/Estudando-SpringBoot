package br.com.springboot.service;

import br.com.springboot.domain.Book;
import br.com.springboot.dto.BookPost;
import br.com.springboot.dto.BookPut;
import br.com.springboot.dto.ConvertDTO;
import br.com.springboot.exception.BadRequestException;
import br.com.springboot.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {
	private final BookRepository bookRepository;
	
	public Page<Book> listPageable(Pageable pageable) {
		return bookRepository.findAll(pageable);
	}

	public List<Book> listAll() {
		return bookRepository.findAll();
	}
	
	public List<Book> findByName(String name) {
		return bookRepository.findByName(name);
	}

	public List<Book> findByAutor(String name) {
		return bookRepository.findByAutor(name);
	}
	
	public Book findByIdOrThrowBadRequest(Long id) {
		return bookRepository.findById(id)
				.orElseThrow(() -> new BadRequestException("Book not Found"));
	}

	public Book save(BookPost bookPost) {
		Book book = ConvertDTO.toEntity(bookPost);
		return bookRepository.save(book);
	}

	public void delete(Long id) {
		bookRepository.delete(findByIdOrThrowBadRequest(id));
	}

	public void replace(BookPut bookPut) {
		findByIdOrThrowBadRequest(bookPut.getId());
		Book book = ConvertDTO.toEntity(bookPut);
		bookRepository.save(book); 
	}
}
