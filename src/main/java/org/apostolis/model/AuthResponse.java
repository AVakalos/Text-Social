package org.apostolis.model;

/* Response entity object for structured login responses  */

public record AuthResponse(
    String username,
    String token,
    String message,
    int status
){ }