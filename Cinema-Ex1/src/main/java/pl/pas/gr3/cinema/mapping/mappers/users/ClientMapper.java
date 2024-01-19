package pl.pas.gr3.cinema.mapping.mappers.users;

import pl.pas.gr3.cinema.mapping.docs.users.ClientDoc;
import pl.pas.gr3.cinema.model.users.Client;

public class ClientMapper {

    public static ClientDoc toClientDoc(Client client) {
        ClientDoc clientDoc = new ClientDoc();
        clientDoc.setClientID(client.getClientID());
        clientDoc.setClientLogin(client.getClientLogin());
        clientDoc.setClientPassword(client.getClientPassword());
        clientDoc.setClientStatusActive(client.isClientStatusActive());
        return clientDoc;
    }

    public static Client toClient(ClientDoc clientDoc) {
        return new Client(clientDoc.getClientID(),
                clientDoc.getClientLogin(),
                clientDoc.getClientPassword(),
                clientDoc.isClientStatusActive());
    }
}
