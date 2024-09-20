package com.gis.gis.payloads.request;

import jakarta.validation.constraints.NotBlank;

public record EncryptedRequest(@NotBlank(message = "Invalid request data") String data) {
}
