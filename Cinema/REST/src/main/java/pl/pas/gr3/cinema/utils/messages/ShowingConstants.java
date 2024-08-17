package pl.pas.gr3.cinema.utils.messages;

public class ShowingConstants {

    // Showing show time

    public static final String SHOWING_SHOT_TIME_NULL = "beans.validation.showing.show.time.null";

    // Showing base price

    public static final int SHOWING_BASE_PRICE_MIN_VALUE = 0;
    public static final int SHOWING_BASE_PRICE_MAX_VALUE = 100;

    public static final String SHOWING_BASE_PRICE_NULL = "beans.validation.showing.base.price.null";
    public static final String SHOWING_BASE_PRICE_NEGATIVE = "beans.validation.showing.base.price.negative";
    public static final String SHOWING_TITLE_TOO_HIGH = "beans.validation.showing.base.price.too.high";

    // Showing room number

    public static final int SHOWING_ROOM_NUMBER_MIN_VALUE = 1;
    public static final int SHOWING_ROOM_NUMBER_MAX_VALUE = 30;

    public static final String SHOWING_ROOM_NUMBER_NULL = "beans.validation.showing.room.number.null";
    public static final String SHOWING_ROOM_NUMBER_NON_POSITIVE = "beans.validation.showing.room.number.non.positive";
    public static final String SHOWING_ROOM_NUMBER_TOO_HIGH = "beans.validation.showing.room.number.too.high";

    // Showing available seats

    public static final int SHOWING_AVAILABLE_SEATS_MIN_VALUE = 0;
    public static final int SHOWING_AVAILABLE_SEATS_MAX_VALUE = 120;

    public static final String SHOWING_AVAILABLE_SEATS_NULL = "beans.validation.showing.available.seats.null";
    public static final String SHOWING_AVAILABLE_SEATS_NEGATIVE = "beans.validation.showing.available.seats.negative";
    public static final String SHOWING_AVAILABLE_SEATS_TOO_HIGH = "beans.validation.showing.available.seats.too.high";
}
