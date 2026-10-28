package com.nb.musify.service;

import com.nb.musify.dto.request.AppUserRequest;
import com.nb.musify.dto.response.AppUserResponse;
import com.nb.musify.dto.response.PaginatedResponse;
import jakarta.validation.Valid;

public interface AppUserService {
    AppUserResponse getUserProfile(String email);

    AppUserResponse updateUserProfile(AppUserRequest request, String email);

    PaginatedResponse<AppUserResponse> getAllUsers(int page, int size);
}
