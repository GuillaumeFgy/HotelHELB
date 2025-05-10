package com.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GoldTicket extends Ticket {
    private final int totalDoors;
    private final int winningDoor;
    private final List<Integer> remainingDoors = new ArrayList<>();
    private int playerChoice = -1;

    public GoldTicket(String code, int floor) {
        super(100, code);
        this.totalDoors = floor + 2;
        this.winningDoor = new Random().nextInt(totalDoors); // door index from 0 to n-1
        for (int i = 0; i < totalDoors; i++) {
            remainingDoors.add(i);
        }
    }

    public int getTotalDoors() {
        return totalDoors;
    }

    public List<Integer> getRemainingDoors() {
        return new ArrayList<>(remainingDoors); // defensive copy
    }

    public void setPlayerChoice(int doorIndex) {
        if (!remainingDoors.contains(doorIndex)) {
            throw new IllegalArgumentException("Invalid choice.");
        }
        this.playerChoice = doorIndex;
    }

    /**
     * Eliminates one non-winning, non-selected door.
     * Returns the door that was eliminated.
     */
    public int eliminateOneWrongDoor() {
        for (int door : remainingDoors) {
            if (door != winningDoor && door != playerChoice) {
                remainingDoors.remove((Integer) door);
                return door;
            }
        }
        throw new IllegalStateException("No door can be eliminated.");
    }

    /**
     * Once only 2 doors remain, this method finalizes the game with the final choice.
     * @param finalChoice the final door the user picks
     * @return true if it's the winning door
     */
    @Override
    public boolean playGame(Object finalChoice) {
        if (!(finalChoice instanceof Integer)) {
            throw new IllegalArgumentException("Expected door index.");
        }
        int choice = (Integer) finalChoice;
        return choice == winningDoor;
    }

    public int getWinningDoor() {
        return winningDoor;
    }

    public int getPlayerChoice() {
        return playerChoice;
    }
}
