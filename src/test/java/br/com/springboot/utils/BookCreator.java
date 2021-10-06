package br.com.springboot.utils;

import br.com.springboot.domain.Book;

public class BookCreator {

    public static Book createBookToBeSaved() {
        return Book.builder().name("Book").autor("Neil").build();
    }

    public static Book createdValidBook() {
        return Book.builder().id(1L).name("Book").autor("Neil").build();
    }

    public static Book createdValidUpdatedBook() {
        return Book.builder().id(1L).name("Book Updated").autor("Neil Updated").build();
    }

}
