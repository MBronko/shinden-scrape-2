package com.bronko.backend.utils;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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

    public static Timestamp parseTimestamp(String date, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        Date parsedDate;
        try {
            parsedDate = dateFormat.parse(date);
        } catch (ParseException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Exception while parsing timestamp: " + date);
        }
        return new Timestamp(parsedDate.getTime());
    }
}
