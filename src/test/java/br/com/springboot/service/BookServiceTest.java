package br.com.springboot.service;

import br.com.springboot.domain.Book;
import br.com.springboot.exception.BadRequestException;
import br.com.springboot.repository.BookRepository;
import br.com.springboot.utils.BookCreator;
import br.com.springboot.utils.BookPostCreator;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
class BookServiceTest {

    @InjectMocks
    private BookService bookService;

    @Mock
    private BookRepository bookRepository;

    @BeforeEach
    void setUp() {
        PageImpl<Book> bookPage = new PageImpl<>(List.of(BookCreator.createdValidBook()));
        BDDMockito.when(bookRepository.findAll(ArgumentMatchers.any(PageRequest.class))).thenReturn(bookPage);

        BDDMockito.when(bookRepository.findAll()).thenReturn(List.of(BookCreator.createdValidBook()));

        BDDMockito.when(bookRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(BookCreator.createdValidBook()));

        BDDMockito.when(bookRepository.findByName(ArgumentMatchers.any()))
                .thenReturn(List.of(BookCreator.createdValidBook()));

        BDDMockito.when(bookRepository.findByAutor(ArgumentMatchers.any()))
                .thenReturn(List.of(BookCreator.createdValidBook()));

        BDDMockito.when(bookRepository.save(ArgumentMatchers.any(Book.class))).thenReturn(BookCreator.createdValidBook());

        BDDMockito.doNothing().when(bookRepository).delete(ArgumentMatchers.any());
    }

    @Test
    @DisplayName("listPageable returns list of books inside page object when successful")
    void listPageable_ReturnsListOfBooksInsidePageObject_WhenSuccessful() {
        String expectedName = BookCreator.createdValidBook().getName();
        Page<Book> bookPage = bookService.listPageable(PageRequest.of(1, 1));

        Assertions.assertThat(bookPage.toList()).isNotNull();
        Assertions.assertThat(bookPage.toList())
                .isNotEmpty()
                .hasSize(1);
        Assertions.assertThat(bookPage.toList().get(0).getName()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("listAll returns list of books when successful")
    void listAll_ReturnsListOfBooks_WhenSuccessful() {
        Book book = BookCreator.createdValidBook();
        List<Book> books = bookService.listAll();

        Assertions.assertThat(books)
                .isNotNull()
                .isNotEmpty()
                .hasSize(1);
        Assertions.assertThat(books).contains(book);
    }

    @Test
    @DisplayName("findById returns books when successful")
    void findById_ReturnsBook_WhenSuccessful() {
        Book book = bookService.findByIdOrThrowBadRequest(1L);

        Assertions.assertThat(book).isNotNull();
        Assertions.assertThat(book.getId()).isEqualTo(BookCreator.createdValidBook().getId());
    }

    @Test
    @DisplayName("findByIdOrThrowBadRequestException throws BadRequestException when book is not found")
    void findByIdOrThrowBadRequestException_ThrowsBadRequestException_WhenBookIsNotFound() {
        BDDMockito.when(bookRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.empty());

        Assertions.assertThatExceptionOfType(BadRequestException.class).isThrownBy(() -> this.bookService.findByIdOrThrowBadRequest(1L));
    }

    @Test
    @DisplayName("findByName returns a list of books when successful")
    void findByName_ReturnsAListOfBook_WhenSuccessful() {
        List<Book> books = bookService.findByName("Any");

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
        List<Book> books = bookService.findByName("Any");

        Assertions.assertThat(books)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("findByAutor returns a list of books when successful")
    void findByAutor_ReturnsAListOfBook_WhenSuccessful() {
        List<Book> books = bookService.findByAutor("Any");

        Assertions.assertThat(books)
                .isNotNull()
                .isNotEmpty()
                .hasSize(1);
        Assertions.assertThat(books.get(0).getAutor()).isEqualTo(BookCreator.createdValidBook().getAutor());
    }

    @Test
    @DisplayName("save returns book when successful")
    void save_ReturnsBook_WhenSuccessful() {
        Book book = bookService.save(BookPostCreator.createBookPostToBeSaved());

        Assertions.assertThat(book)
                .isNotNull()
                .isEqualTo(BookCreator.createdValidBook());
    }

    @Test
    @DisplayName("delete removes book when successful")
    void delete_RemovesBook_WhenSuccessful() {
        Assertions.assertThatCode(() -> bookService.delete(1L)).doesNotThrowAnyException();
    }
}

