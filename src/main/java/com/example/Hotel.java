package com.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Hotel {

    private Map<Integer, Floor> floorMap;
    private HotelObserver observer;
    private final int numberOfFloors;
    private final ArrayList<ArrayList<String>> floorLayout;
    private final int numRows;
    private final int numCols;



    public Hotel(int numberOfFloors, ArrayList<ArrayList<String>> floorLayout, HotelObserver observer)
    {
        this.numberOfFloors = numberOfFloors;
        this.floorLayout = floorLayout;
        this.observer = observer;
        this.floorMap = new HashMap<>();
        this.numRows = floorLayout.size();
        this.numCols = floorLayout.get(0).size();
        initializeFloors();
    }
    

    private void initializeFloors()
    {
        for (int i = 0; i < numberOfFloors; i++)
        {
            Floor floor = new Floor(i + 1, floorLayout);
            floorMap.put(i + 1, floor);
        }
    }

    public List<Room> getAvailableRooms() {
        List<Room> availableRooms = new ArrayList<>();
        for (int i = 1; i <= numberOfFloors; i++) {
            Floor floor = floorMap.get(i);
            for (Room room : floor.getRoomMap().values()) {
                if (!room.isReserved()) {
                    availableRooms.add(room);
                }
            }
        }
        return availableRooms;
    }

    public void reserveRoom(AssignmentRequest request)
    {
        Room room = getRoom(request.room.getName());
        room.reserveRoom(request.reservation);
        observer.reserveRoom(request.room.getName());
    }

    public void freeRoom(String roomName)
    {
        Room room = getRoom(roomName);
        room.freeRoom();
        observer.freeRoom(roomName, room.getType());
    }

    public Room getRoom(String roomName) {
        for (Floor floor : floorMap.values()) {
            Room room = floor.getRoomMap().get(roomName);
            if (room != null) return room;
        }
        throw new IllegalArgumentException("Room " + roomName + " not found.");
    }

    public int getNumRows() { return numRows; }
    public int getNumCols() { return numCols; }
    public int getNumberOfFloors(){ return numberOfFloors; }
    public ArrayList<ArrayList<String>> getFloorLayout(){ return floorLayout; }
    public Floor getFloor(int level){ return floorMap.get(level);}
    public static String getLetterFromNumber(int number){return String.valueOf((char) ('A' + number));}
    
}
