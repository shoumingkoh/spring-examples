package com.smk.example.spring.boot.rest.errorhandling;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpStatus;

import lombok.Data;

@Data
public class ErrorResponse {

	private HttpStatus status;
	private String exceptionMessage;
	private List<String> errorMessages;
	
	public ErrorResponse() {
		super();
	}

	public ErrorResponse(final HttpStatus status, final String exceptionMessage, final List<String> errorMessages) {
		super();
		this.status = status;
		this.exceptionMessage = exceptionMessage;
		this.errorMessages = errorMessages;
	}

	public ErrorResponse(final HttpStatus status, final String exceptionMessage, final String errorMessage) {
		super();
		this.status = status;
		this.exceptionMessage = exceptionMessage;
		errorMessages = Arrays.asList(errorMessage);
	}

	public void setError(final String error) {
		errorMessages = Arrays.asList(error);
	}

}