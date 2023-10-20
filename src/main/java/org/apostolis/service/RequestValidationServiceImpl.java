package org.apostolis.service;

import org.apostolis.repository.OperationsRepository;
import org.apostolis.repository.UserRepository;
import org.apostolis.security.TokenManager;

import java.util.ArrayList;

/* This class implements the retrieval of user and post ids, to validate that
the authenticated user can not perform operations on other user's data or with different identity.  */

public class RequestValidationServiceImpl implements RequestValidationService {

    private final TokenManager tokenManager;
    private final UserRepository userRepository;
    private final OperationsRepository operationsRepository;

    public RequestValidationServiceImpl(TokenManager tokenManager, UserRepository userRepository, OperationsRepository operationsRepository) {
        this.tokenManager = tokenManager;
        this.userRepository = userRepository;
        this.operationsRepository = operationsRepository;
    }

    @Override
    public int extractUserId(String token) {
        String username = tokenManager.extractUserId(token);
        return userRepository.getUserIdFromUsername(username);
    }

    @Override
    public String extractUsername(int user_id) {
        return userRepository.getUsernameFromId(user_id);
    }

    @Override
    public ArrayList<Integer> getUsersPostIds(int user_id) {
        return operationsRepository.getPostIds(user_id);
    }


}
