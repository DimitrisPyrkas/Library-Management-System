package com.example.Library.Controller;

import com.example.Library.Entity.Transaction;
import com.example.Library.Entity.User;
import com.example.Library.Service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

//Controller for handling book borrowing transaction history

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    @Autowired
    private TransactionService transactionService;


    //Admins only: view all transactions
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<Transaction> getAll() {
        return transactionService.findAll();
    }


    //Admins only: view only overdue transactions
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/overdue")
    public List<Transaction> getOverdueTransactions() {
        return transactionService.findAll().stream()
                .filter(Transaction::isOverdue)
                .collect(Collectors.toList());
    }


    //Members only: view their own borrowing history
    @PreAuthorize("hasRole('MEMBER')")
    @GetMapping("/me")
    public List<Transaction> getMyTransactions(@AuthenticationPrincipal User user) {
        return transactionService.getTransactionsByUser(user.getId());
    }
}
