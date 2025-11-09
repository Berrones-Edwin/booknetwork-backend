package com.example.book_network.auth;

import java.security.SecureRandom;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.book_network.email.EmailService;
import com.example.book_network.email.EmailTemplateName;
import com.example.book_network.role.RoleRepository;
import com.example.book_network.user.User;
import com.example.book_network.user.UserRepository;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    @Value("${mailing.frontend.activation-url}")
    private String activationUrl;

    private final RoleRepository roleRepository;

    private PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    private final EmailService emailService;

    public void register(RegistrationRequest request) throws MessagingException {
        var userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new IllegalStateException("Role user was not initialized"));

        var user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .accountLocked(false)
                .enabled(false)
                .roles(List.of(userRole))
                .build();

        userRepository.save(user);
        sendValidationEmail(user);

    }

    private void sendValidationEmail(User user) throws MessagingException {

        var newToken = generateAndSaveActivationToken(user);
        emailService.sendEmail(
                user.getEmail(),
                user.getFullName(),
                EmailTemplateName.ACTIVATE_ACCOUNT,
                activationUrl,
                newToken,
                "Account activation");

    }

    private String generateAndSaveActivationToken(User user) {

        String generateToken = generateActivationCode(6);
        return generateToken;
    }

    private String generateActivationCode(int len) {

        String characters = "0123456789";
        StringBuilder codeBuilder = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();

        for (int i = 0; i < len; i++) {

            int randomIndex = secureRandom.nextInt(characters.length());
            codeBuilder.append(characters.charAt(randomIndex));
        }

        return codeBuilder.toString();

    }

}
