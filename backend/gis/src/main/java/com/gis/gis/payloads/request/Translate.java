package com.gis.gis.payloads.request;

public record Translate(String sourceLanguage, String destinationLanguage, String source) {
}
