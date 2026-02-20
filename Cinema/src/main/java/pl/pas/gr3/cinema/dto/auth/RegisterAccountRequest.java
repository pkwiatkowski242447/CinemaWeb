package pl.pas.gr3.cinema.dto.auth;

public record RegisterAccountRequest(
    AccountResponse user,
    String accessToken
) {}
