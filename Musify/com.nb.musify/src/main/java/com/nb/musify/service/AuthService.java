package com.nb.musify.service;

import com.nb.musify.dto.request.ForgotPasswordRequest;
import com.nb.musify.dto.request.LoginUserRequest;
import com.nb.musify.dto.request.RefreshTokenRequest;
import com.nb.musify.dto.request.RegisterUserRequest;
import com.nb.musify.dto.response.AppUserResponse;
import com.nb.musify.dto.response.MessageResponse;
import jakarta.validation.Valid;

public interface AuthService {
    MessageResponse registerUser(RegisterUserRequest request);

    AppUserResponse loginUser(LoginUserRequest request);

    AppUserResponse refreshAccessToken(RefreshTokenRequest request);

    MessageResponse forgotPassword(ForgotPasswordRequest request);
}
