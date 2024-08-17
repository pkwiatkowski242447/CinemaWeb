package pl.pas.gr3.cinema.utils.constants;

public class DatabaseConstants {

    // AbstractEntity

    public static final String PK_COLUMN = "id";
    public static final String VERSION_COLUMN = "version";

    // public.accounts

    public static final String ACCOUNTS_TABLE = "accounts";

    public static final String ACCOUNTS_LOGIN_COLUMN = "login";
    public static final String ACCOUNTS_PASSWORD_COLUMN = "password";
    public static final String ACCOUNTS_ACTIVE_COLUMN = "active";
    public static final String ACCOUNTS_BLOCKED_COLUMN = "blocked";
    public static final String ACCOUNTS_LANGUAGE_COLUMN = "language";
    public static final String ACCOUNTS_AVATAR_ID = "avatar_id";

    public static final String ACCOUNTS_AVATAR_ID_FK = "accounts_avatar_id_fk";
    public static final String ACCOUNTS_AVATAR_ID_IDX = "accounts_avatar_id_idx";

    // public.personal_data

    public static final String PERSONAL_DATA_TABLE = "personal_data";

    public static final String PERSONAL_DATA_ACCOUNT_ID = "account_id";
    public static final String PERSONAL_DATA_ACCOUNT_ID_FK = "personal_data_account_id_fk";
    public static final String PERSONAL_DATA_ACCOUNT_ID_IDX = "personal_data_account_id_idx";

    public static final String PERSONAL_DATA_FIRST_NAME_COLUMN = "first_name";
    public static final String PERSONAL_DATA_LAST_NAME_COLUMN = "last_name";
    public static final String PERSONAL_DATA_EMAIL_COLUMN = "email";
    public static final String PERSONAL_DATA_PHONE_NUMBER_COLUMN = "phone_number";

    // public.address_data

    public static final String ADDRESS_DATA_TABLE = "address_data";

    public static final String ADDRESS_DATA_ACCOUNT_ID = "account_id";
    public static final String ADDRESS_DATA_ACCOUNT_ID_FK = "address_data_account_id_fk";
    public static final String ADDRESS_DATA_ACCOUNT_ID_IDX = "address_data_account_id_idx";

    public static final String ADDRESS_DATA_COUNTRY_NAME_COLUMN = "country";
    public static final String ADDRESS_DATA_PROVINCE_NAME_COLUMN = "province";
    public static final String ADDRESS_DATA_CITY_NAME_COLUMN = "city_name";
    public static final String ADDRESS_DATA_STREET_NAME_COLUMN = "street_name";
    public static final String ADDRESS_DATA_STREET_NUMBER_COLUMN = "street_number";
    public static final String ADDRESS_DATA_ZIP_CODE_COLUMN = "zip_code";

    // public.access_levels

    public static final String ACCESS_LEVELS_TABLE = "access_levels";
    public static final String ACCESS_LEVELS_DISCRIMINATOR = "type";

    public static final String ACCESS_LEVELS_ACCOUNT_ID_COLUMN = "account_id";

    public static final String ACCESS_LEVELS_ACCOUNT_ID_FK = "access_level_account_id_fk";
    public static final String ACCESS_LEVELS_ACCOUNT_ID_IDX = "access_level_account_id_idx";

    // public.admins

    public static final String ADMINS_TABLE = "admins";
    public static final String ADMINS_DISCRIMINATOR = "ADMIN";

    // public.staff

    public static final String STAFF_TABLE = "staff";
    public static final String STAFFS_DISCRIMINATOR = "STAFF";

    // public.clients

    public static final String CLIENTS_TABLE = "clients";
    public static final String CLIENTS_DISCRIMINATOR = "CLIENT";

    // public.movies

    public static final String MOVIES_TABLE = "movies";

    public static final String MOVIES_TITLE_COLUMN = "title";
    public static final String MOVIES_DESCRIPTION_COLUMN = "description";

    // public.seances

    public static final String SHOWINGS_TABLE = "showings";

    public static final String SHOWINGS_SHOW_TIME_COLUMN = "show_time";
    public static final String SHOWINGS_BASE_PRICE_COLUMN = "base_price";
    public static final String SHOWINGS_ROOM_NUMBER_COLUMN = "room_number";
    public static final String SHOWINGS_AVAILABLE_SEATS_COLUMN = "available_seats";
    public static final String SHOWINGS_MOVIE_ID_COLUMN = "movie_id";

    public static final String SHOWINGS_MOVIE_ID_FK = "showings_movie_id_fk";
    public static final String SHOWINGS_MOVIE_ID_IDX = "showings_movie_id_idx";

    // public.tickets

    public static final String TICKETS_TABLE = "tickets";

    public static final String TICKETS_PRICE_COLUMN = "price";
    public static final String TICKETS_CLIENT_ID = "client_id";
    public static final String TICKETS_SHOWING_ID = "showing_id";

    public static final String TICKETS_CLIENT_ID_FK = "tickets_client_id_fk";
    public static final String TICKETS_CLIENT_ID_IDX = "tickets_client_id_idx";

    public static final String TICKETS_SHOWING_ID_FK = "tickets_showing_id_fk";
    public static final String TICKETS_SHOWING_ID_IDX = "tickets_showing_id_idx";

    // User file

    public static final String USER_FILE_TABLE = "user_files";

    public static final String USER_FILE_PATH_COLUMN = "path";
    public static final String USER_ORIGINAL_FILE_NAME_COLUMN = "file_name";
    public static final String USER_FILE_FORMAT_COLUMN = "format";
}
