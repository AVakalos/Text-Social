package org.apostolis.model;

import io.javalin.security.RouteRole;

/* The user roles */

public enum Role implements RouteRole {
    FREE,
    PREMIUM
}
