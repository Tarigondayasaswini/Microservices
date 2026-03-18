package com.revplay.analyticsservice.enums;

import com.revplay.analyticsservice.exception.AnalyticsValidationException;

public enum TrendRange {
    DAILY,
    WEEKLY,
    MONTHLY;

    public static TrendRange from(String value) {
        try {
            return TrendRange.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new AnalyticsValidationException("range must be one of: daily, weekly, monthly");
        }
    }
}




