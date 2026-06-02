package org.example.ai_auth_service.services;

import org.example.ai_auth_service.entity.UserVerification;
import org.example.ai_auth_service.repository.UserVerificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VerificationService {

    @Autowired
    private UserVerificationRepository verificationRepository;

    public boolean verifyCode(String email, String code){
        UserVerification userVerification = verificationRepository.findByEmail(email);
        if (userVerification != null && userVerification.getVerificationCode().equals(code)){
            return true;
        }
        return false;
    }
}
