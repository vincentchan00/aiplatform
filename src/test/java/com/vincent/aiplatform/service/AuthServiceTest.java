package com.vincent.aiplatform.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.vincent.aiplatform.dto.LoginRequest;
import com.vincent.aiplatform.dto.RegisterRequest;
import com.vincent.aiplatform.entity.Role;
import com.vincent.aiplatform.entity.Tenant;
import com.vincent.aiplatform.entity.User;
import com.vincent.aiplatform.repository.TenantRepository;
import com.vincent.aiplatform.repository.UserRepository;
import com.vincent.aiplatform.security.JwtService;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock UserRepository userRepository;
    @Mock TenantRepository tenantRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock JwtService jwtService;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(userRepository, tenantRepository, passwordEncoder, jwtService);
    }

    @Test
    void registerHashesPasswordAndReturnsToken() {
        UUID tenantId = UUID.randomUUID();
        Tenant tenant = new Tenant();
        tenant.setId(tenantId);
        when(tenantRepository.findById(tenantId)).thenReturn(Optional.of(tenant));
        when(passwordEncoder.encode("password123")).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(UUID.randomUUID());
            return user;
        });
        when(jwtService.generateToken(any(User.class))).thenReturn("jwt");
        when(jwtService.getExpirationSeconds()).thenReturn(3600L);

        var result = authService.register(new RegisterRequest("USER@EXAMPLE.COM", "password123", tenantId));

        assertEquals("jwt", result.token());
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertEquals("user@example.com", captor.getValue().getEmail());
        assertEquals("hashed", captor.getValue().getPassword());
        assertEquals(Role.USER, captor.getValue().getRole());
    }

    @Test
    void loginRejectsWrongPassword() {
        UUID tenantId = UUID.randomUUID();
        User user = new User();
        user.setPassword("hashed");
        when(userRepository.findByEmailAndTenantId("user@example.com", tenantId))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong-password", "hashed")).thenReturn(false);

        assertThrows(ResponseStatusException.class,
                () -> authService.login(new LoginRequest("user@example.com", "wrong-password", tenantId)));
    }
}
