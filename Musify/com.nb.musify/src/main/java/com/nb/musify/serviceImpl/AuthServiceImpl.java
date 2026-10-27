package com.nb.musify.serviceImpl;

import com.nb.musify.dto.request.LoginUserRequest;
import com.nb.musify.dto.request.RefreshTokenRequest;
import com.nb.musify.dto.request.RegisterUserRequest;
import com.nb.musify.dto.response.AppUserResponse;
import com.nb.musify.dto.response.MessageResponse;
import com.nb.musify.entity.AppUser;
import com.nb.musify.exception.EmailAlreadyExistsException;
import com.nb.musify.exception.InvalidCredentialsException;
import com.nb.musify.repository.AppUserRepository;
import com.nb.musify.service.AuthService;
import com.nb.musify.service.EmailService;
import com.nb.musify.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Optional;


@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private JwtUtil jwtUtil ;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Override
    public MessageResponse registerUser(RegisterUserRequest request) {
        if(appUserRepository.existsByEmail(request.getEmail())){
            throw new EmailAlreadyExistsException("Email already exists");
        }
        String tempPassword = generateTemporaryPassword();
        AppUser appUser= new AppUser();
        appUser.setName(request.getName());
        appUser.setEmail(request.getEmail());
        appUser.setPassword(passwordEncoder.encode(tempPassword));
        appUser.setRole(request.getRole()!=null ? request.getRole() : "USER");
        appUserRepository.save(appUser);
        emailService.sendWelcomeEmail(appUser.getEmail(), appUser.getName(), tempPassword);

        return new MessageResponse("Account created successfully. A temporary password has been sent to your email.");
    }

    @Override
    public AppUserResponse loginUser(LoginUserRequest request) {
        AppUser appUser = appUserRepository.findByEmail(request.getEmail())
                .orElseThrow(()->new InvalidCredentialsException("Invalid email or password"));

        if(!passwordEncoder.matches(request.getPassword(), appUser.getPassword())){
            throw new InvalidCredentialsException("Invalid email or password");
        }
        String accessToken = jwtUtil.generateAccessToken(appUser.getId(),appUser.getName(),appUser.getEmail(), appUser.getRole());
        String refreshToken=jwtUtil.generateRefreshToken(appUser.getId(),appUser.getEmail());

        appUser.setRefreshToken(refreshToken);
        appUserRepository.save(appUser);

        return AppUserResponse.fromEntity(appUser,accessToken,refreshToken);

    }

    @Override
    public AppUserResponse refreshAccessToken(RefreshTokenRequest request) {
        String refreshToken = request

    }

    private String generateTemporaryPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$%";

        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(10);

        for (int i = 0; i < 10; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }

        return password.toString();
    }
}
