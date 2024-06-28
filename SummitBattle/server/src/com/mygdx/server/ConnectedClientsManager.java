package com.mygdx.server;

import com.mygdx.common.ConnectedClient;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

class ConnectedClientsManager {
    private final List<ConnectedClient> ConnectedClients;

    ConnectedClientsManager() {
        ConnectedClients = new CopyOnWriteArrayList<>();
    }

    public void addConnectedClient(String ipAddress, String name, int id) {
        ConnectedClients.add(new ConnectedClient(ipAddress, name, id));
    }

    public void removeConnectedClientById(int id) {
        ConnectedClients.removeIf(client -> client.getID() == id);
    }

    public List<ConnectedClient> getConnectedClients() {
        return ConnectedClients;
    }
}
