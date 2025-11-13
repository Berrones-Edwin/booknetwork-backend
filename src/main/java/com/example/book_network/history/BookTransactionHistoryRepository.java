package com.example.book_network.history;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BookTransactionHistoryRepository extends JpaRepository<BookTransactionHistory, Integer> {

    @Query("""
            SELECT history
            FROM BookTransactionHistory h
            WHERE h.user.id = :id
            """)
    Page<BookTransactionHistory> finAllBorrowedBooks(Pageable pageable, Integer id);

}
