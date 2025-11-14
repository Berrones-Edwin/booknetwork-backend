package com.example.book_network.feedback;

import java.util.Objects;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.example.book_network.book.Book;
import com.example.book_network.book.BookRepository;
import com.example.book_network.exception.OperationNotPermittedException;
import com.example.book_network.user.User;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FeedBackService {

    private final BookRepository bookRepository;
    private final FeedBackRepository feedBackRepository;
    private final FeedBackMapper feedBackMapper;

    public Integer saveFeedBack(FeedBackRequest request, Authentication authentication) {

        Book book = bookRepository.findById(request.bookId())
                .orElseThrow(() -> new EntityNotFoundException("No book found with the ID: " + request.bookId()));
        if (book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException(
                    "You cannot give a feedback  for an  archived or not shareable book");
        }

        User user = ((User) authentication.getPrincipal());

        if (!Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You cannot give a feedback to your own book");
        }

        FeedBack feedBack = feedBackMapper.toFeedBack(request);

        return feedBackRepository.save(feedBack).getId();

    }

}
