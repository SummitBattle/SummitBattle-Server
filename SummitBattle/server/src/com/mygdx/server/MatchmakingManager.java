package com.mygdx.server;

import com.esotericsoftware.kryonet.Server;
import com.mygdx.common.ConnectedClient;
import com.mygdx.common.Network.NotifyMessage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MatchmakingManager {
    private Map<Integer, Integer> clientPairs = new HashMap<>(); // Store pairs of connected clients

    public void matchmaking(List<ConnectedClient> connectedClients, ConnectedClientsManager connectedClientsManager, Server server) {
        if (connectedClients.size() >= 2) {
            for (int i = 0; i < connectedClients.size(); i += 2) {
                ConnectedClient player1 = connectedClients.get(i);
                ConnectedClient player2 = connectedClients.get(i + 1);

                clientPairs.put(player1.getID(), player2.getID());
                clientPairs.put(player2.getID(), player1.getID());

                notifyPlayers(player1, player2, server);

                connectedClientsManager.removeConnectedClientById(player1.getID());
                connectedClientsManager.removeConnectedClientById(player2.getID());
            }
        }
    }

    private void notifyPlayers(ConnectedClient player1, ConnectedClient player2, Server server) {
        NotifyMessage notifyMessage = new NotifyMessage();
        notifyMessage.connectedClient1 = player1;
        notifyMessage.connectedClient2 = player2;
        notifyMessage.isReady = true;
        server.sendToTCP(player1.getID(), notifyMessage);
        server.sendToTCP(player2.getID(), notifyMessage);
    }

    public Integer getPairedClientId(int clientId) {
        return clientPairs.get(clientId);
    }
}