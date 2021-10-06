package br.com.springboot.utils;

import br.com.springboot.dto.BookPut;

public class BookPutCreator {
    public static BookPut createBookPutToBeSaved() {
        return BookPut.builder()
                .id(BookCreator.createdValidUpdatedBook().getId())
                .name(BookCreator.createdValidUpdatedBook().getName())
                .autor(BookCreator.createdValidUpdatedBook().getAutor())
                .build();
    }
}
