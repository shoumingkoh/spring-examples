package com.smk.example.spring.boot.rest.exception;

public class NoBookFoundException extends RuntimeException {

	private static final long serialVersionUID = -8862058366227562793L;

	public NoBookFoundException() {
        super("No book found");
    }
}
