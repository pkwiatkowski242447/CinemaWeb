package pl.pas.gr3.cinema.common.dto.input;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.multipart.MultipartFile;

public record AccountRegisterDTO(
        String login,
        String password,
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        String language,
        MultipartFile avatar) {}
