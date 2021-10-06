package br.com.springboot.client;

import br.com.springboot.domain.Book;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Log4j2
public class SpringClient {
    public static void main(String[] args) {
        // fazendo requisição para meu próprio serviço
        // buscando uma entidade do livro
        ResponseEntity<Book> entity = new RestTemplate().getForEntity("http://localhost:8080/books/18", Book.class);
        log.info(entity);

        // se quisermos direto o objeto, podemos utilizar o getForObject
        Book object = new RestTemplate().getForObject("http://localhost:8080/books/18", Book.class);
        log.info(object);

        // temos outra forma de buscar esse livro por id
        // passando vários ids
        Book bookObject = new RestTemplate().getForObject("http://localhost:8080/books/{id}", Book.class, 18);
        log.info(bookObject);

        // buscando um array de livros
        Book[] booksArray = new RestTemplate().getForObject("http://localhost:8080/books", Book[].class);
        log.info(Arrays.toString(booksArray));

        // buscando uma lista de livros
        ResponseEntity<List<Book>> booksList = new RestTemplate().exchange("http://localhost:8080/books",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Book>>() {
                });
        log.info(booksList.getBody());

        // requisições post com rest template

        // salvando um livro com postForObject
        // informando url, objeto a ser salvo e tipo do objeto a ser retornado
        Book bookOne = Book.builder().name("Jogos Vorazes").autor("Suzanne Collins").build();
        Book bookSavedOne = new RestTemplate().postForObject("http://localhost:8080/books", bookOne, Book.class);
        log.info("Saved book {}", bookSavedOne);

        // salvando um livro com exchange
        // informando url, tipo de requisição, objeto a ser salvo e tipo do objeto a ser retornado
        Book bookTwo = Book.builder().name("Admirável Mundo Novo").autor("Aldous Huxley").build();
        ResponseEntity<Book> bookSavedTwo = new RestTemplate().exchange("http://localhost:8080/books/",
                HttpMethod.POST,
                new HttpEntity<>(bookTwo),
                Book.class);
        log.info("Saved book {}", bookSavedTwo.getBody());

        // enviando um header informando que o content type é um JSON com o método criado abaixo: createJsonHeader
        // informando url, tipo de requisição, objeto a ser salvo e content type json, tipo de objeto a ser retornadou
        Book bookThree = Book.builder().name("O Herói Perdido").autor("Rick Riordan").build();
        ResponseEntity<Book> bookSavedThree = new RestTemplate().exchange("http://localhost:8080/books/",
                HttpMethod.POST,
                new HttpEntity<>(bookThree, createJsonHeader()),
                Book.class);
        log.info("Saved book {}", bookSavedThree);

        // executando requisições put
        // alterando livro
        Book bookToUpdated = bookSavedThree.getBody();
        bookToUpdated.setName("The Last Olympian");
        ResponseEntity<Void> bookUpdated = new RestTemplate().exchange("http://localhost:8080/books/",
                HttpMethod.PUT,
                new HttpEntity<>(bookToUpdated, createJsonHeader()),
                Void.class);
        log.info(bookUpdated);

        // executando requisições delete
        // deletando livro
        ResponseEntity<Void> bookDeleted = new RestTemplate().exchange("http://localhost:8080/books/{id}",
                HttpMethod.DELETE,
                null,
                Void.class,
                bookToUpdated.getId());
        log.info(bookDeleted);
    }

    private static HttpHeaders createJsonHeader() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return httpHeaders;
    }
}
