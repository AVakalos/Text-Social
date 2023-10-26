package org.apostolis.model;

/* Request entity object for structured login requests */

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AuthRequest(@NotNull @NotBlank String username, @NotNull @NotBlank String password){ }
