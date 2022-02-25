package com.smk.example.spring.boot.rest.errorhandling;

import java.util.Arrays;
import java.util.List;

import lombok.Data;

@Data
public class ErrorResponse {

	private String exception;
	private List<String> errors;
	
	public ErrorResponse() {
		super();
	}

	public ErrorResponse(final String exception, final List<String> errors) {
		super();
		this.exception = exception;
		this.errors = errors;
	}

	public ErrorResponse(final String exception, final String errors) {
		super();
		this.exception = exception;
		this.errors = Arrays.asList(errors);
	}

}