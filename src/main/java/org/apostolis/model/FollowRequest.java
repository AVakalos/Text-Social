package org.apostolis.model;

import jakarta.validation.constraints.Positive;

public record FollowRequest(@Positive int user, @Positive int follows) { }
