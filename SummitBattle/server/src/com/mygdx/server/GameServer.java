package com.mygdx.server;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;
import com.mygdx.common.ConnectedClient;
import com.mygdx.common.Network;
import com.mygdx.common.Network.PlayerNumberReq;
import com.mygdx.common.Network.PlayerNumberSend;
import com.mygdx.common.Network.SendName;
import com.mygdx.common.PlayerInput;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;


public class GameServer {
    private Server server;
    private ConnectedClientsManager clientsManager = new ConnectedClientsManager();
    int ClientID;
    boolean A_PRESSED;
    boolean D_PRESSED;
    boolean W_PRESSED;
    boolean ENTER_PRESSED;
    ConnectedClient SENTCLIENT;
    ConnectedClient REQUESTCLIENT;





    public GameServer() throws IOException {
        JFrame frame = new JFrame("Chat Server");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosed (WindowEvent evt) {
                server.stop();
            }
        });
        frame.getContentPane().add(new JLabel("Close to stop the server."));
        frame.setSize(320, 200);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        server = new Server();
        Network.register(server);
        MatchmakingManager matchmakingManager = new MatchmakingManager();

        server.addListener(new Listener() {
            @Override
            public void connected(Connection connection) {

            }

            @Override
            public void disconnected(Connection connection) {
                int dcID = connection.getID();
                clientsManager.removeConnectedClientById(dcID);
            }

            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof SendName) {
                    String name = ((SendName) object).name;

                    if (name == null || name.trim().isEmpty()) {
                        return;
                    }

                    name = name.trim();
                    String clientName = name;

                    InetSocketAddress address = connection.getRemoteAddressTCP();
                    String ipAddress = address.getAddress().getHostAddress();
                    ClientID = connection.getID();

                    clientsManager.addConnectedClient(ipAddress, clientName, ClientID);

                    List<ConnectedClient> ConnectedClients = clientsManager.getConnectedClients();
                    for (ConnectedClient client : ConnectedClients) {
                    }
                    matchmakingManager.matchmaking(clientsManager.getConnectedClients(),clientsManager,server);





                }
                if (object instanceof PlayerNumberReq) {
                    PlayerNumberSend playerNumberSend = new PlayerNumberSend();
                    if ( connection.getID() % 2 == 1) {
                        playerNumberSend.Playernumber = "Player 1";
                    } else if (connection.getID() % 2 == 0) {
                        playerNumberSend.Playernumber = "Player 2";
                    }
                    server.sendToTCP(connection.getID(), playerNumberSend);
                }

                if (object instanceof PlayerInput) {

                    PlayerInput input = (PlayerInput) object;
                    int clientId = connection.getID();
                    Integer pairedClientId = matchmakingManager.getPairedClientId(clientId);
                    if (pairedClientId != null) {
                        server.sendToTCP(pairedClientId, input);

                    }






                }
            }
        });

        // Choose a higher port number, e.g., 5000
        int port = 5000;
        int UDPPort = 4999;
        server.bind(port,UDPPort);
        server.start();

        System.out.println("Server started and listening on port " + port);
    }

    public static void main(String[] args) {
        try {
            Log.set(Log.LEVEL_DEBUG);
            new GameServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}