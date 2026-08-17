package com.vincent.aiplatform.security;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.vincent.aiplatform.entity.Role;
import com.vincent.aiplatform.entity.Tenant;
import com.vincent.aiplatform.entity.User;
import java.time.Duration;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class JwtServiceTest {
    private final JwtService jwtService = new JwtService(
            "a-test-secret-that-is-longer-than-thirty-two-bytes", Duration.ofHours(1));

    @Test
    void generatedTokenContainsTenantAndRoleClaims() {
        Tenant tenant = new Tenant();
        tenant.setId(UUID.randomUUID());
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("user@example.com");
        user.setRole(Role.USER);
        user.setTenant(tenant);

        var claims = jwtService.parseToken(jwtService.generateToken(user));

        assertEquals("user@example.com", claims.getSubject());
        assertEquals(tenant.getId().toString(), claims.get("tenantId"));
        assertEquals("USER", claims.get("role"));
    }
}
