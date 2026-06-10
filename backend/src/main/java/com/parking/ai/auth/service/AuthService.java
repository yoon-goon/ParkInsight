package com.parking.ai.auth.service;

import com.parking.ai.auth.dto.AuthResponse;
import com.parking.ai.auth.dto.LoginRequest;
import com.parking.ai.auth.dto.SignupRequest;
import com.parking.ai.auth.entity.User;
import com.parking.ai.auth.repository.UserRepository;
import com.parking.ai.auth.security.JwtProvider;
import com.parking.ai.common.BusinessException;
import com.parking.ai.common.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Transactional
    public AuthResponse signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .build();

        user = userRepository.save(user);
        return new AuthResponse(jwtProvider.generate(user.getId()), user.getName(), user.getEmail());
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
        }

        return new AuthResponse(jwtProvider.generate(user.getId()), user.getName(), user.getEmail());
    }
}
