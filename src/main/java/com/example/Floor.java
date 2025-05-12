package com.example;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Floor {

    private Map<String, Room> roomMap = new HashMap<>();
    private final String floorName;
    private final int level;
    
    private final ArrayList<ArrayList<String>> floorLayout;

    public Floor(int level, ArrayList<ArrayList<String>> floorLayout) {
        this.level = level;
        this.floorName = Hotel.getLetterFromNumber(level) + 1;
        this.floorLayout = floorLayout;
        initializeRooms();
    }

    /* initializeRooms
    Inputs: none (uses floorLayout and level fields).
    Outputs: none (populates the roomMap with Room instances).
    Description: Iterates over the floor layout and creates Room objects for each valid cell (not 'Z'), assigning them unique names. */
    private void initializeRooms() {
        int roomCounter = 1; // Used to number rooms on this floor
        for (int rowIndex = 0; rowIndex < floorLayout.size(); rowIndex++) {
            ArrayList<String> row = floorLayout.get(rowIndex);
            for (int colIndex = 0; colIndex < row.size(); colIndex++) {
                String code = row.get(colIndex); // Get room type code
                if (!code.equals("Z")) { // Skip if marked as void space
                    String roomName = Hotel.getLetterFromNumber(level - 1) + roomCounter + code; // Generate unique room name
                    Room room = new Room(roomName, rowIndex, colIndex, code.charAt(0));
                    roomMap.put(roomName, room); 
                    roomCounter++;
                }
            }
        }
    }

    
    
    public Map<String, Room> getRoomMap() {
        return roomMap;
    }

    /* getRoomAt
    Inputs: row – target row index; col – target column index.
    Outputs: the Room located at the given coordinates, or null if not found.
    Description: Searches the floor’s room map for a room at the specified position. */
    public Room getRoomAt(int row, int col) {
        for (Room room : roomMap.values()) {
            if (room.getRow() == row && room.getCol() == col) { return room; } // Match found
        }
        return null; // No room found at given coordinates
    }

    

}
