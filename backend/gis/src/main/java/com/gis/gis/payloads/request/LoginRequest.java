package com.gis.gis.payloads.request;

public record LoginRequest(String username, String password, String otp, String captcha,
        String fp) {
}
