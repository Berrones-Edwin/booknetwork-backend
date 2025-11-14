package com.example.book_network.feedback;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FeedBackRepository extends JpaRepository<FeedBack, Integer> {

    @Query("""
            SELECT f
            FROM FeedBack f
            WHERE f.book.id = :bookId
            """)
    Page<FeedBack> findAllByBookId(Integer bookId, Pageable pageable);

}
