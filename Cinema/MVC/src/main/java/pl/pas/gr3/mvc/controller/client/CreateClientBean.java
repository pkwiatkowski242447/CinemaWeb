package pl.pas.gr3.mvc.controller.client;

import jakarta.enterprise.context.ConversationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.pas.gr3.dto.users.ClientInputDTO;
import pl.pas.gr3.mvc.exceptions.beans.clients.ClientCreateException;

import java.io.Serializable;

@Getter @Setter
@NoArgsConstructor
@ConversationScoped
@Named
public class CreateClientBean implements Serializable {

    private String clientLogin;
    private String clientPasswordFirst;
    private String clientPasswordSecond;

    private String message;

    @Inject
    private ClientControllerBean clientControllerBean;

    // Methods

    public String createClient() {
        if (clientPasswordFirst.equals(clientPasswordSecond)) {
            try {
                clientControllerBean.createClient(new ClientInputDTO(clientLogin, clientPasswordFirst));
                return "clientCreatedSuccessfully";
            } catch (ClientCreateException exception) {
                message = exception.getMessage();
                return null;
            }
        } else {
            message = "Podane hasła różnią się.";
            return null;
        }
    }
}
