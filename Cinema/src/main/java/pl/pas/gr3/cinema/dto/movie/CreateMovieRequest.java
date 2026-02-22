package pl.pas.gr3.cinema.dto.movie;

public record CreateMovieRequest(
    String title,
    double basePrice,
    int scrRoomNumber,
    int availableSeats
) {}
