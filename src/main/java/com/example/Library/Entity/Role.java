package com.example.Library.Entity;

//Enum representing the role a user can have in the library system

public enum Role {
    ADMIN,//Has full access (add/update/delete books, view all transactions)
    MEMBER//Can borrow/return books and view personal transactions
}
