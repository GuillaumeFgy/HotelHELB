package com.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GoldTicket extends Ticket {
    private final int totalDoors;
    private final int winningDoor;
    private final List<Integer> remainingDoors = new ArrayList<>();
    private int playerChoice = -1;

    /* GoldTicket constructor
    Inputs: code – discount code; floor – floor number to influence number of doors.
    Outputs: none (initializes the GoldTicket fields).
    Description: Initializes a GoldTicket with a number of doors based on the floor and randomly picks a winning one. */
    public GoldTicket(String code, int floor) {
        super(100, code); // Call parent constructor with 100% discount and provided code
        this.totalDoors = floor + 2; // Number of doors increases with floor level
        this.winningDoor = new Random().nextInt(totalDoors); // Randomly select the winning door
        for (int i = 0; i < totalDoors; i++) { remainingDoors.add(i); } // Initialize all door indices
    }


    public int getTotalDoors() {
        return totalDoors;
    }

    /* getRemainingDoors
    Inputs: none.
    Outputs: a copy of the list of remaining door indices.
    Description: Returns a defensive copy of the doors still in play to prevent external modification. */
    public List<Integer> getRemainingDoors() {
        return new ArrayList<>(remainingDoors); // Return a copy to protect internal state
    }


    /* setPlayerChoice
    Inputs: doorIndex – the index of the door selected by the player.
    Outputs: none.
    Description: Sets the player’s chosen door if it's still available; otherwise throws an error. */
    public void setPlayerChoice(int doorIndex) {
        if (!remainingDoors.contains(doorIndex)) {
            throw new IllegalArgumentException("Invalid choice."); // Door must be one of the remaining ones
        }
        this.playerChoice = doorIndex; // Store the valid choice
    }


    /* eliminateOneWrongDoor
    Inputs: none.
    Outputs: the index of the eliminated door.
    Description: Randomly removes one door that is neither the player's choice nor the winning door. */
    public int eliminateOneWrongDoor() {
        List<Integer> candidates = new ArrayList<>(); // Possible doors to eliminate

        for (int door : remainingDoors) {
            if (door != winningDoor && door != playerChoice) {
                candidates.add(door); // Collect non-winning, non-selected doors
            }
        }

        if (candidates.isEmpty()) {
            throw new IllegalStateException("No door can be eliminated."); // No eligible doors
        }

        int eliminated = candidates.get(new Random().nextInt(candidates.size())); // Pick one to eliminate
        remainingDoors.remove((Integer) eliminated); // Remove from the list
        return eliminated; // Return eliminated door index
    }

    /* playGame
    Inputs: finalChoice – the final door index selected by the user.
    Outputs: true – if the chosen door is the winning one; false – otherwise.
    Description: Final step of the GoldTicket game; checks if the player's last choice matches the winning door. */
    @Override
    public boolean playGame(Object finalChoice) {
        if (!(finalChoice instanceof Integer)) {
            throw new IllegalArgumentException("Expected door index."); // Ensure input is a valid Integer
        }
        int choice = (Integer) finalChoice; // Cast input to int
        return choice == winningDoor; // Return true if the choice matches the winning door
    }


    public int getWinningDoor() {
        return winningDoor;
    }

    public int getPlayerChoice() {
        return playerChoice;
    }
}
