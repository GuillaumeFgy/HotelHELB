package com.example;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

public class QuietZoneAssignmentTest {

    private QuietZoneAssignment strategy;
    private Hotel hotel;
    private Room edgeRoom;
    private Room centerRoom;

    /* setUp
    Description: Sets up the test environment with a 3x3 floor layout where all rooms are of type 'E', and initializes the hotel and strategy for testing. */
    @BeforeEach
    public void setUp() {
        // 3x3 floor layout: all 'E' rooms
        ArrayList<ArrayList<String>> layout = new ArrayList<>();
        layout.add(new ArrayList<>(Arrays.asList("E", "E", "E")));
        layout.add(new ArrayList<>(Arrays.asList("E", "E", "E")));
        layout.add(new ArrayList<>(Arrays.asList("E", "E", "E")));

        hotel = new Hotel(1, layout, new DummyObserver()); // Create hotel with dummy observer
        strategy = new QuietZoneAssignment(hotel); // Strategy to assign rooms in a quiet zone

        edgeRoom = hotel.getFloor(1).getRoomAt(0, 0); // top-left room (border)
        centerRoom = hotel.getFloor(1).getRoomAt(1, 1); // center room (non-border)
    }

    /* testFumerMustBeOnBorder
    Description: Verifies that a smoker is assigned to a room on the border of the hotel floor. */
    @Test
    public void testFumerMustBeOnBorder() {
        Reservation smoker = new Reservation("Jean", "Smoky", 1, true, Reservation.StayPurpose.TOURISM, 0); // Smoker reservation
        List<Room> available = List.of(centerRoom, edgeRoom); // Available rooms (center and edge)

        AssignmentRequest assigned = strategy.createAssignmentRequest(available, smoker); // Assign room based on strategy

        assertEquals(edgeRoom.getName(), assigned.room.getName(), "Smoker should be placed on border"); // Smoker should be placed on edge
    }

    /* testNoNeighborConflictForFamily
    Description: Verifies that a family with children is not assigned next to a solo adult. */
    @Test
    public void testNoNeighborConflictForFamily() {
        // Adult solo neighbor
        Room neighbor = hotel.getFloor(1).getRoomAt(1, 0); // Neighbor room (next to family)
        neighbor.reserveRoom(new Reservation("Paul", "Solo", 1, false, Reservation.StayPurpose.BUSINESS, 0)); // Reserve for solo adult

        Reservation family = new Reservation("Famille", "Durand", 3, false, Reservation.StayPurpose.TOURISM, 1); // Family reservation with children

        // Add one conflict room and one safe fallback room
        Room fallback = hotel.getFloor(1).getRoomAt(0, 2); // top-right corner (isolated)

        List<Room> available = List.of(centerRoom, fallback); // Available rooms

        AssignmentRequest assigned = strategy.createAssignmentRequest(available, family); // Assign room based on strategy

        assertEquals(fallback.getName(), assigned.room.getName(), "Family should avoid neighbor conflict if another room is available"); // Family should get fallback room
    }



    private static class DummyObserver implements HotelObserver {
        public void reserveRoom(String roomName) {}
        public void freeRoom(String roomName, char type) {}
    }
}
