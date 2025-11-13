package com.example.book_network.book;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.example.book_network.history.BookTransactionHistory;
import com.example.book_network.history.BookTransactionHistoryRepository;
import com.example.book_network.user.User;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookService {

        private final BookMapper bookMapper;
        private final BookRepository bookRepository;
        private final BookTransactionHistoryRepository bookTransactionHistoryRepository;

        public Integer save(BookRequest request, Authentication authentication) {
                User user = ((User) authentication.getPrincipal());
                Book book = bookMapper.toBook(request);
                book.setOwner(user);
                return bookRepository.save(book).getId();
        }

        public BookResponse findBookById(Integer bookId) {

                return bookRepository.findById(bookId)
                                .map(bookMapper::toBookResponse)
                                .orElseThrow(() -> new EntityNotFoundException("No book found with the ID: " + bookId));

        }

        public PageResponse<BookResponse> findAllBooks(int page, int size, Authentication authentication) {

                User user = ((User) authentication.getPrincipal());
                Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
                Page<Book> books = bookRepository.findAllDispayableBooks(pageable, user.getId());
                List<BookResponse> bookResponse = books.stream()
                                .map(bookMapper::toBookResponse)
                                .toList();

                return new PageResponse<>(
                                bookResponse,
                                books.getNumber(),
                                books.getSize(),
                                books.getTotalElements(),
                                books.getTotalPages(),
                                books.isFirst(),
                                books.isLast());

        }

        public PageResponse<BookResponse> findAllBooksByOwner(int page, int size, Authentication authentication) {
                User user = ((User) authentication.getPrincipal());
                Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());

                Page<Book> books = bookRepository.findAll(
                                BookSpecification.withOwnerId(user.getId()),
                                pageable);

                List<BookResponse> bookResponse = books.stream()
                                .map(bookMapper::toBookResponse)
                                .toList();

                return new PageResponse<>(
                                bookResponse,
                                books.getNumber(),
                                books.getSize(),
                                books.getTotalElements(),
                                books.getTotalPages(),
                                books.isFirst(),
                                books.isLast());
        }

        public PageResponse<BorrowedBookResponse> findAllBorrowedBooks(int page, int size,
                        Authentication authentication) {
                User user = ((User) authentication.getPrincipal());
                Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
                Page<BookTransactionHistory> allBorrowedBooks = bookTransactionHistoryRepository
                                .finAllBorrowedBooks(pageable, user.getId());

                List<BorrowedBookResponse> bookResponse = allBorrowedBooks.stream()
                                .map(bookMapper::toBorrowedBookResponse)
                                .toList();

                return new PageResponse<>(
                                bookResponse,
                                allBorrowedBooks.getNumber(),
                                allBorrowedBooks.getSize(),
                                allBorrowedBooks.getTotalElements(),
                                allBorrowedBooks.getTotalPages(),
                                allBorrowedBooks.isFirst(),
                                allBorrowedBooks.isLast());
        }

        public PageResponse<BorrowedBookResponse> findAllReturnedBooks(int page, int size,
                        Authentication authentication) {

                User user = ((User) authentication.getPrincipal());
                Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
                Page<BookTransactionHistory> allBorrowedBooks = bookTransactionHistoryRepository
                                .finAllReturnedBooks(pageable, user.getId());

                List<BorrowedBookResponse> bookResponse = allBorrowedBooks.stream()
                                .map(bookMapper::toBorrowedBookResponse)
                                .toList();

                return new PageResponse<>(
                                bookResponse,
                                allBorrowedBooks.getNumber(),
                                allBorrowedBooks.getSize(),
                                allBorrowedBooks.getTotalElements(),
                                allBorrowedBooks.getTotalPages(),
                                allBorrowedBooks.isFirst(),
                                allBorrowedBooks.isLast());
        }

}
