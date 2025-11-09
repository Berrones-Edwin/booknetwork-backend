package com.example.book_network.auth;

import java.time.LocalDateTime;
// import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

// import com.example.book_network.email.EmailService;
import com.example.book_network.role.RoleRepository;
import com.example.book_network.security.JwtService;
import com.example.book_network.user.Token;
import com.example.book_network.user.TokenRepository;
import com.example.book_network.user.User;
import com.example.book_network.user.UserRepository;

import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    @Value("${mailing.frontend.activation-url}")
    private String activationUrl;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    // private final EmailService emailService;

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    private final TokenRepository tokenRepository;

    public void register(RegistrationRequest request) throws MessagingException {
        var userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new IllegalStateException("Role user was not initialized"));

        var user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .accountLocked(false)
                .enabled(true)
                .roles(List.of(userRole))
                .build();

        userRepository.save(user);
        sendValidationEmail(user);

    }

    private void sendValidationEmail(User user) throws MessagingException {

        // var newToken = generateAndSaveActivationToken(user);
        // emailService.sendEmail(
        // user.getEmail(),
        // user.getFullName(),
        // EmailTemplateName.ACTIVATE_ACCOUNT,
        // activationUrl,
        // newToken,
        // "Account activation");

    }

    // private String generateAndSaveActivationToken(User user) {

    // String generateToken = generateActivationCode(6);
    // return generateToken;
    // }

    // private String generateActivationCode(int len) {

    // String characters = "0123456789";
    // StringBuilder codeBuilder = new StringBuilder();
    // SecureRandom secureRandom = new SecureRandom();

    // for (int i = 0; i < len; i++) {

    // int randomIndex = secureRandom.nextInt(characters.length());
    // codeBuilder.append(characters.charAt(randomIndex));
    // }

    // return codeBuilder.toString();

    // }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {

        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        var claims = new HashMap<String, Object>();
        var user = ((User) auth.getPrincipal());
        claims.put("fullname", user.getFullName());
        var jwtToken = jwtService.generateToken(claims, user);

        return AuthenticationResponse
                .builder()
                .token(jwtToken)
                .build();
    }

    @Transactional
    public void activateAccount(String token) throws MessagingException {

        Token saveToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid Token"));

        if (LocalDateTime.now().isAfter((saveToken.getExpiresAt()))) {

            sendValidationEmail(saveToken.getUser());

            throw new RuntimeException("Activation token has expired. A new token has been sent");
        }

        var user = userRepository.findById(saveToken.getUser().getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        user.setEnabled(true);
        userRepository.save(user);
        saveToken.setValidateAt(LocalDateTime.now());
        tokenRepository.save(saveToken);
    }

}
