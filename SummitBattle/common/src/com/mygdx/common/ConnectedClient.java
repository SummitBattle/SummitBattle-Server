package com.mygdx.common;

public class ConnectedClient {
    private String ipAddress;
    private String name;
    private int id;

    // No-argument constructor
    public ConnectedClient() {}

    // Parameterized constructor
    public ConnectedClient(String ipAddress, String name, int id) {
        this.ipAddress = ipAddress;
        this.name = name;
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public int getID() {
        return id;
    }
}
