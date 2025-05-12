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

    @BeforeEach
    public void setUp() {
        // 3x3 floor layout: all 'E' rooms
        ArrayList<ArrayList<String>> layout = new ArrayList<>();
        layout.add(new ArrayList<>(Arrays.asList("E", "E", "E")));
        layout.add(new ArrayList<>(Arrays.asList("E", "E", "E")));
        layout.add(new ArrayList<>(Arrays.asList("E", "E", "E")));

        hotel = new Hotel(1, layout, new DummyObserver());
        strategy = new QuietZoneAssignment(hotel);

        edgeRoom = hotel.getFloor(1).getRoomAt(0, 0); // top-left
        centerRoom = hotel.getFloor(1).getRoomAt(1, 1); // center
    }

    @Test
    public void testFumerMustBeOnBorder() {
        Reservation smoker = new Reservation("Jean", "Smoky", 1, true, Reservation.StayPurpose.TOURISM, 0);
        List<Room> available = List.of(centerRoom, edgeRoom);

        AssignmentRequest assigned = strategy.createAssignmentRequest(available, smoker);

        assertEquals(edgeRoom.getName(), assigned.room.getName(), "Smoker should be placed on border");
    }

    @Test
    public void testNoNeighborConflictForFamily() {
        // Adult solo neighbor
        Room neighbor = hotel.getFloor(1).getRoomAt(1, 0);
        neighbor.reserveRoom(new Reservation("Paul", "Solo", 1, false, Reservation.StayPurpose.BUSINESS, 0));

        Reservation family = new Reservation("Famille", "Durand", 3, false, Reservation.StayPurpose.TOURISM, 1);

        // Add one conflict room and one safe fallback room
        Room fallback = hotel.getFloor(1).getRoomAt(0, 2); // top-right corner (edge and isolated)

        List<Room> available = List.of(centerRoom, fallback);

        AssignmentRequest assigned = strategy.createAssignmentRequest(available, family);

        assertEquals(fallback.getName(), assigned.room.getName(), "Family should avoid neighbor conflict if another room is available");
    }


    private static class DummyObserver implements HotelObserver {
        public void reserveRoom(String roomName) {}
        public void freeRoom(String roomName, char type) {}
    }
}
