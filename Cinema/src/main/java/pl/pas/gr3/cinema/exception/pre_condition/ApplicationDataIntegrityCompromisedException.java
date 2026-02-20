package pl.pas.gr3.cinema.exception.pre_condition;

import pl.pas.gr3.cinema.exception.general.PreConditionFailedException;
import pl.pas.gr3.cinema.util.I18n;

public class ApplicationDataIntegrityCompromisedException extends PreConditionFailedException {

    public ApplicationDataIntegrityCompromisedException() {
        super(I18n.APP_DATA_INTEGRITY_COMPROMISED);
    }
}
