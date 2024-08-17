package pl.pas.gr3.cinema.utils.messages;

public class AccountConstants {

    // Account id

    public static final String ACCOUNT_ID_NULL = "beans.validation.account.id.null";

    // Account login

    public static final int ACCOUNT_LOGIN_MIN_LENGTH = 8;
    public static final int ACCOUNT_LOGIN_MAX_LENGTH = 64;

    public static final String ACCOUNT_LOGIN_BLANK = "beans.validation.account.login.null";
    public static final String ACCOUNT_LOGIN_TOO_SHORT = "beans.validation.account.login.too.short";
    public static final String ACCOUNT_LOGIN_TOO_LONG = "beans.validation.account.login.too.long";

    // Account password

    // Password length must be equal to 60, since password stores password hash.

    public static final int ACCOUNT_PASSWORD_LENGTH = 60;

    public static final String ACCOUNT_PASSWORD_BLANK = "beans.validation.account.password.blank";
    public static final String ACCOUNT_PASSWORD_TOO_SHORT = "beans.validation.account.password.too.short";
    public static final String ACCOUNT_PASSWORD_TOO_LONG = "beans.validation.account.password.too.long";

    // Account first name

    public static final int PERSONAL_DATA_FIRST_NAME_MIN_LENGTH = 4;
    public static final int PERSONAL_DATA_FIRST_NAME_MAX_LENGTH = 64;

    public static final String PERSONAL_DATA_FIRST_NAME_BLANK = "beans.validation.personal.data.first.name.blank";
    public static final String PERSONAL_DATA_FIRST_NAME_TOO_SHORT = "beans.validation.personal.data.first.name.too.short";
    public static final String PERSONAL_DATA_FIRST_NAME_TOO_LONG = "beans.validation.personal.data.first.name.too.long";

    // Account last name

    public static final int PERSONAL_DATA_LAST_NAME_MIN_LENGTH = 4;
    public static final int PERSONAL_DATA_LAST_NAME_MAX_LENGTH = 64;

    public static final String PERSONAL_DATA_LAST_NAME_BLANK = "beans.validation.personal.data.last.name.blank";
    public static final String PERSONAL_DATA_LAST_NAME_TOO_SHORT = "beans.validation.personal.data.last.name.too.short";
    public static final String PERSONAL_DATA_LAST_NAME_TOO_LONG = "beans.validation.personal.data.last.name.too.long";

    // Account email

    public static final int PERSONAL_DATA_EMAIL_MIN_LENGTH = 4;
    public static final int PERSONAL_DATA_EMAIL_MAX_LENGTH = 64;

    public static final String PERSONAL_DATA_EMAIL_BLANK = "beans.validation.personal.data.email.blank";
    public static final String PERSONAL_DATA_EMAIL_TOO_SHORT = "beans.validation.personal.data.email.too.short";
    public static final String PERSONAL_DATA_EMAIL_TOO_LONG = "beans.validation.personal.data.email.too.long";

    // Account phone number

    public static final int PERSONAL_DATA_PHONE_NUMBER_MIN_LENGTH = 4;
    public static final int PERSONAL_DATA_PHONE_NUMBER_MAX_LENGTH = 64;

    public static final String PERSONAL_DATA_PHONE_NUMBER_BLANK = "beans.validation.personal.data.phone.number.blank";
    public static final String PERSONAL_DATA_PHONE_NUMBER_TOO_SHORT = "beans.validation.personal.data.phone.number.too.short";
    public static final String PERSONAL_DATA_PHONE_NUMBER_TOO_LONG = "beans.validation.personal.data.phone.number.too.long";

    // Account country name

    public static final int ADDRESS_DATA_COUNTRY_NAME_MIN_LENGTH = 4;
    public static final int ADDRESS_DATA_COUNTRY_NAME_MAX_LENGTH = 64;

    public static final String ADDRESS_DATA_COUNTRY_NAME_BLANK = "beans.validation.address.data.country.name.blank";
    public static final String ADDRESS_DATA_COUNTRY_NAME_TOO_SHORT = "beans.validation.address.data.country.name.too.short";
    public static final String ADDRESS_DATA_COUNTRY_NAME_TOO_LONG = "beans.validation.address.data.country.name.too.long";

    // Account province name

    public static final int ADDRESS_DATA_PROVINCE_NAME_MIN_LENGTH = 4;
    public static final int ADDRESS_DATA_PROVINCE_NAME_MAX_LENGTH = 64;

    public static final String ADDRESS_DATA_PROVINCE_NAME_BLANK = "beans.validation.address.data.province.name.blank";
    public static final String ADDRESS_DATA_PROVINCE_NAME_TOO_SHORT = "beans.validation.address.data.province.name.too.short";
    public static final String ADDRESS_DATA_PROVINCE_NAME_TOO_LONG = "beans.validation.address.data.province.name.too.long";

    // Account city name

    public static final int ADDRESS_DATA_CITY_NAME_MIN_LENGTH = 4;
    public static final int ADDRESS_DATA_CITY_NAME_MAX_LENGTH = 64;

    public static final String ADDRESS_DATA_CITY_NAME_BLANK = "beans.validation.address.data.city.name.blank";
    public static final String ADDRESS_DATA_CITY_NAME_TOO_SHORT = "beans.validation.address.data.city.name.too.short";
    public static final String ADDRESS_DATA_CITY_NAME_TOO_LONG = "beans.validation.address.data.city.name.too.long";

    // Account street name

    public static final int ADDRESS_DATA_STREET_NAME_MIN_LENGTH = 4;
    public static final int ADDRESS_DATA_STREET_NAME_MAX_LENGTH = 64;

    public static final String ADDRESS_DATA_STREET_NAME_BLANK = "beans.validation.address.data.street.name.blank";
    public static final String ADDRESS_DATA_STREET_NAME_TOO_SHORT = "beans.validation.address.data.street.name.too.short";
    public static final String ADDRESS_DATA_STREET_NAME_TOO_LONG = "beans.validation.address.data.street.name.too.long";

    // Account street number

    public static final String ADDRESS_DATA_STREET_NUMBER_NOT_NULL = "beans.validation.address.data.street.number.not.null";
    public static final String ADDRESS_DATA_STREET_NUMBER_NEGATIVE = "beans.validation.address.data.street.number.negative";

    // Account zip code

    public static final int ADDRESS_DATA_ZIP_CODE_LENGTH = 6;

    public static final String ADDRESS_DATA_ZIP_CODE_BLANK = "beans.validation.address.data.zip.code.blank";
    public static final String ADDRESS_DATA_ZIP_CODE_TOO_SHORT = "beans.validation.address.data.zip.code.too.short";
    public static final String ADDRESS_DATA_ZIP_CODE_TOO_LONG = "beans.validation.address.data.zip.code.too.long";

    // Account active

    public static final String ACCOUNT_ACTIVE_NULL = "beans.validation.account.active.null";

    // Account blocked

    public static final String ACCOUNT_BLOCKED_NULL = "beans.validation.account.blocked.null";

    // Account languages

    public static final int ACCOUNT_LANGUAGE_LENGTH = 2;

    public static final String ACCOUNT_LANGUAGE_BLANK = "beans.validation.account.language.blank";
    public static final String ACCOUNT_LANGUAGE_TOO_SHORT = "beans.validation.account.language.too.short";
    public static final String ACCOUNT_LANGUAGE_TOO_LONG = "beans.validation.account.language.too.long";

    // JWT fields

    public static final String ACCOUNT_LOGIN = "login";
    public static final String ACCOUNT_ACCESS_LEVELS = "access_levels";
}
