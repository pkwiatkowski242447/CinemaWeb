package pl.pas.gr3.cinema.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.pas.gr3.cinema.exception.ApplicationBaseException;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class ExceptionResponse {

    private String key;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String, Object> metadata = new HashMap<>();

    public ExceptionResponse(ApplicationBaseException exception) {
        this.key = exception.getKey();
        this.metadata = exception.getMetadata().isEmpty() ? new HashMap<>() : Map.copyOf(exception.getMetadata());
    }

    public ExceptionResponse(String key) {
        this.key = key;
    }

    public ExceptionResponse(String key, Map<String, Object> metadata) {
        this.key = key;
        this.metadata.putAll(metadata);
    }
}
