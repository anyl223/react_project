package com.gis.gis.payloads.request;

public record Register(
    long id, String username, String email, String password,
    Integer roleId, String mobile) {}
