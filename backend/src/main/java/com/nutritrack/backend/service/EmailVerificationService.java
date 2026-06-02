package com.nutritrack.backend.service;

import com.nutritrack.backend.entity.User;
import org.springframework.stereotype.Service;

/**
 * Stub for future email verification (send link, confirm token, set emailVerified=true).
 */
@Service
public class EmailVerificationService {

    public void registerUnverified(User user) {
        user.setEmailVerified(false);
    }
}
