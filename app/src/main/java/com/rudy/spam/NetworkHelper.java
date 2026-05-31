package com.rudy.spam;

import okhttp3.*;
import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

public class NetworkHelper {
    public static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS)
            .writeTimeout(5, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build();

    public static String codex(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    public static String fetchValue(String response, String start, String end) {
        if (response == null) return null;
        int i = response.indexOf(start);
        if (i == -1) return null;
        String rem = response.substring(i + start.length());
        int j = rem.indexOf(end);
        return j == -1 ? null : rem.substring(0, j);
    }

    public static String normalizePhone(String p) {
        String clean = p.replaceAll("\\D", "");
        if (clean.startsWith("0")) clean = clean.substring(1);
        if (clean.startsWith("62")) clean = clean.substring(2);
        return clean;
    }
}