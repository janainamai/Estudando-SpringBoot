package br.com.springboot.controller;

import br.com.springboot.domain.Book;
import br.com.springboot.dto.BookPost;
import br.com.springboot.dto.BookPut;
import br.com.springboot.service.BookService;
import br.com.springboot.utils.BookCreator;
import br.com.springboot.utils.BookPostCreator;
import br.com.springboot.utils.BookPutCreator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;

@ExtendWith(SpringExtension.class)
class BookControllerTest {

    @InjectMocks
    private BookController bookController;

    @Mock
    private BookService bookService;

    @BeforeEach
    void setUp() {
        PageImpl<Book> bookPage = new PageImpl<>(List.of(BookCreator.createdValidBook()));
        BDDMockito.when(bookService.listPageable(ArgumentMatchers.any())).thenReturn(bookPage);

        BDDMockito.when(bookService.listAll()).thenReturn(List.of(BookCreator.createdValidBook()));

        BDDMockito.when(bookService.findByIdOrThrowBadRequest(ArgumentMatchers.anyLong()))
                .thenReturn(BookCreator.createdValidBook());

        BDDMockito.when(bookService.findByName(ArgumentMatchers.any()))
                .thenReturn(List.of(BookCreator.createdValidBook()));

        BDDMockito.when(bookService.findByAutor(ArgumentMatchers.any()))
                .thenReturn(List.of(BookCreator.createdValidBook()));

        BDDMockito.when(bookService.save(ArgumentMatchers.any(BookPost.class))).thenReturn(BookCreator.createdValidBook());

        BDDMockito.doNothing().when(bookService).replace(ArgumentMatchers.any(BookPut.class));

        BDDMockito.doNothing().when(bookService).delete(ArgumentMatchers.anyLong());
    }

    @Test
    @DisplayName("listPageable returns list of books inside page object when successful")
    void list_ReturnsListOfBooksInsidePageObject_WhenSuccessful() {
        String expectedName = BookCreator.createdValidBook().getName();
        Page<Book> bookPage = bookController.listPageable(null).getBody();

        Assertions.assertThat(bookPage).isNotNull();
        Assertions.assertThat(bookPage.toList())
                .isNotEmpty()
                .hasSize(1);
        Assertions.assertThat(bookPage.toList().get(0).getName()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("listAll returns list of books when successful")
    void list_ReturnsListOfBooks_WhenSuccessful() {
        Book book = BookCreator.createdValidBook();
        List<Book> books = bookController.list().getBody();

        Assertions.assertThat(books)
                .isNotNull()
                .isNotEmpty()
                .hasSize(1);
        Assertions.assertThat(books).contains(book);
    }

    @Test
    @DisplayName("findById returns books when successful")
    void findById_ReturnsBook_WhenSuccessful() {
        Book book = bookController.findById(1L).getBody();

        Assertions.assertThat(book).isNotNull();
        Assertions.assertThat(book.getId()).isEqualTo(BookCreator.createdValidBook().getId());
    }

    @Test
    @DisplayName("findByName returns a list of books when successful")
    void findByName_ReturnsAListOfBook_WhenSuccessful() {
        List<Book> books = bookController.findByName("Any").getBody();

        Assertions.assertThat(books)
                .isNotNull()
                .isNotEmpty()
                .hasSize(1);
        Assertions.assertThat(books.get(0).getName()).isEqualTo(BookCreator.createdValidBook().getName());
    }

    @Test
    @DisplayName("findByName returns an empty list of books when book is not found")
    void findByName_ReturnsEmptyListOfBook_WhenBookIsNotFound() {
        BDDMockito.when(bookService.findByName(ArgumentMatchers.any())).thenReturn(Collections.emptyList());
        List<Book> books = bookController.findByName("Any").getBody();

        Assertions.assertThat(books)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("findByAutor returns a list of books when successful")
    void findByAutor_ReturnsAListOfBook_WhenSuccessful() {
        List<Book> books = bookController.findByAutor("Any").getBody();

        Assertions.assertThat(books)
                .isNotNull()
                .isNotEmpty()
                .hasSize(1);
        Assertions.assertThat(books.get(0).getAutor()).isEqualTo(BookCreator.createdValidBook().getAutor());
    }

    @Test
    @DisplayName("save returns book when successful")
    void save_ReturnsBook_WhenSuccessful() {
        Book book = bookController.save(BookPostCreator.createBookPostToBeSaved()).getBody();

        Assertions.assertThat(book)
                .isNotNull()
                .isEqualTo(BookCreator.createdValidBook());
    }

    @Test
    @DisplayName("replace updates book when successful")
    void replace_UpdatesBook_WhenSuccessful() {
        ResponseEntity<Void> entity = bookController.replace(BookPutCreator.createBookPutToBeSaved());

        Assertions.assertThat(entity).isNotNull();
        Assertions.assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("delete removes book when successful")
    void delete_RemovesBook_WhenSuccessful() {
        ResponseEntity<Void> entity = bookController.delete(1L);

        Assertions.assertThat(entity).isNotNull();
        Assertions.assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}

