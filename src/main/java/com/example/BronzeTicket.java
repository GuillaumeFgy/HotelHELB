package com.example;

import java.util.Random;

public class BronzeTicket extends Ticket {
    private final int winningDoor;

    public BronzeTicket(String code) {
        super(25, code);
        this.winningDoor = new Random().nextInt(2);
    }

    @Override
    public boolean playGame(Object input) {
        if (!(input instanceof Integer)) {
            throw new IllegalArgumentException("Expected an Integer for door choice.");
        }
        int chosenDoor = (Integer) input;
        return chosenDoor == winningDoor;
    }

    public int getWinningDoor() {
        return winningDoor;
    }
}
