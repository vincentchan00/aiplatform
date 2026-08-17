package com.vincent.aiplatform.service;

import com.vincent.aiplatform.dto.AuthResponse;
import com.vincent.aiplatform.dto.LoginRequest;
import com.vincent.aiplatform.dto.RegisterRequest;
import com.vincent.aiplatform.entity.Role;
import com.vincent.aiplatform.entity.Tenant;
import com.vincent.aiplatform.entity.User;
import com.vincent.aiplatform.repository.TenantRepository;
import com.vincent.aiplatform.repository.UserRepository;
import com.vincent.aiplatform.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, TenantRepository tenantRepository,
                       PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.tenantRepository = tenantRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        Tenant tenant = tenantRepository.findById(request.tenantId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tenant not found"));
        String email = request.email().trim().toLowerCase();
        if (userRepository.existsByEmailAndTenantId(email, request.tenantId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already registered for this tenant");
        }
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(Role.USER);
        user.setTenant(tenant);
        return responseFor(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmailAndTenantId(
                        request.email().trim().toLowerCase(), request.tenantId())
                .orElseThrow(this::unauthorized);
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw unauthorized();
        }
        return responseFor(user);
    }

    private AuthResponse responseFor(User user) {
        return new AuthResponse(jwtService.generateToken(user), jwtService.getExpirationSeconds());
    }

    private ResponseStatusException unauthorized() {
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
    }
}
