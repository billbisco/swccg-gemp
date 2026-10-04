package com.gempukku.swccgo.hall;

/**
 * Casual hall Game Timer presets, matching LOTR GEMP's hall.html options:
 * time bank minutes / per-action timeout minutes.
 */
public enum HallGameTimer {
    DEFAULT("default", 45, 6),
    BLITZ("blitz", 25, 3),
    WC("WC", 20, 10),
    SLOW("slow", 80, 10),
    GLACIAL("glacial", 1440, 1440);

    private final String _code;
    private final int _bankMinutes;
    private final int _decisionMinutes;

    HallGameTimer(String code, int bankMinutes, int decisionMinutes) {
        _code = code;
        _bankMinutes = bankMinutes;
        _decisionMinutes = decisionMinutes;
    }

    public String getCode() {
        return _code;
    }

    public int getBankMinutes() {
        return _bankMinutes;
    }

    public int getDecisionMinutes() {
        return _decisionMinutes;
    }

    public int getBankSeconds() {
        return _bankMinutes * 60;
    }

    public int getDecisionTimeoutSeconds() {
        return _decisionMinutes * 60;
    }

    public static HallGameTimer fromCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return DEFAULT;
        }
        for (HallGameTimer timer : values()) {
            if (timer._code.equalsIgnoreCase(code.trim())) {
                return timer;
            }
        }
        return DEFAULT;
    }
}
