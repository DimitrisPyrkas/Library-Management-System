package com.example.Library.Repository;

import com.example.Library.Entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

//BookRepository is the data access layer for the Book Entity

public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findByTitleAndAuthor(String title, String author);
}
