package com.smk.example.spring.boot.rest.entity;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Version;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper=true)
public class Book extends DateAudit implements Serializable {

	public enum Format {
		PAPERBACK("Paperback"), HARDBACK("Hardback"), DIGITAL("Digital"), AUDIO("Audio");

		@Getter(onMethod = @__( @JsonValue))
		@Setter
		private String label;

		private Format(String label) {
			this.label = label;
		}			
	}
	
	@Getter(value = AccessLevel.NONE)
	@Setter(value = AccessLevel.NONE)
	private static final long serialVersionUID = 4171805558809613766L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotBlank
    @Size(min = 1, max = 255)
	@Column(nullable = false, length = 255)
	private String title;
	
	@NotBlank
    @Size(min = 1, max = 255)
	@Column(nullable = false, length = 255)
	private String author;
	
	@NotNull
    @Past
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	private LocalDate publicationDate;
	
	@NotBlank
    @Size(min = 1, max = 255)
	@Column(length = 255)
	private String publisher;
	
    @Pattern(regexp="^(?:ISBN(?:-1[03])?:? )?(?=[0-9X]{10}$|(?=(?:[0-9]+[- ]){3})[- 0-9X]{13}$|97[89][0-9]{10}$|(?=(?:[0-9]+[- ]){4})[- 0-9]{17}$)(?:97[89][- ]?)?[0-9]{1,5}[- ]?[0-9]+[- ]?[0-9]+[- ]?[0-9X]$")
    private String isbn;
    
	@Enumerated(EnumType.STRING)
	private Format format;
	
	@Positive
	@Digits(integer = 7, fraction = 0)
	private Integer pages;
	
	@Version
	private Long version;	
		
}
