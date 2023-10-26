package org.apostolis.model;

import jakarta.validation.constraints.Positive;

public record UnfollowRequest(@Positive int user, @Positive int unfollows){ }