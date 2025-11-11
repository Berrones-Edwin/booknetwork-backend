package com.example.book_network.book;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BookRepository extends JpaRepository<Book, Integer> {

    @Query("""
            SELECT book
            FROM Book b
            WHERE b.archived =false
            AND b.shareable =true
            AND b.owner.id != :userId
            """)
    Page<Book> findAllDispayableBooks(Pageable pageable, Integer userId);

}
