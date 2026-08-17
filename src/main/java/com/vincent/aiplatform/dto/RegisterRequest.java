package com.vincent.aiplatform.dto;

import java.util.UUID;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;

public record RegisterRequest(
@NotBlank
@Email
String email,

@NotBlank
@Size(min = 8, max = 100)
String password,

@NotNull
UUID tenantId){}
