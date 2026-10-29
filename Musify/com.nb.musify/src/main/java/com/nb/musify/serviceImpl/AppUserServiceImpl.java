package com.nb.musify.serviceImpl;

import com.nb.musify.dto.request.AppUserRequest;
import com.nb.musify.dto.response.AppUserResponse;
import com.nb.musify.dto.response.PaginatedResponse;
import com.nb.musify.entity.AppUser;
import com.nb.musify.repository.AppUserRepository;
import com.nb.musify.service.AppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppUserServiceImpl implements AppUserService {

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public AppUserResponse getUserProfile(String email) {
        AppUser appUser = appUserRepository.findByEmail(email)
                .orElseThrow(()->new RuntimeException("User not found"));

        return AppUserResponse.fromEntity(appUser,null,null);
    }

    @Override
    public AppUserResponse updateUserProfile(AppUserRequest request, String email) {
        AppUser appUser = appUserRepository.findByEmail(email)
                .orElseThrow(()->new RuntimeException("User not found"));

        if(request.getName()!=null && !request.getName().trim().isEmpty()){
            appUser.setName(request.getName().trim());
        }

        if(request.getPassword()!=null && !request.getPassword().trim().isEmpty()){
            if (request.getOldPassword()==null || request.getOldPassword().trim().isEmpty()){
                throw new RuntimeException("Old password is required to update password");
            }

            if(!passwordEncoder.matches(request.getOldPassword(), appUser.getPassword())){
                throw new RuntimeException("Old password is incorrect");
            }
            appUser.setPassword(passwordEncoder.encode(request.getPassword()));
        }


        AppUser updatedUser = appUserRepository.save(appUser);
        return AppUserResponse.fromEntity(updatedUser,null,null);

    }

    @Override
    public PaginatedResponse<AppUserResponse> getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page,size);
        Page<AppUser> userPage = appUserRepository.findAll(pageable);

        List<AppUserResponse> userResponses=userPage.getContent().stream()
                .map(user->AppUserResponse.fromEntity(user,null,null))
                .collect(Collectors.toList());

        return new PaginatedResponse<>(
                userResponses,
                userPage.getNumber(),
                userPage.getSize(),
                userPage.getTotalElements(),
                userPage.getTotalPages(),
                userPage.isLast(),
                userPage.isFirst()
        );
    }

    @Override
    public AppUserResponse updateUserRole(Long userId, String role, String email) {
        AppUser adminUser = appUserRepository.findByEmail(email)
                .orElseThrow(()->new RuntimeException("User not found"));

        if(!"ADMIN".equals(adminUser.getRole())){
            throw new RuntimeException("Only admin can update user roles");
        }

        AppUser userToUpdate = appUserRepository.findById(userId)
                .orElseThrow(()->new RuntimeException("User not found"));

        String normalizedRole= role.trim().toUpperCase();
        userToUpdate.setRole(normalizedRole);
        AppUser updatedUser = appUserRepository.save(userToUpdate);

        return AppUserResponse.fromEntity(updatedUser,null,null);
    }
}
