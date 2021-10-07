package br.com.springboot.integration;

import br.com.springboot.domain.Book;
import br.com.springboot.dto.BookPost;
import br.com.springboot.repository.BookRepository;
import br.com.springboot.utils.BookCreator;
import br.com.springboot.utils.BookPostCreator;
import br.com.springboot.wrapper.PageableResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
public class BookControllerIntegrationTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @LocalServerPort
    private int port;

    @Autowired
    private BookRepository bookRepository;

    @Test
    @DisplayName("listPageable returns list of books inside a page when successful")
    void listPageable_ReturnsListOfBooksInsidePage_WhenSuccessful() {
        Book savedBook = bookRepository.save(BookCreator.createBookToBeSaved());
        String expectedName = savedBook.getName();

        PageableResponse<Book> books = testRestTemplate.exchange("/books/listPageable",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<PageableResponse<Book>>() {
                }).getBody();

        Assertions.assertThat(books).isNotNull();
        Assertions.assertThat(books.toList())
                .isNotEmpty()
                .hasSize(1);
        Assertions.assertThat(books.toList().get(0).getName()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("list returns list of books when successful")
    void list_ReturnsListOfBooks_WhenSuccessful() {
        Book savedBook = bookRepository.save(BookCreator.createBookToBeSaved());
        String expectedName = savedBook.getName();

        List<Book> books = testRestTemplate.exchange("/books",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Book>>() {
                }).getBody();

        Assertions.assertThat(books)
                .isNotNull()
                .isNotEmpty()
                .hasSize(1);
        Assertions.assertThat(books.get(0).getName()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("findById returns books when successful")
    void findById_ReturnsBook_WhenSuccessful() {
        Book savedBook = bookRepository.save(BookCreator.createBookToBeSaved());
        Long expectedId = savedBook.getId();

        Book book = testRestTemplate.getForObject("/books/{id}", Book.class, expectedId);

        Assertions.assertThat(book).isNotNull();
        Assertions.assertThat(book.getId()).isEqualTo(expectedId);
    }

    @Test
    @DisplayName("findByName returns a list of books when successful")
    void findByName_ReturnsAListOfBook_WhenSuccessful() {
        Book savedBook = bookRepository.save(BookCreator.createBookToBeSaved());
        String expectedName = savedBook.getName();

        List<Book> books = testRestTemplate.exchange("/books/find/{name}",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Book>>() {
                }, expectedName).getBody();

        Assertions.assertThat(books)
                .isNotNull()
                .isNotEmpty()
                .hasSize(1);
        Assertions.assertThat(books.get(0).getName()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("findByName returns an empty list of books when book is not found")
    void findByName_ReturnsEmptyListOfBook_WhenBookIsNotFound() {
        List<Book> books = testRestTemplate.exchange("/books/find/{name}",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Book>>() {
                }, "any").getBody();

        Assertions.assertThat(books)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("findByAutor returns a list of books when successful")
    void findByAutor_ReturnsAListOfBook_WhenSuccessful() {
        Book savedBook = bookRepository.save(BookCreator.createBookToBeSaved());
        String expectedAutor = savedBook.getAutor();
        String url = String.format("/books/find?autor=%s", expectedAutor);

        List<Book> books = testRestTemplate.exchange(url,
                HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Book>>() {
                }).getBody();

        Assertions.assertThat(books)
                .isNotNull()
                .isNotEmpty()
                .hasSize(1);
        Assertions.assertThat(books.get(0).getAutor()).isEqualTo(expectedAutor);
    }

    @Test
    @DisplayName("save returns book when successful")
    void save_ReturnsBook_WhenSuccessful() {
        BookPost bookPostToBeSaved = BookPostCreator.createBookPostToBeSaved();
        ResponseEntity<Book> bookResponseEntity = testRestTemplate.postForEntity("/books",
                bookPostToBeSaved, Book.class);

        Assertions.assertThat(bookResponseEntity).isNotNull();
        Assertions.assertThat(bookResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Assertions.assertThat(bookResponseEntity.getBody()).isNotNull();
        Assertions.assertThat(bookResponseEntity.getBody().getId()).isNotNull();
    }

    @Test
    @DisplayName("replace updates book when successful")
    void replace_UpdatesBook_WhenSuccessful() {
        Book savedBook = bookRepository.save(BookCreator.createBookToBeSaved());
        savedBook.setName("New name");

        ResponseEntity<Void> bookResponseEntity = testRestTemplate.exchange("/books",
                HttpMethod.PUT, new HttpEntity<>(savedBook), Void.class);

        Assertions.assertThat(bookResponseEntity).isNotNull();
        Assertions.assertThat(bookResponseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("delete removes book when successful")
    void delete_RemovesBook_WhenSuccessful() {
        Book savedBook = bookRepository.save(BookCreator.createBookToBeSaved());
        ResponseEntity<Void> bookResponseEntity = testRestTemplate.exchange("/books/{id}",
                HttpMethod.DELETE, null, Void.class, savedBook.getId());

        Assertions.assertThat(bookResponseEntity).isNotNull();
        Assertions.assertThat(bookResponseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}
