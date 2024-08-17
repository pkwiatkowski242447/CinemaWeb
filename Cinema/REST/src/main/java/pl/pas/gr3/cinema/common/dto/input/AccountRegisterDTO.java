package pl.pas.gr3.cinema.common.dto.input;

public record AccountRegisterDTO(
        String login,
        String password,
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        String language) {}
