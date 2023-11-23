package pl.pas.gr3.cinema.dto.mappers.users;

import pl.pas.gr3.cinema.dto.json.users.ClientJson;
import pl.pas.gr3.cinema.dto.json.users.ClientJsonPassword;
import pl.pas.gr3.cinema.model.users.Client;

public class ClientMapper {

    public static ClientJson toClientJsonFromClient(Client client) {
        ClientJson clientJson = new ClientJson();
        clientJson.setClientID(client.getClientID());
        clientJson.setClientLogin(client.getClientLogin());
        clientJson.setClientStatusActive(client.isClientStatusActive());
        return clientJson;
    }

    public static ClientJsonPassword toClientJsonWithPasswordFromClient(Client client) {
        ClientJsonPassword clientJsonPassword = new ClientJsonPassword();
        clientJsonPassword.setClientID(client.getClientID());
        clientJsonPassword.setClientLogin(client.getClientLogin());
        clientJsonPassword.setClientPassword(client.getClientPassword());
        clientJsonPassword.setClientStatusActive(client.isClientStatusActive());
        return clientJsonPassword;
    }

    public static Client toClientFromClientJsonWithPassword(ClientJsonPassword clientJsonPassword) {
        return new Client(clientJsonPassword.getClientID(),
                clientJsonPassword.getClientLogin(),
                clientJsonPassword.getClientPassword(),
                clientJsonPassword.isClientStatusActive());
    }
}
