package pl.pas.gr3.mvc.dao.interfaces;

import pl.pas.gr3.dto.users.ClientDTO;
import pl.pas.gr3.dto.users.ClientInputDTO;
import pl.pas.gr3.dto.users.ClientPasswordDTO;
import pl.pas.gr3.mvc.exceptions.daos.client.*;

import java.util.List;

public interface IClientDao {

    // Create methods

    ClientDTO create(ClientInputDTO clientInputDTO) throws ClientDaoCreateException;

    // Read methods

    ClientDTO readClientForChange(ClientDTO clientDTO) throws ClientDaoReadException;
    List<ClientDTO> findAll() throws ClientDaoReadException;

    // Update methods

    void updateClient(ClientPasswordDTO clientPasswordDTO) throws ClientDaoUpdateException;
    void activateClient(ClientDTO clientDTO) throws ClientDaoActivateException;
    void deactivateClient(ClientDTO clientDTO) throws ClientDaoDeactivateException;
}
