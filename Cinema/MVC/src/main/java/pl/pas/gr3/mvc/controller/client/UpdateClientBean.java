package pl.pas.gr3.mvc.controller.client;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ConversationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.pas.gr3.dto.users.ClientDTO;
import pl.pas.gr3.dto.users.ClientPasswordDTO;
import pl.pas.gr3.mvc.exceptions.beans.clients.ClientUpdateException;

import java.io.Serializable;

@Getter @Setter
@NoArgsConstructor
@RequestScoped
@Named
public class UpdateClientBean implements Serializable {

    private ClientPasswordDTO clientPasswordDTO;

    private String clientPasswordFirst;
    private String clientPasswordSecond;

    private String message;

    @Inject
    private ClientControllerBean clientControllerBean;

    @PostConstruct
    public void beanInit() {
        clientPasswordDTO = new ClientPasswordDTO();
        ClientDTO clientDTO = clientControllerBean.getSelectedClient();
        clientPasswordDTO.setClientID(clientDTO.getClientID());
        clientPasswordDTO.setClientLogin(clientDTO.getClientLogin());
        clientPasswordDTO.setClientStatusActive(clientDTO.isClientStatusActive());
    }

    public String updateClient() {
        if (clientPasswordFirst.equals(clientPasswordSecond)) {
            clientPasswordDTO.setClientPassword(clientPasswordFirst);
            try {
                clientControllerBean.updateClient(clientPasswordDTO);
                return "listOfAllClients";
            } catch (ClientUpdateException exception) {
                message = exception.getMessage();
                return null;
            }
        } else {
            message = "Podane hasła różnią się";
            return null;
        }
    }
}
