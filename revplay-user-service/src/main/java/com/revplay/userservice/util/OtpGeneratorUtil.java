package com.revplay.userservice.util;

import java.security.SecureRandom;

public class OtpGeneratorUtil {
    private static final SecureRandom random = new SecureRandom();

    public static String generateOtp() {
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }
}
