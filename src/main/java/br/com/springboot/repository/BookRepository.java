package br.com.springboot.repository;

import br.com.springboot.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {
	
	List<Book> findByName(String name);
	
	List<Book> findByAutor(String autor);
	
}
