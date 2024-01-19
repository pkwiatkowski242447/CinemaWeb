package pl.pas.gr3.mvc.controller.client;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.pas.gr3.dto.users.ClientDTO;
import pl.pas.gr3.mvc.dao.implementations.ClientDao;
import pl.pas.gr3.mvc.dao.interfaces.IClientDao;
import pl.pas.gr3.mvc.exceptions.beans.clients.ClientActivateException;
import pl.pas.gr3.mvc.exceptions.beans.clients.ClientDeactivateException;
import pl.pas.gr3.mvc.exceptions.beans.clients.ClientReadException;
import pl.pas.gr3.mvc.exceptions.daos.client.ClientDaoReadException;

import java.io.Serializable;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
@ViewScoped
@Named
public class ReadAllClientsBean implements Serializable {

    private List<ClientDTO> listOfClients;
    private String message;

    private IClientDao clientDao;

    @Inject
    private ClientControllerBean clientControllerBean;

    @PostConstruct
    public void beanInit() {
        clientDao = new ClientDao();
        this.findAllClients();
    }

    public void findAllClients() {
        try {
            listOfClients = clientDao.findAll();
        } catch (ClientDaoReadException exception) {
            message = exception.getMessage();
        }
    }

    public String activateClient(ClientDTO clientDTO) {
        try {
            clientControllerBean.activateClient(clientDTO);
            return "listOfAllClients";
        } catch (ClientActivateException exception) {
            message = exception.getMessage();
            return null;
        }
    }

    public String deactivateClient(ClientDTO clientDTO) {
        try {
            clientControllerBean.deactivateClient(clientDTO);
            return "listOfAllClients";
        } catch (ClientDeactivateException exception) {
            message = exception.getMessage();
            return null;
        }
    }

    public String updateClient(ClientDTO clientDTO) {
        try {
            clientControllerBean.readClientForChange(clientDTO);
            return "updateClientAction";
        } catch (ClientReadException exception) {
            message = exception.getMessage();
            return null;
        }
    }
}
