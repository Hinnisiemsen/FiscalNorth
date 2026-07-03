package de.fiscalnorth.auth.dto;

public record CsrfTokenResponse(String token, String headerName) {}
