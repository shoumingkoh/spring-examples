package com.smk.example.spring.boot.rest.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smk.example.spring.boot.rest.entity.Book;
import com.smk.example.spring.boot.rest.repository.BookRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional
public class BookService {

	private final BookRepository bookRepository;

	public BookService(BookRepository bookRepository) {
		this.bookRepository = bookRepository;
	}

	public Book saveBook(Book book) {
		log.info("Saving book {}", book);
		return bookRepository.saveAndFlush(book);
	}

	public Optional<Book> getBookById(Long id) {
		log.info("Getting book with id {}", id);
		Optional<Book> optionalBook = bookRepository.findById(id);
		return optionalBook;
	}
	
	public Optional<Book> getBookByTitle(String title) {
		log.info("Getting book with title {}", title);
		Optional<Book> optionalBook = bookRepository.findByTitle(title);
		return optionalBook;
	}
	
	public Optional<Book> getBookByTitleAndAuthor(String title, String author) {
		log.info("Getting book with title and author {}", title, author);
		Optional<Book> optionalBook = bookRepository.findByTitleAndAuthor(title, author);
		return optionalBook;
	}
	
	public List<Book> getAllBooks() {
		log.info("Geting all books");
		List<Book> books = bookRepository.findAll();
		return books;
	}
	
	public Page<Book> getAllBooks(Pageable pageable) {
		log.info("Geting all books by page");
		Page<Book> books = bookRepository.findAll(pageable);
		return books;
	}
	
	public void deleteBook(Long id) {
		log.info("Deleting book with id {}", id);
		bookRepository.deleteById(id);
	}

}
