package com.sandeep.eventrabackend.service;

import com.sandeep.eventrabackend.dto.request.UpdateUserProfileRequest;
import com.sandeep.eventrabackend.dto.response.UserProfileResponse;
import com.sandeep.eventrabackend.model.User;
import com.sandeep.eventrabackend.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public UserProfileResponse updateProfile(
            String authenticatedEmail,
            UpdateUserProfileRequest request
    ) {

        User user = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "Authenticated user not found"));

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());

        User updatedUser = userRepository.save(user);

        return mapToProfileResponse(updatedUser);
    }

    private UserProfileResponse mapToProfileResponse(User user) {

        return UserProfileResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}