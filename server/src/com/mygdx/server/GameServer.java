package com.mygdx.server;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;

import com.mygdx.common.Network;
import com.mygdx.common.Network.PlayerNumberReq;
import com.mygdx.common.Network.PlayerNumberSend;
import com.mygdx.common.Network.SendName;
import com.mygdx.common.PlayerInput;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.*;



public class GameServer {
    private Server server;
    private ConnectedClientsManager clientsManager;
    int ClientID;
    InetAddress localhost;

    String publicIP;





    public GameServer() throws IOException {
        try
        {
            localhost = InetAddress.getLocalHost();
            System.out.println("Local IP Address = " +localhost.getHostAddress());
        }
        catch(Exception e)
        {
            System.out.println("Exception: " +e);
        }
        try
        {
            URL ipfinder = new URL("https://checkip.amazonaws.com");
            BufferedReader br = new BufferedReader(new InputStreamReader(ipfinder.openStream()));
            publicIP = br.readLine();
            System.out.println("Public IP Address = " +publicIP);
        }
        catch(Exception e)
        {
            System.out.println("Exception: " +e);
        }

        JFrame frame = new JFrame("Chat Server");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosed (WindowEvent evt) {
                server.stop();
            }
        });
        frame.getContentPane().add(new JLabel("        Your private IP is: " + localhost.getHostAddress() + "               Public IP is: " + publicIP));
        frame.setSize(500, 200);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        server = new Server();
        Network.register(server);
        clientsManager = new ConnectedClientsManager();
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

                    matchmakingManager.matchmaking(clientsManager.getConnectedClients(),clientsManager,server);

                }
                if (object instanceof PlayerNumberReq) {
                    PlayerNumberSend playerNumberSend = new PlayerNumberSend();
                    if ( connection.getID() % 2 == 1) {
                        playerNumberSend.Playernumber = "Player 1";
                    } else if (connection.getID() % 2 == 0) {
                        playerNumberSend.Playernumber = "Player 2";
                    }
                    server.sendToUDP(connection.getID(), playerNumberSend);
                }

                if (object instanceof PlayerInput) {

                    PlayerInput input = (PlayerInput) object;
                    int clientId = connection.getID();
                    Integer pairedClientId = matchmakingManager.getPairedClientId(clientId);
                    if (pairedClientId != null) {
                        server.sendToUDP(pairedClientId, input);

                    }
                }}
        });


        int UDPPort = 4999;
        server.bind(UDPPort);
        server.start();

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