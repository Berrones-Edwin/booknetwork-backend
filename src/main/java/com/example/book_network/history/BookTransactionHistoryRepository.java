package com.example.book_network.history;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BookTransactionHistoryRepository extends JpaRepository<BookTransactionHistory, Integer> {

        @Query("""
                        SELECT h
                        FROM BookTransactionHistory h
                        WHERE h.user.id = :id
                        """)
        Page<BookTransactionHistory> finAllBorrowedBooks(Pageable pageable, Integer id);

        @Query("""
                        SELECT h
                        FROM BookTransactionHistory h
                        WHERE h.book.owner.id = :id
                        """)
        Page<BookTransactionHistory> finAllReturnedBooks(Pageable pageable, Integer id);

        @Query("""
                        SELECT
                        COUNT(COUNT(*)>0) AS isBorrowed
                        FROM BookTransactionHistory h
                        WHERE h.user.id = :id
                        AND h.book.id = :bookId
                        AND h.returnApproved = false
                        """)
        boolean isAlreadyBorrowedByUser(Integer bookId, Integer id);

        @Query("""
                        SELECT h
                        FROM BookTransactionHistory h
                        WHERE h.user.id = :id
                        AND h.book.id = :bookId
                        AND h.returned =false
                        AND h.returnedSpproved =false
                        """)
        Optional<BookTransactionHistory> findByBookIdAndUserId(Integer bookId, Integer id);

}
