package com.example.Library.Controller;

import com.example.Library.Entity.Book;
import com.example.Library.Entity.User;
import com.example.Library.Repository.BookRepository;
import com.example.Library.Service.BookService;
import com.example.Library.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

//Controller for managing books in library

@RestController
@RequestMapping("/api/books")
public class BookController {
    @Autowired
    private BookService bookService;

    @Autowired
    private UserService userService;

    @Autowired
    private BookRepository bookRepository;

    @GetMapping
    public List<Book> getAllBooks() {
        return bookService.findAll();
    }

    @GetMapping("/{id}")
    public Book getBook(@PathVariable Long id) {
        return bookService.findById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> addBook(@AuthenticationPrincipal User user, @RequestBody Book book) {
        Optional<Book> existingBook = bookRepository.findByTitleAndAuthor(book.getTitle(), book.getAuthor());
        if (existingBook.isPresent()) {
            return ResponseEntity.status(409).body("Book already exists. Use update to add more copies.");
        }

        book.setTotalCopies(book.getCopiesAvailable());
        Book savedBook = bookService.save(book);
        return ResponseEntity.ok(savedBook);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateBook(@PathVariable Long id, @RequestBody Map<String, Integer> request) {
        Book book = bookService.findById(id);
        if (book == null) {
            return ResponseEntity.notFound().build();
        }

        Integer addCopies = request.get("copiesAvailable");
        if (addCopies == null || addCopies <= 0) {
            return ResponseEntity.badRequest().body("Invalid number of copies to add.");
        }

        book.setCopiesAvailable(book.getCopiesAvailable() + addCopies);
        book.setTotalCopies(book.getTotalCopies() + addCopies);
        Book updatedBook = bookService.save(book);

        return ResponseEntity.ok(updatedBook);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBook(@PathVariable Long id) {
        bookService.deleteById(id);
        return ResponseEntity.ok().body("Book deleted");
    }
    @PreAuthorize("hasRole('MEMBER')")
    @PostMapping("/{bookId}/borrow")
    public ResponseEntity<?> borrowBook(@PathVariable Long bookId, @AuthenticationPrincipal User user) {
        Book borrowedBook = bookService.borrowBook(bookId, user.getId());
        if (borrowedBook != null) {
            return ResponseEntity.ok(borrowedBook);
        } else {
            return ResponseEntity.badRequest().body("Book not available or does not exist.");
        }
    }

    @PreAuthorize("hasRole('MEMBER')")
    @PostMapping("/{bookId}/return")
    public ResponseEntity<?> returnBook(@PathVariable Long bookId, @AuthenticationPrincipal User user) {
        Book returnedBook = bookService.returnBook(bookId);
        if (returnedBook != null) {
            return ResponseEntity.ok(returnedBook);
        } else {
            return ResponseEntity.badRequest().body("Book not borrowed or invalid book ID.");
        }
    }
}
