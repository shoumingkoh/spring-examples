package com.smk.example.spring.boot.rest.controller;

import java.io.StringReader;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.json.Json;
import javax.json.JsonMergePatch;
import javax.json.JsonPatch;
import javax.json.JsonReader;
import javax.json.JsonStructure;
import javax.json.JsonValue;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;
import javax.validation.constraints.Min;

import org.modelmapper.ModelMapper;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smk.example.spring.boot.rest.entity.Book;
import com.smk.example.spring.boot.rest.exception.BookNotFoundException;
import com.smk.example.spring.boot.rest.exception.NoBookFoundException;
import com.smk.example.spring.boot.rest.model.BookModel;
import com.smk.example.spring.boot.rest.model.BookModel.OnUpdate;
import com.smk.example.spring.boot.rest.service.BookService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(path = "/books")
@Slf4j
@Validated
public class BookController {

	private final ObjectMapper objectMapper;

	private final Validator validator;

	private final BookService bookService;

	public BookController(BookService bookService, ObjectMapper objectMapper, Validator validator) {
		this.bookService = bookService;
		this.objectMapper = objectMapper;
		this.validator = validator;
	}

	@Operation(summary = "Get book by ID")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Book is found", content = {
			@Content(mediaType = "application/json", schema = @Schema(implementation = BookModel.class)) }) })
	@GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.OK)
	public BookModel getBook(@Parameter(description = "ID of book to search") @PathVariable @Min(1) Long id) {
		log.info("HTTP GET Book with id {}", id);
		ModelMapper modelMapper = new ModelMapper();
		Book book = bookService.getBookById(id).orElseThrow(() -> new BookNotFoundException(id));
		BookModel bookModel = modelMapper.map(book, BookModel.class);
		return bookModel;
	}

	@Operation(summary = "Search book by title and author (optional)")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Book is found", content = {
			@Content(mediaType = "application/json", schema = @Schema(implementation = BookModel.class)) }) })
	@GetMapping(path = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.OK)
	public BookModel getBookByTitleAndOptionalAuthor(
			@Parameter(description = "Title of book to search") @RequestParam String title,
			@Parameter(description = "Author of book to search (optional)") @RequestParam(required = false) Optional<String> author) {
		log.info("HTTP GET Book with title and optional author");
		ModelMapper modelMapper = new ModelMapper();
		Book book;
		if (author.isPresent())
			book = bookService.getBookByTitleAndAuthor(title, author.get())
					.orElseThrow(() -> new BookNotFoundException(title, author.get()));
		else
			book = bookService.getBookByTitle(title).orElseThrow(() -> new BookNotFoundException(title));
		BookModel bookModel = modelMapper.map(book, BookModel.class);
		return bookModel;
	}

	@Operation(summary = "Get all books")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "All books are found", content = {
			@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BookModel.class))) }) })
	@GetMapping(path = "/", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.OK)
	public List<BookModel> getAllBooks() {
		log.info("HTTP GET all Book");
		ModelMapper modelMapper = new ModelMapper();
		List<Book> books = bookService.getAllBooks();
		if (CollectionUtils.isEmpty(books))
			throw new NoBookFoundException();
		List<BookModel> bookModels = books.stream().map(book -> {
			BookModel bookModel = modelMapper.map(book, BookModel.class);
			return bookModel;
		}).collect(Collectors.toList());
		bookModels.sort(Comparator.comparing(BookModel::getTitle));
		return bookModels;
	}

	@Operation(summary = "Get all books by pages")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "All books are found") })
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.OK)
	public Page<BookModel> getAllBooks(@ParameterObject Pageable pageable) {
		log.info("HTTP GET all Book by Page");
		ModelMapper modelMapper = new ModelMapper();
		Page<Book> books = bookService.getAllBooks(pageable);
		if (CollectionUtils.isEmpty(books.getContent()))
			throw new NoBookFoundException();
		Page<BookModel> bookModels = books.map(book -> {
			BookModel bookModel = modelMapper.map(book, BookModel.class);
			return bookModel;
		});
		return bookModels;
	}

	@Operation(summary = "Add new book")
	@ApiResponses(value = { @ApiResponse(responseCode = "201", description = "Book added successfully", content = {
			@Content(mediaType = "application/json", schema = @Schema(implementation = BookModel.class)) }) })
	@PostMapping(path = "/", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	public BookModel createBook(
			@Valid @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "JSON document of book to add", required = true, content = @Content(schema = @Schema(implementation = BookModel.class))) @RequestBody BookModel bookModel) {
		log.info("HTTP POST new Book {}", bookModel);
		return saveBook(bookModel, new Book());
	}

	@Operation(summary = "Update book by ID")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Book added successfully", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = BookModel.class)) }),
			@ApiResponse(responseCode = "200", description = "Book updated successfully", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = BookModel.class)) }) })
	@PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<BookModel> updateBook(
			@Parameter(description = "ID of the book to update") @PathVariable @Min(1) Long id,
			@Validated(OnUpdate.class) @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "JSON document of book to update (need to include all fields even if they have no changes)", required = true, content = @Content(schema = @Schema(implementation = BookModel.class))) @RequestBody BookModel bookModel) {
		log.info("HTTP PUT Book {})", bookModel);
		return bookService.getBookById(id).map(book -> {
			return new ResponseEntity<BookModel>(saveBook(bookModel, book), HttpStatus.OK);
		}).orElseGet(() -> {
			return new ResponseEntity<BookModel>(saveBook(bookModel, new Book()), HttpStatus.CREATED);
		});
	}

	@Operation(summary = "Merge book by ID")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Book updated successfully", content = {
			@Content(mediaType = "application/json", schema = @Schema(implementation = BookModel.class)) }) })
	@PatchMapping(path = "/merge/{id}", consumes = "application/merge-patch+json", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.OK)
	public BookModel mergeBook(@Parameter(description = "ID of book to merge") @PathVariable @Min(1) Long id,
			@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "JSON document that contains field(s) of book to update (no need to include fields that have no changes even if they are specified as required)", required = true, content = @Content(schema = @Schema(implementation = BookModel.class))) @RequestBody String payload) {
		log.info("HTTP MERGE PATCH Book with id {}", id);
		JsonMergePatch patchDocument;
		try (JsonReader reader = Json.createReader(new StringReader(payload))) {
			patchDocument = Json.createMergePatch(reader.readValue());
		}
		ModelMapper modelMapper = new ModelMapper();
		Book book = bookService.getBookById(id).orElseThrow(() -> new BookNotFoundException(id));
		BookModel bookModel = modelMapper.map(book, BookModel.class);
		// convert model targeted for patching to JSON document
		JsonStructure target = objectMapper.convertValue(bookModel, JsonStructure.class);
		// apply JSON patch onto JSON document of target model
		JsonValue patched = patchDocument.apply(target);
		// convert JSON document of target model back to model
		BookModel patchedBookModel = objectMapper.convertValue(patched, BookModel.class);
		Set<ConstraintViolation<BookModel>> violations = validator.validate(patchedBookModel);
		if (!violations.isEmpty()) {
			throw new ConstraintViolationException(violations);
		}
		return saveBook(patchedBookModel, book);
	}

	@Operation(summary = "Patch book by ID")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Book updated successfully", content = {
			@Content(mediaType = "application/json", schema = @Schema(implementation = BookModel.class)) }) })
	@PatchMapping(path = "/patch/{id}", consumes = "application/json-patch+json", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.OK)
	public BookModel patchBook(@Parameter(description = "ID of book to patch") @PathVariable @Min(1) Long id,
			@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "JSONPatch document to patch book", required = true, content = @Content(schema = @Schema(ref = "#/components/schemas/JSONPatch"))) @RequestBody String payload) {
		log.info("HTTP PATCH Book with id {}", id);
		JsonPatch patchDocument;
		try (JsonReader reader = Json.createReader(new StringReader(payload))) {
			// pay load must be in JSON Array
			patchDocument = Json.createPatch(reader.readArray());
		}
		ModelMapper modelMapper = new ModelMapper();
		Book book = bookService.getBookById(id).orElseThrow(() -> new BookNotFoundException(id));
		BookModel bookModel = modelMapper.map(book, BookModel.class);
		// convert model targeted for patching to JSON document
		JsonStructure target = objectMapper.convertValue(bookModel, JsonStructure.class);
		// apply JSON patch onto JSON document of target model
		JsonValue patched = patchDocument.apply(target);
		// convert JSON document of target model back to model
		BookModel patchedBookModel = objectMapper.convertValue(patched, BookModel.class);
		Set<ConstraintViolation<BookModel>> violations = validator.validate(patchedBookModel);
		if (!violations.isEmpty()) {
			throw new ConstraintViolationException(violations);
		}
		return saveBook(patchedBookModel, book);
	}

	@Operation(summary = "Delete book by ID")
	@ApiResponses(value = { @ApiResponse(responseCode = "204", description = "Book deleted successfully") })
	@DeleteMapping(path = "/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteBook(@Parameter(description = "ID of book to delete") @PathVariable @Min(1) Long id) {
		log.info("HTTP DELETE Book with id {}", id);
		try {
			bookService.deleteBook(id);
		} catch (EmptyResultDataAccessException exception) {
			throw new BookNotFoundException(id);
		}
	}

	private BookModel saveBook(BookModel bookModel, Book book) {
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.typeMap(BookModel.class, Book.class).addMappings(mapper -> {
			mapper.skip(Book::setId);
		});
		modelMapper.map(bookModel, book);
		book = bookService.saveBook(book);
		BookModel savedBookModel = modelMapper.map(book, BookModel.class);
		return savedBookModel;
	}

}
