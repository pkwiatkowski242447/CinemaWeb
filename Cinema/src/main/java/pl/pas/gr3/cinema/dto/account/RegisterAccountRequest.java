package pl.pas.gr3.cinema.dto.account;

public record RegisterAccountRequest(
    AccountResponse user,
    String accessToken
) {}
