package pl.pas.gr3.cinema.dto.auth;

import java.util.UUID;

public record AccountResponse(
    UUID id,
    String login,
    boolean active
) {}
