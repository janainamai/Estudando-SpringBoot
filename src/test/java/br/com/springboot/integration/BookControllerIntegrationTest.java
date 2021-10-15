package br.com.springboot.integration;

import br.com.springboot.domain.Book;
import br.com.springboot.domain.SystemUser;
import br.com.springboot.dto.BookPost;
import br.com.springboot.repository.BookRepository;
import br.com.springboot.repository.SystemUserRepository;
import br.com.springboot.utils.BookCreator;
import br.com.springboot.utils.BookPostCreator;
import br.com.springboot.wrapper.PageableResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
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
    @Qualifier(value = "testRestTemplateRoleUser")
    private TestRestTemplate testRestTemplateRoleUser;

    @Autowired
    @Qualifier(value = "testRestTemplateRoleAdmin")
    private TestRestTemplate testRestTemplateRoleAdmin;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private SystemUserRepository userRepository;

    private static final SystemUser ADMIN = SystemUser.builder()
            .name("Janaina Mai")
            .password("{bcrypt}$2a$10$QFLje8sda2z69NPE4QGqgepc4BkF.hqzfit9YlO9zz275hONW3oeW")
            .username("janainamai")
            .authorities("ROLE_USER,ROLE_ADMIN")
            .build();

    private static final SystemUser USER = SystemUser.builder()
            .name("Heloisa Vendel Theiss")
            .password("{bcrypt}$2a$10$QFLje8sda2z69NPE4QGqgepc4BkF.hqzfit9YlO9zz275hONW3oeW")
            .username("heloisatheiss")
            .authorities("ROLE_USER")
            .build();

    @TestConfiguration
    @Lazy
    static class Config {

        @Bean(name = "testRestTemplateRoleUser")
        public TestRestTemplate testRestTemplateRoleUserCreator(@Value("${local.server.port}") int port) {
            RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
                    .rootUri("http://localhost:" + port)
                    .basicAuthentication("heloisatheiss", "admin");
            return new TestRestTemplate(restTemplateBuilder);
        }

        @Bean(name = "testRestTemplateRoleAdmin")
        public TestRestTemplate testRestTemplateRoleAdminCreator(@Value("${local.server.port}") int port) {
            RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
                    .rootUri("http://localhost:" + port)
                    .basicAuthentication("janainamai", "admin");
            return new TestRestTemplate(restTemplateBuilder);
        }
    }

    @Test
    @DisplayName("listPageable returns list of books inside a page when successful")
    void listPageable_ReturnsListOfBooksInsidePage_WhenSuccessful() {
        userRepository.save(USER);
        Book savedBook = bookRepository.save(BookCreator.createBookToBeSaved());
        String expectedName = savedBook.getName();

        PageableResponse<Book> books = testRestTemplateRoleUser.exchange("/books/listPageable",
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
        userRepository.save(USER);
        String expectedName = savedBook.getName();

        List<Book> books = testRestTemplateRoleUser.exchange("/books",
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
        userRepository.save(USER);
        Book savedBook = bookRepository.save(BookCreator.createBookToBeSaved());
        Long expectedId = savedBook.getId();

        Book book = testRestTemplateRoleUser.getForObject("/books/{id}", Book.class, expectedId);

        Assertions.assertThat(book).isNotNull();
        Assertions.assertThat(book.getId()).isEqualTo(expectedId);
    }

    @Test
    @DisplayName("findByName returns a list of books when successful")
    void findByName_ReturnsAListOfBook_WhenSuccessful() {
        userRepository.save(USER);
        Book savedBook = bookRepository.save(BookCreator.createBookToBeSaved());
        String expectedName = savedBook.getName();

        List<Book> books = testRestTemplateRoleUser.exchange("/books/find/{name}",
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
        userRepository.save(USER);
        List<Book> books = testRestTemplateRoleUser.exchange("/books/find/{name}",
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
        userRepository.save(USER);
        Book savedBook = bookRepository.save(BookCreator.createBookToBeSaved());
        String expectedAutor = savedBook.getAutor();
        String url = String.format("/books/find?autor=%s", expectedAutor);

        List<Book> books = testRestTemplateRoleUser.exchange(url,
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
        userRepository.save(ADMIN);
        BookPost bookPostToBeSaved = BookPostCreator.createBookPostToBeSaved();
        ResponseEntity<Book> bookResponseEntity = testRestTemplateRoleUser.postForEntity("/books/admin",
                bookPostToBeSaved, Book.class);

        Assertions.assertThat(bookResponseEntity).isNotNull();
        Assertions.assertThat(bookResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Assertions.assertThat(bookResponseEntity.getBody()).isNotNull();
        Assertions.assertThat(bookResponseEntity.getBody().getId()).isNotNull();
    }

    @Test
    @DisplayName("save returns 403 when user is not admin")
    void save_Returns403_WhenUserIsNotAdmin() {
        userRepository.save(USER);
        BookPost bookPostToBeSaved = BookPostCreator.createBookPostToBeSaved();
        ResponseEntity<Book> bookResponseEntity = testRestTemplateRoleUser.postForEntity("/books/admin",
                bookPostToBeSaved, Book.class);

        Assertions.assertThat(bookResponseEntity).isNotNull();
        Assertions.assertThat(bookResponseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("replace updates book when successful")
    void replace_UpdatesBook_WhenSuccessful() {
        userRepository.save(ADMIN);
        Book savedBook = bookRepository.save(BookCreator.createBookToBeSaved());
        savedBook.setName("New name");

        ResponseEntity<Void> bookResponseEntity = testRestTemplateRoleUser.exchange("/books/admin",
                HttpMethod.PUT, new HttpEntity<>(savedBook), Void.class);

        Assertions.assertThat(bookResponseEntity).isNotNull();
        Assertions.assertThat(bookResponseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("delete removes book when successful")
    void delete_RemovesBook_WhenSuccessful() {
        userRepository.save(ADMIN);
        Book savedBook = bookRepository.save(BookCreator.createBookToBeSaved());
        ResponseEntity<Void> bookResponseEntity = testRestTemplateRoleUser.exchange("/books/admin/{id}",
                HttpMethod.DELETE, null, Void.class, savedBook.getId());

        Assertions.assertThat(bookResponseEntity).isNotNull();
        Assertions.assertThat(bookResponseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}
