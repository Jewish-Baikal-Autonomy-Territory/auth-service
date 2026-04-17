package org.example.ai_auth_service.util;

import java.security.SecureRandom;

public class VerificationCodeGenerator {
    private static final String CHARACTERS="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int CODE_LENGTH = 6;
    private static final SecureRandom random = new SecureRandom();

    public String generateCode(){
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(CHARACTERS.charAt((random.nextInt(CHARACTERS.length()))));
        }
        return code.toString();
    }
}
