package com.bronko.backend.utils;

public class Utils {
    public static int parseFirstDigitsInString(String str) {
        int res = 0;

        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c < '0' || c > '9') return res;
            res = res * 10 + (c - '0');
        }

        return res;
    }

    public static int parseIntDefaultZero(String str) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
