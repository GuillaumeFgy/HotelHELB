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

    private void initializeRooms() {
        int roomCounter = 1;
        for (int rowIndex = 0; rowIndex < floorLayout.size(); rowIndex++) {
            ArrayList<String> row = floorLayout.get(rowIndex);
            for (int colIndex = 0; colIndex < row.size(); colIndex++) {
                String code = row.get(colIndex);
                if (!code.equals("Z")) {
                    String roomName = Hotel.getLetterFromNumber(level - 1) + roomCounter + code;
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

    public Room getRoomAt(int row, int col) {
        for (Room room : roomMap.values()) {
            if (room.getRow() == row && room.getCol() == col) {
                return room;
            }
        }
        return null;
    }
    

}
