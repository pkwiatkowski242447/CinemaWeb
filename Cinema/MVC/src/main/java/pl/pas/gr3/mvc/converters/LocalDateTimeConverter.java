package pl.pas.gr3.mvc.converters;

import java.time.LocalDateTime;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;

import java.time.format.DateTimeFormatter;

@FacesConverter("pl.pas.gr3.mvc.converters.LocalDateTimeConverter")
public class LocalDateTimeConverter implements Converter<LocalDateTime> {

    @Override
    public LocalDateTime getAsObject(FacesContext facesContext, UIComponent uiComponent, String s) {
        return LocalDateTime.parse(s, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, LocalDateTime localDateTime) {
        return localDateTime.toString();
    }
}
