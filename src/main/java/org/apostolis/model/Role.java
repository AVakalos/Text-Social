package org.apostolis.model;

import io.javalin.security.RouteRole;

public enum Role implements RouteRole {
    FREE,
    PREMIUM
}
