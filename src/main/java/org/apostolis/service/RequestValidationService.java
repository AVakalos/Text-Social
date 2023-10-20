package org.apostolis.service;

import java.util.ArrayList;

public interface RequestValidationService {
    int extractUserId(String token);
    String extractUsername(int user_id);
    ArrayList<Integer> getUsersPostIds(int user_id);
}
