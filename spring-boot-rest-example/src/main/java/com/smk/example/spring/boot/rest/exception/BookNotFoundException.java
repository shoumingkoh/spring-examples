package com.smk.example.spring.boot.rest.exception;

public class BookNotFoundException extends RuntimeException {
	
	private static final long serialVersionUID = -425161178890028011L;

	public BookNotFoundException(Long id) {
        super(String.format("Book with id %d not found", id));
    }
	
	public BookNotFoundException(String title) {
        super(String.format("Book with title %s not found", title));
    }
	
	public BookNotFoundException(String title, String author) {
        super(String.format("Book with title %s and author %s not found", title, author));
    }
}
