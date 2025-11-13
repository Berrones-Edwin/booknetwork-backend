package com.example.book_network.handler;

import java.util.HashSet;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.book_network.exception.OperationNotPermittedException;

import jakarta.mail.MessagingException;

@RestControllerAdvice
public class GlobalExceptionHandler {

        @ExceptionHandler(LockedException.class)
        public ResponseEntity<ExceptionResponse> handleException(LockedException exception) {

                return ResponseEntity
                                .status(HttpStatus.UNAUTHORIZED)
                                .body(
                                                ExceptionResponse
                                                                .builder()
                                                                .businessErrorCode(BusinessErrorCode.ACCOUNT_LOCKED
                                                                                .getCode())
                                                                .businessErrorDescription(
                                                                                BusinessErrorCode.ACCOUNT_LOCKED
                                                                                                .getDescription())
                                                                .error(exception.getMessage())
                                                                .build());

        }

        @ExceptionHandler(DisabledException.class)
        public ResponseEntity<ExceptionResponse> handleException(DisabledException exception) {

                return ResponseEntity
                                .status(HttpStatus.UNAUTHORIZED)
                                .body(
                                                ExceptionResponse
                                                                .builder()
                                                                .businessErrorCode(BusinessErrorCode.ACCOUNT_DISABLED
                                                                                .getCode())
                                                                .businessErrorDescription(
                                                                                BusinessErrorCode.ACCOUNT_DISABLED
                                                                                                .getDescription())
                                                                .error(exception.getMessage())
                                                                .build());

        }

        @ExceptionHandler(MessagingException.class)
        public ResponseEntity<ExceptionResponse> handleException(MessagingException exception) {

                return ResponseEntity
                                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(
                                                ExceptionResponse
                                                                .builder()
                                                                .error(exception.getMessage())
                                                                .build());

        }

        @ExceptionHandler(BadCredentialsException.class)
        public ResponseEntity<ExceptionResponse> handleException(BadCredentialsException exception) {

                return ResponseEntity
                                .status(HttpStatus.UNAUTHORIZED)
                                .body(
                                                ExceptionResponse
                                                                .builder()
                                                                .businessErrorCode(BusinessErrorCode.BAD_CREDENTIALS
                                                                                .getCode())
                                                                .businessErrorDescription(
                                                                                BusinessErrorCode.BAD_CREDENTIALS
                                                                                                .getDescription())
                                                                .error(BusinessErrorCode.BAD_CREDENTIALS
                                                                                .getDescription())
                                                                .build());

        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ExceptionResponse> handleException(MethodArgumentNotValidException exception) {

                Set<String> errors = new HashSet<>();
                exception.getBindingResult()
                                .getAllErrors()
                                .forEach(error -> {
                                        var errorMessage = error.getDefaultMessage();
                                        errors.add(errorMessage);
                                });
                ;
                return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(
                                                ExceptionResponse
                                                                .builder()
                                                                .validationErrors(errors)
                                                                .build());

        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ExceptionResponse> handleException(Exception exception) {

                exception.printStackTrace();
                return ResponseEntity
                                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(
                                                ExceptionResponse
                                                                .builder()
                                                                .businessErrorDescription(
                                                                                "Internal error, contact the admin")
                                                                .error(exception.getMessage())
                                                                .build());

        }

        @ExceptionHandler(OperationNotPermittedException.class)
        public ResponseEntity<ExceptionResponse> handleException(OperationNotPermittedException exception) {

                return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(
                                                ExceptionResponse
                                                                .builder()
                                                                .error(exception.getMessage())
                                                                .build());

        }

}
