package org.apostolis.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateLinkRequest(@Positive int user,@Positive int post) { }