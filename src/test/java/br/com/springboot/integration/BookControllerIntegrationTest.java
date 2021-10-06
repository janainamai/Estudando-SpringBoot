package br.com.springboot.integration;

import br.com.springboot.domain.Book;
import br.com.springboot.repository.BookRepository;
import br.com.springboot.utils.BookCreator;
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
import org.springframework.http.HttpMethod;
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
    @DisplayName("list returns list of books when successful")
    void list_ReturnsListOfBooksInsidePageObject_WhenSuccessful() {
        Book savedBook = bookRepository.save(BookCreator.createBookToBeSaved());
        String expectedName = savedBook.getName();

        List<Book> books = testRestTemplate.exchange("/books",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Book>>() {
                }).getBody();

        Assertions.assertThat(books).isNotNull();
        Assertions.assertThat(books)
                .isNotEmpty()
                .hasSize(1);
        Assertions.assertThat(books.get(0).getName()).isEqualTo(expectedName);
    }
}
