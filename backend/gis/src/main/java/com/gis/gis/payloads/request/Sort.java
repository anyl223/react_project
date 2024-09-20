package com.gis.gis.payloads.request;

// directions: 0 -> ASC, 1 -> DESC
public record Sort(String column, int direction) {
}
