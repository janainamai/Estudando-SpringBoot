package br.com.springboot.utils;

import br.com.springboot.dto.BookPost;

public class BookPostCreator {
    public static BookPost createBookPostToBeSaved() {
        return BookPost.builder()
                .name(BookCreator.createBookToBeSaved().getName())
                .autor(BookCreator.createBookToBeSaved().getAutor())
                .build();
    }
}
