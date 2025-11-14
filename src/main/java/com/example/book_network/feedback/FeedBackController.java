package com.example.book_network.feedback;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.book_network.book.PageResponse;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("api/v1/feedbacks")
@RequiredArgsConstructor
@Tag(name = "FeedBack")
public class FeedBackController {

    private final FeedBackService feedBackService;

    @PostMapping
    public ResponseEntity<Integer> saveFeedBack(
            @Valid @RequestBody FeedBackRequest request,
            Authentication authentication) {

        return ResponseEntity.ok(feedBackService.saveFeedBack(request, authentication));

    }

    @GetMapping("/book/{bookId}")
    public ResponseEntity<PageResponse<FeedBackResponse>> findAllFeedbacksByBook(
            @PathVariable("bookId") Integer bookId,
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication authentication) {
        return ResponseEntity.ok(feedBackService.findAllFeedbacksByBook(bookId, page, size, authentication));
    }

}
