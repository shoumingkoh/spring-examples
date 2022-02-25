package com.smk.example.spring.boot.rest.errorhandling;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.smk.example.spring.boot.rest.exception.BookNotFoundException;
import com.smk.example.spring.boot.rest.exception.NoBookFoundException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class RestExceptionHandler {

	@ExceptionHandler({ MethodArgumentNotValidException.class })
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, WebRequest request) {
		log.error("Argument annotated with @Valid failed validation", ex);
		List<String> errorMessages = new ArrayList<String>();
		for (FieldError error : ex.getBindingResult().getFieldErrors()) {
			errorMessages.add(error.getField() + " " + error.getDefaultMessage());
		}
		for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
			errorMessages.add(error.getObjectName() + " " + error.getDefaultMessage());
		}
		final ErrorResponse errorResponse = new ErrorResponse(ex.getLocalizedMessage(), errorMessages);
		return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler({ MissingServletRequestParameterException.class })
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<ErrorResponse> handleMissingServletRequestParameter(
			MissingServletRequestParameterException ex, WebRequest request) {
		log.error("Request with missing parameter", ex);
		String errorMessage = ex.getParameterName() + " parameter is missing";
		final ErrorResponse errorResponse = new ErrorResponse(ex.getLocalizedMessage(), errorMessage);
		return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler({ HttpMessageNotReadableException.class })
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, WebRequest request) {
		log.error("Uable to deserialize from JSON", ex);
		String errorMessage = "Malformed JSON request";
		final ErrorResponse errorResponse = new ErrorResponse(ex.getLocalizedMessage(), errorMessage);
		return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler({ HttpMessageNotWritableException.class })
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseEntity<ErrorResponse> handleHttpMessageNotWritable(HttpMessageNotWritableException ex, WebRequest request) {
		log.error("Unable to serialize to JSON", ex);
		String errorMessage = "Error encountered while serializing to JSON";
		final ErrorResponse errorResponse = new ErrorResponse(ex.getLocalizedMessage(), errorMessage);
		return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler({ ConstraintViolationException.class })
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex, WebRequest request) {
		log.error("Constraint violation encountered", ex);
		List<String> errorMessages = new ArrayList<String>();
		for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
			errorMessages.add(violation.getRootBeanClass().getSimpleName() + " " + violation.getPropertyPath() + " "
					+ violation.getMessage());
		}
		final ErrorResponse errorResponse = new ErrorResponse(ex.getLocalizedMessage(), errorMessages);
		return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler({ MethodArgumentTypeMismatchException.class })
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex, WebRequest request) {
		log.error("Method argument is not the expected type", ex);
		String errorMessage = ex.getName() + " should be of type " + ex.getRequiredType().getSimpleName();
		final ErrorResponse errorResponse = new ErrorResponse(ex.getLocalizedMessage(), errorMessage);
		return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler({ HttpRequestMethodNotSupportedException.class })
	@ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
	public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupported(
			HttpRequestMethodNotSupportedException ex, WebRequest request) {
		log.error("Request with an unsupported HTTP method", ex);
		StringBuilder builder = new StringBuilder();
		builder.append(ex.getMethod());
		builder.append(" method is not supported for this request; supported methods are: ");
		ex.getSupportedHttpMethods().forEach(t -> builder.append(t + "; "));
		final ErrorResponse errorResponse = new ErrorResponse(ex.getLocalizedMessage(), builder.toString());
		return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.METHOD_NOT_ALLOWED);
	}

	@ExceptionHandler({ HttpMediaTypeNotSupportedException.class })
	@ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
	public ResponseEntity<ErrorResponse> handleHttpMediaTypeNotSupported(final HttpMediaTypeNotSupportedException ex, final WebRequest request) {
		log.error("Request with an unsupported media types", ex);
		StringBuilder builder = new StringBuilder();
		builder.append(ex.getContentType());
		builder.append(" media type is not supported; supported media types are: ");
		ex.getSupportedMediaTypes().forEach(t -> builder.append(t + "; "));
		final ErrorResponse errorResponse = new ErrorResponse(ex.getLocalizedMessage(), builder.toString());
		return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
	}

	@ExceptionHandler({ ObjectOptimisticLockingFailureException.class })
	@ResponseStatus(HttpStatus.CONFLICT)
	public ResponseEntity<ErrorResponse> handleOptimisticLocking(ObjectOptimisticLockingFailureException ex, WebRequest request) {
		log.error("Optimistic Locking encountered", ex);
		String errorMessage = "The record has been updated by another user; please reload it and resubmit your changes";
		final ErrorResponse errorResponse = new ErrorResponse(ex.getLocalizedMessage(), errorMessage);
		return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.CONFLICT);
	}

	@ExceptionHandler({ BookNotFoundException.class })
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ResponseEntity<ErrorResponse> handleBookNotFound(BookNotFoundException ex, WebRequest request) {
		log.error("Book not found", ex);
		String errorMessage = ex.getLocalizedMessage();
		final ErrorResponse errorResponse = new ErrorResponse(ex.getLocalizedMessage(), errorMessage);
		return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler({ NoBookFoundException.class })
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ResponseEntity<ErrorResponse> handleNoBookFound(NoBookFoundException ex, WebRequest request) {
		log.error("No book found", ex);
		String errorMessage = ex.getLocalizedMessage();
		final ErrorResponse errorResponse = new ErrorResponse(ex.getLocalizedMessage(), errorMessage);
		return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler({ NoHandlerFoundException.class })
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ResponseEntity<ErrorResponse> handleNoHandlerFoundException(NoHandlerFoundException ex, WebRequest request) {
		log.error("No handler found", ex);
		String errorMessage = "No handler found for " + ex.getHttpMethod() + " " + ex.getRequestURL();
		final ErrorResponse errorResponse = new ErrorResponse(ex.getLocalizedMessage(), errorMessage);
		return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler({ Exception.class })
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseEntity<ErrorResponse> handleUncaughtException(Exception ex, WebRequest request) {
		log.error("Uncaught exception", ex);
		final ErrorResponse errorResponse = new ErrorResponse(ex.getLocalizedMessage(), "An error has occurred");
		return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}

}