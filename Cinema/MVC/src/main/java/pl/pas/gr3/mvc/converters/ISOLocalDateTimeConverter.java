package pl.pas.gr3.mvc.converters;

import java.time.LocalDateTime;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.ConverterException;
import jakarta.faces.convert.FacesConverter;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@FacesConverter("isoLocalDateTimeConverter")
public class ISOLocalDateTimeConverter implements Converter<LocalDateTime> {

    @Override
    public LocalDateTime getAsObject(FacesContext facesContext, UIComponent uiComponent, String s) {
        try {
            return LocalDateTime.parse(s, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (DateTimeParseException exception) {
            throw new ConverterException("Wpisany łańuch znaków %s nie jest prawidłowym obiektem LocalDateTime".formatted(s));
        }
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, LocalDateTime localDateTime) {
        try {
            return localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (DateTimeParseException exception) {
            throw new ConverterException(exception.getMessage());
        }
    }
}
