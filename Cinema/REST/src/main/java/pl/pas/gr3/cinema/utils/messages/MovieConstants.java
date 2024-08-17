package pl.pas.gr3.cinema.utils.messages;

public class MovieConstants {

    // Movie title

    public static final int MOVIE_TITLE_MIN_LENGTH = 1;
    public static final int MOVIE_TITLE_MAX_LENGTH = 128;

    public static final String MOVIE_TITLE_BLANK = "beans.validation.movie.title.blank";
    public static final String MOVIE_TITLE_TOO_SHORT = "beans.validation.movie.title.too.short";
    public static final String MOVIE_TITLE_TOO_LONG = "beans.validation.movie.title.too.long";

    // Movie description

    public static final int MOVIE_DESCRIPTION_MIN_LENGTH = 1;
    public static final int MOVIE_DESCRIPTION_MAX_LENGTH = 512;

    public static final String MOVIE_DESCRIPTION_BLANK = "beans.validation.movie.description.blank";
    public static final String MOVIE_DESCRIPTION_TOO_SHORT = "beans.validation.movie.description.too.short";
    public static final String MOVIE_DESCRIPTION_TOO_LONG = "beans.validation.movie.description.too.long";
}
