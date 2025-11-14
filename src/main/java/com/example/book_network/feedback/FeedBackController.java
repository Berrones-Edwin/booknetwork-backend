package com.example.book_network.feedback;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

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

}
