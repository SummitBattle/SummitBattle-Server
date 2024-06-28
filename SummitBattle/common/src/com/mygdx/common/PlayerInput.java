package com.mygdx.common;

public class PlayerInput {
    public boolean A_PRESSED;
    public boolean D_PRESSED;
    public boolean W_PRESSED;
    public boolean ENTER_PRESSED;
    ConnectedClient Enemyclient;

    // Default constructor is needed for KryoNet serialization
    public PlayerInput() {}

    public PlayerInput(boolean A_PRESSED, boolean D_PRESSED, boolean W_PRESSED, boolean ENTER_PRESSED) {
        this.A_PRESSED = A_PRESSED;
        this.D_PRESSED = D_PRESSED;
        this.W_PRESSED = W_PRESSED;
        this.ENTER_PRESSED = ENTER_PRESSED;

    }
}
