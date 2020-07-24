package com.cranajit.Blog.services;

import com.cranajit.Blog.dto.LoginRequest;
import com.cranajit.Blog.dto.LoginResponse;
import com.cranajit.Blog.dto.RegisterRequest;
import com.cranajit.Blog.exception.BloggingException;
import com.cranajit.Blog.models.NotificationEmail;
import com.cranajit.Blog.models.User;
import com.cranajit.Blog.models.VerificationToken;
import com.cranajit.Blog.reposetories.UserRepository;
import com.cranajit.Blog.reposetories.VerificationTokenRepository;
import com.cranajit.Blog.security.JwtProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final MailService mailService;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final UserDetailsService userDetailsService;

    @Transactional
    public void signup(RegisterRequest registerRequest) {
        User user = new User();
        user.setUserName(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setCreatedDate(Instant.now());
        user.setEnabled(false);
        userRepository.save(user);

        String token = generateVerificationToken(user);
        mailService.sendEmail(new NotificationEmail("Please Click on the below mentioned url to activate your account \n " +
                "http://localhost:8080/api/auth/accountverification/" + token, "PLEASE ACTIVATE YOUR ACCOUNT", user.getEmail()));
    }

    public String generateVerificationToken(User user) {
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);

        verificationTokenRepository.save(verificationToken);
        return token;
    }

    public void verifyAccount(String token) {
        Optional<VerificationToken> verificationToken = verificationTokenRepository.findByToken(token);
        verificationToken.orElseThrow(() -> new BloggingException("invalid Token"));
        fetchUserAndEnable(verificationToken.get());
    }

    @Transactional
    private void fetchUserAndEnable(VerificationToken verificationToken) {
        String username = verificationToken.getUser().getUserName();
        User user = userRepository.findByUserName(username).orElseThrow(() -> new BloggingException("no user found: " + username));
        user.setEnabled(true);
        userRepository.save(user);
    }

    public LoginResponse login(LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
                    loginRequest.getPassword()));
        } catch (BadCredentialsException e) {
            System.out.println("Error");
            throw new BloggingException("Credentials are not correct");
        }
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());
        String jwtToken = jwtProvider.generateToken(userDetails);
        System.out.println(jwtToken);
        return LoginResponse.builder().username(userDetails.getUsername()).authToken(jwtToken).build();
    }
}
