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



    /* Hotel constructor
    Inputs: numberOfFloors – total number of floors; floorLayout – layout of rooms per floor; observer – observer for room events.
    Outputs: none.
    Description: Initializes a Hotel with given structure, dimensions, and sets up all floors with rooms. */
    public Hotel(int numberOfFloors, ArrayList<ArrayList<String>> floorLayout, HotelObserver observer) {
        this.numberOfFloors = numberOfFloors; 
        this.floorLayout = floorLayout; 
        this.observer = observer; // Store observer for UI updates
        this.floorMap = new HashMap<>(); 
        this.numRows = floorLayout.size(); // Get number of layout rows
        this.numCols = floorLayout.get(0).size(); // Get number of layout columns
        initializeFloors(); // Populate floor map with Floor objects
    }

    

    /* initializeFloors
    Inputs: none.
    Outputs: none.
    Description: Creates and stores Floor objects for each hotel level using the shared floor layout. */
    private void initializeFloors() {
        for (int i = 0; i < numberOfFloors; i++) {
            Floor floor = new Floor(i + 1, floorLayout);
            floorMap.put(i + 1, floor); // Add to floor map with level as key
        }
    }


    /* getAvailableRooms
    Inputs: none.
    Outputs: a list of all unreserved rooms in the hotel.
    Description: Iterates through all floors and collects rooms that are currently not reserved. */
    public List<Room> getAvailableRooms() {
        List<Room> availableRooms = new ArrayList<>(); // List to store free rooms

        for (int i = 1; i <= numberOfFloors; i++) {
            Floor floor = floorMap.get(i); // Get floor by number
            for (Room room : floor.getRoomMap().values()) {
                if (!room.isReserved()) { availableRooms.add(room); } // Add if room is free
            }
        }

        return availableRooms; // Return all collected free rooms
    }


    /* reserveRoom
    Inputs: request – contains reservation and room to assign.
    Outputs: none.
    Description: Reserves the specified room for the reservation and notifies the observer. */
    public void reserveRoom(AssignmentRequest request) {
        Room room = getRoom(request.room.getName()); // Find room by name
        room.reserveRoom(request.reservation); // Assign reservation to room
        observer.reserveRoom(request.room.getName()); // Notify observer (UI update)
    }

    /* freeRoom
    Inputs: roomName – name of the room to release.
    Outputs: none.
    Description: Frees the specified room and notifies the observer with its type. */
    public void freeRoom(String roomName) {
        Room room = getRoom(roomName); // Find room by name
        room.freeRoom(); // Clear reservation
        observer.freeRoom(roomName, room.getType()); // Notify observer (UI update)
    }

    /* getRoom
    Inputs: roomName – name of the room to find.
    Outputs: the Room object corresponding to the given name.
    Description: Searches all floors to find and return a room by its name; throws if not found. */
    public Room getRoom(String roomName) {
        for (Floor floor : floorMap.values()) {
            Room room = floor.getRoomMap().get(roomName); // Try to find the room
            if (room != null) return room; // Return if found
        }
        throw new IllegalArgumentException("Room " + roomName + " not found."); // Not found
    }


    public int getNumRows() { return numRows; }
    public int getNumCols() { return numCols; }
    public int getNumberOfFloors(){ return numberOfFloors; }
    public ArrayList<ArrayList<String>> getFloorLayout(){ return floorLayout; }
    public Floor getFloor(int level){ return floorMap.get(level);}
    public static String getLetterFromNumber(int number){return String.valueOf((char) ('A' + number));}
    public Map<Integer, Floor> getFloorMap() { return floorMap; }
    
}
