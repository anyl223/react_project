package com.gis.gis.payloads.response;

import com.gis.gis.models.User;
import com.gis.gis.models.Users;

public record LoginResponse(String token, Users user) {
}
