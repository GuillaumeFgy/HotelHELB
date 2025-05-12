package com.example;

import java.util.Random;

public class BronzeTicket extends Ticket {
    private final int winningDoor;

    public BronzeTicket(String code) {
        super(25, code);
        this.winningDoor = new Random().nextInt(2);
    }

    /* playGame
    Inputs: input – final door choice (should be an Integer).
    Outputs: true – if the chosen door is the winning one; false – otherwise.
    Description: Evaluates if the player’s selected door matches the winning door. */
    @Override
    public boolean playGame(Object input) {
        if (!(input instanceof Integer)) { throw new IllegalArgumentException("Expected an Integer for door choice."); } // Validate input type
        int chosenDoor = (Integer) input; // Cast to integer
        return chosenDoor == winningDoor; // Return true if match
    }

    public int getWinningDoor() {
        return winningDoor;
    }
}
