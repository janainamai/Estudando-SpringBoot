package br.com.springboot.repository;

import br.com.springboot.domain.Book;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import br.com.springboot.utils.BookCreator;

import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Optional;

@DataJpaTest
@DisplayName("Tests for Book Repository")
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Test
    @DisplayName("Save persists book when successful")
    void save_PersistBook_WhenSuccessful() {
        Book bookToBeSaved = BookCreator.createBookToBeSaved();
        Book bookSaved = this.bookRepository.save(bookToBeSaved);

        Assertions.assertThat(bookSaved).isNotNull();
        Assertions.assertThat(bookSaved.getId()).isNotNull();
        Assertions.assertThat(bookSaved.getName()).isEqualTo(bookToBeSaved.getName());
        Assertions.assertThat(bookSaved.getAutor()).isEqualTo(bookToBeSaved.getAutor());
    }

    @Test
    @DisplayName("Save updates book when successful")
    void save_UpdatesBook_WhenSuccessful() {
        Book bookToBeSaved = BookCreator.createBookToBeSaved();
        Book bookSaved = this.bookRepository.save(bookToBeSaved);

        bookSaved.setName("Another");
        Book bookUpdated = this.bookRepository.save(bookSaved);

        Assertions.assertThat(bookUpdated).isNotNull();
        Assertions.assertThat(bookUpdated.getId()).isNotNull();
        Assertions.assertThat(bookUpdated.getName()).isEqualTo(bookSaved.getName());
        Assertions.assertThat(bookUpdated.getAutor()).isEqualTo(bookSaved.getAutor());
    }

    @Test
    @DisplayName("Delete removes book when successful")
    void delete_RemovesBook_WhenSuccessful() {
        Book bookToBeSaved = BookCreator.createBookToBeSaved();
        Book bookSaved = this.bookRepository.save(bookToBeSaved);

        this.bookRepository.delete(bookSaved);
        Optional<Book> bookOptional = this.bookRepository.findById(bookSaved.getId());
        Assertions.assertThat(bookOptional).isEmpty();
    }

    @Test
    @DisplayName("Find by name returns book when successful")
    void findByName_ReturnsBook_WhenSuccessful() {
        Book bookToBeSaved = BookCreator.createBookToBeSaved();
        Book bookSaved = this.bookRepository.save(bookToBeSaved);

        String name = bookSaved.getName();
        List<Book> books = this.bookRepository.findByName(name);
        Assertions.assertThat(books).isNotEmpty().contains(bookSaved);
    }

    @Test
    @DisplayName("Find by autor returns book when successful")
    void findByAutor_ReturnsBook_WhenSuccessful() {
        Book bookToBeSaved = BookCreator.createBookToBeSaved();
        Book bookSaved = this.bookRepository.save(bookToBeSaved);

        String autor = bookSaved.getAutor();
        List<Book> books = this.bookRepository.findByAutor(autor);
        Assertions.assertThat(books).isNotEmpty().contains(bookSaved);
    }

    @Test
    @DisplayName("Find by name returns empty list when no book is found")
    void findByName_ReturnsEmptyList_WhenBookIsNotFound() {
        List<Book> books = this.bookRepository.findByName("Nonexistent");
        Assertions.assertThat(books).isEmpty();
    }

    @Test
    @DisplayName("Save throw ConstraintViolationException when name is empty")
    void save_ThrowsConstraintViolationException_WhenNameIsEmpty() {
        Book bookToBeSaved = new Book();
        Assertions.assertThatThrownBy(() -> this.bookRepository.save(bookToBeSaved))
                .isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    @DisplayName("Save throw ConstraintViolationException when autor is empty")
    void save_ThrowsConstraintViolationException_WhenAutorIsEmpty() {
        Book bookToBeSaved = new Book();
        bookToBeSaved.setName("Any Name");
        Assertions.assertThatExceptionOfType(ConstraintViolationException.class)
                .isThrownBy(() -> bookRepository.save(bookToBeSaved))
                .withMessageContaining("The autor cannot be null or empty");
    }

}
