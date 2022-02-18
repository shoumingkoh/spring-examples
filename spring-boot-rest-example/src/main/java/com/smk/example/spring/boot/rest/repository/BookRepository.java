package com.smk.example.spring.boot.rest.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.smk.example.spring.boot.rest.entity.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
	
	public Optional<Book> findByTitle(String title);
	
	public Optional<Book> findByTitleAndAuthor(String title, String author);

}
