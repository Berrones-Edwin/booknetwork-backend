package com.example.book_network.book;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.example.book_network.user.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookMapper bookMapper;
    private final BookRepository bookRepository;

    public Integer save(BookRequest request, Authentication authentication) {
        User user = ((User) authentication.getPrincipal());
        Book book = bookMapper.toBook(request);
        book.setOwner(user);
        return bookRepository.save(book).getId();
    }

}
