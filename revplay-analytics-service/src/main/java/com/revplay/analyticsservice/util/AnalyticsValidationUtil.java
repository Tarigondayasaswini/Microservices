package com.revplay.analyticsservice.util;
 
import com.revplay.analyticsservice.exception.AnalyticsValidationException;
 
public final class AnalyticsValidationUtil {
 
    private AnalyticsValidationUtil() {
    }
 
    public static void requireLimitInRange(int limit, int min, int max) {
        if (limit < min || limit > max) {
            throw new AnalyticsValidationException("limit must be between " + min + " and " + max);
        }
    }
}
