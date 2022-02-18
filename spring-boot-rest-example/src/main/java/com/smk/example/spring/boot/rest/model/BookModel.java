package com.smk.example.spring.boot.rest.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.validation.GroupSequence;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import javax.validation.groups.Default;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.smk.example.spring.boot.rest.entity.Book.Format;

import lombok.Data;

@Data
public class BookModel {	
	
	public interface CheckVersion{}
	
	@GroupSequence({ CheckVersion.class, Default.class })
	public interface OnUpdate {}
	
	private Long id;
	
	@NotBlank
    @Size(min = 1, max = 255)
	private String title;
	
	@NotBlank
    @Size(min = 1, max = 255)
	private String author;
	
	@NotNull
    @Past
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
	private LocalDate publicationDate;
	
	@NotBlank
    @Size(min = 1, max = 255)
	private String publisher;

    @Pattern(regexp="^(?:ISBN(?:-1[03])?:? )?(?=[0-9X]{10}$|(?=(?:[0-9]+[- ]){3})[- 0-9X]{13}$|97[89][0-9]{10}$|(?=(?:[0-9]+[- ]){4})[- 0-9]{17}$)(?:97[89][- ]?)?[0-9]{1,5}[- ]?[0-9]+[- ]?[0-9]+[- ]?[0-9X]$")
    private String isbn;
    
	private Format format;
		
	@Positive
	@Digits(integer = 7, fraction = 0)
	private Integer pages;
	
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy hh:mm:ss a")
	private LocalDateTime updatedDate;
	
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy hh:mm:ss a")
	private LocalDateTime createdDate;
	
	@NotNull(groups = CheckVersion.class)
	private Long version;
	
}
