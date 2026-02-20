package pl.pas.gr3.cinema.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

@Getter @Setter
public class ApplicationBaseException extends RuntimeException {

    private String key;
    private Map<String, Object> metadata = new HashMap<>();
    private HttpStatus status;

    public ApplicationBaseException(String key, HttpStatus status) {
        this.key = key;
        this.status = status;
    }

    public ApplicationBaseException(String key, HttpStatus status, Throwable cause) {
        super(cause);
        this.key = key;
        this.status = status;
    }

    public ApplicationBaseException(String key, HttpStatus status, Map<String, Object> metadata) {
        this.key = key;
        this.status = status;
        this.metadata.putAll(metadata);
    }

    /* OTHER */

    public ApplicationBaseException addMetadata(String key, Object value) {
        if (metadata.get(key) != null) metadata.replace(key, value);
        else metadata.put(key, value);
        return this;
    }

    public ApplicationBaseException removeMetadata(String key) {
        metadata.remove(key);
        return this;
    }
}
