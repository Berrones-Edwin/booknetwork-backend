package com.example.book_network.feedback;

import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.example.book_network.book.Book;
import com.example.book_network.book.BookRepository;
import com.example.book_network.book.PageResponse;
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

    public PageResponse<FeedBackResponse> findAllFeedbacksByBook(Integer bookId, int page, int size,
            Authentication authentication) {
        Pageable pageable = PageRequest.of(page, size);
        User user = ((User) authentication.getPrincipal());
        Page<FeedBack> feedBacks = feedBackRepository.findAllByBookId(bookId, pageable);

        List<FeedBackResponse> feedBackResponses = feedBacks.stream()
                .map(f -> feedBackMapper.toFeedBackResponse(f, user.getId()))
                .toList();

                return new PageResponse<>(
                    feedBackResponses,
                    feedBacks.getNumber(),
                    feedBacks.getSize(),
                    feedBacks.getTotalElements(),
                    feedBacks.getTotalPages(),
                    feedBacks.isFirst(),
                    feedBacks.isLast()
                );
    }

}
