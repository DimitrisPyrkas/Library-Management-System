package com.example.Library.Service;

import com.example.Library.Entity.Book;
import com.example.Library.Entity.Transaction;
import com.example.Library.Entity.User;
import com.example.Library.Repository.BookRepository;
import com.example.Library.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/*Service for handling the core logic for managing books.
 *Include operations like borrowing and returning books, enforcing rules such as:
 * - Borrow limit per user
 * - Due date validation
 * - Checking availableCopies
*/

@Service
public class BookService {
    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionService transactionService;

    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    public Book findById(Long id) {
        return bookRepository.findById(id).orElse(null);
    }

    public Book save(Book book) {
        return bookRepository.save(book);
    }

    public void deleteById(Long id) {
        bookRepository.deleteById(id);
    }
    /*
     * Borrow a book if:
     * - It exists and has available copies
     * - The user exists
     * - The user has borrowed less than 3 active books
     * - The user has no overdue books
     *
     * Records the transaction and updates the book's availability
     */

    public Book borrowBook(Long bookId, Long userId) {
        Optional<Book> optionalBook = bookRepository.findById(bookId);
        Optional<User> optionalUser = userRepository.findById(userId);

        //Check if book & user exist
        if (optionalBook.isPresent() && optionalUser.isPresent()) {
            Book book = optionalBook.get();
            User user = optionalUser.get();

            // Check if copies are available
            if (book.getCopiesAvailable() <= 0) {
                return null;
            }

            // Check if user has borrowed 3 active books
            List<Transaction> allTransactions = transactionService.findAll();
            long activeBorrows = allTransactions.stream()
                    .filter(tx -> tx.getUser().getId().equals(userId) && tx.getReturnDate() == null)
                    .count();

            if (activeBorrows >= 3) {
                return null;
            }
            // Check if user has any overdue books
            boolean hasOverdue = allTransactions.stream()
                    .anyMatch(tx ->
                            tx.getUser().getId().equals(userId) &&
                                    tx.getReturnDate() == null &&
                                    LocalDate.now().isAfter(tx.getDueDate())
                    );

            if (hasOverdue) {
                return null;
            }

            // Update book and save
            book.setCopiesAvailable(book.getCopiesAvailable() - 1);
            book.setBorrowedBy(user);
            bookRepository.save(book);

            // Save transaction
            Transaction tx = new Transaction();
            tx.setUser(user);
            tx.setBook(book);
            tx.setBorrowDate(LocalDate.now());
            tx.setDueDate(LocalDate.now().plusDays(10));//10 days limit
            tx.setOverdue(false);

            transactionService.save(tx);

            return book;
        }

        return null;
    }

    public Book returnBook(Long bookId) {
        Optional<Book> optionalBook = bookRepository.findById(bookId);
        if (optionalBook.isPresent()) {
            Book book = optionalBook.get();

            // Ensure it was borrowed
            if (book.getBorrowedBy() != null) {
                book.setCopiesAvailable(book.getCopiesAvailable() + 1);
                book.setBorrowedBy(null);
                bookRepository.save(book);

                // Find the most recent active transaction
                List<Transaction> transactions = transactionService.findAll();
                for (int i = transactions.size() - 1; i >= 0; i--) {
                    Transaction tx = transactions.get(i);
                    if (tx.getBook().getId().equals(bookId) && tx.getReturnDate() == null) {
                        LocalDate now = LocalDate.now();
                        tx.setReturnDate(now);

                        //Check for overdue
                        if (now.isAfter(tx.getDueDate())) {
                            tx.setOverdue(true);
                            System.out.println("Book was returned late.");
                        } else {
                            tx.setOverdue(false);
                        }

                        transactionService.save(tx);
                        break;
                    }
                }

                return book;
            }
        }

        return null;
    }
}
