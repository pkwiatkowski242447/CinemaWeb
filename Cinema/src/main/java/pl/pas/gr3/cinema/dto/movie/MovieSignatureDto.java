package pl.pas.gr3.cinema.dto.movie;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import pl.pas.gr3.cinema.dto.ObjectSignatureResponse;

import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

@Getter @Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class MovieSignatureDto implements ObjectSignatureResponse {

    private UUID id;

    /* ObjectSignatureResponse interface method */

    @Override
    public Map<String, Object> obtainSigningFields() {
        Map<String, Object> map = new TreeMap<>();
        map.put("id", id != null ? id.toString() : null);
        return map;
    }
}
