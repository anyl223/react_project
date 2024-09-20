package com.gis.gis.security.jwt;

public record Jwt(String subject, String fp, long timestamp) {
}
