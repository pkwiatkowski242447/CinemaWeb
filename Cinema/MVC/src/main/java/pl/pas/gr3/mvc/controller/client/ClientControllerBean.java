package pl.pas.gr3.mvc.controller.client;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.pas.gr3.dto.output.ClientDTO;
import pl.pas.gr3.dto.input.ClientInputDTO;
import pl.pas.gr3.dto.update.ClientPasswordDTO;
import pl.pas.gr3.mvc.dao.implementations.ClientDao;
import pl.pas.gr3.mvc.dao.interfaces.IClientDao;
import pl.pas.gr3.mvc.exceptions.beans.clients.*;
import pl.pas.gr3.mvc.exceptions.daos.client.*;

import java.io.Serializable;

@Getter @Setter
@NoArgsConstructor
@SessionScoped
@Named
public class ClientControllerBean implements Serializable {

    private ClientDTO createdClient;
    private ClientDTO selectedClient;

    private String message;

    private IClientDao clientDao;

    @PostConstruct
    public void beanInit() {
        clientDao = new ClientDao();
    }

    // Actions

    public void createClient(ClientInputDTO clientInputDTO) throws ClientCreateException {
        try {
            createdClient = clientDao.create(clientInputDTO);
        } catch (ClientDaoCreateException exception) {
            throw new ClientCreateException(exception.getMessage());
        }
    }

    public void readClientForChange(ClientDTO clientDTO) throws ClientReadException {
        try {
            selectedClient = clientDao.readClientForChange(clientDTO);
        } catch (ClientDaoReadException exception) {
            throw new ClientReadException(exception.getMessage());
        }
    }

    public void updateClient(ClientPasswordDTO clientPasswordDTO) throws ClientUpdateException {
        try {
            clientDao.updateClient(clientPasswordDTO);
        } catch (ClientDaoUpdateException exception) {
            throw new ClientUpdateException(exception.getMessage());
        }
    }

    public void activateClient(ClientDTO clientDTO) throws ClientActivateException {
        try {
            clientDao.activateClient(clientDTO);
        } catch (ClientDaoActivateException exception) {
            throw new ClientActivateException(exception.getMessage());
        }
    }

    public void deactivateClient(ClientDTO clientDTO) throws ClientDeactivateException {
        try {
            clientDao.deactivateClient(clientDTO);
        } catch (ClientDaoDeactivateException exception) {
            throw new ClientDeactivateException(exception.getMessage());
        }
    }
}
