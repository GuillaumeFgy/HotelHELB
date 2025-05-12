package com.example;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

public class StayPurposeAssignmentTest {

    private StayPurposeAssignment strategy;

    private Room roomB; // Business
    private Room roomL; // Luxury
    private Room roomE; // Economic

    @BeforeEach
    public void setUp() {
        strategy = new StayPurposeAssignment();
        roomB = new Room("A1B", 0, 0, 'B');
        roomL = new Room("A2L", 0, 1, 'L');
        roomE = new Room("A3E", 0, 2, 'E');
    }

    @Test
    public void testAssignsBusinessRoom() {
        Reservation businessRes = new Reservation("Bob", "Martin", 1, false, Reservation.StayPurpose.BUSINESS, 0);
        List<Room> rooms = List.of(roomL, roomB);

        AssignmentRequest request = strategy.createAssignmentRequest(rooms, businessRes);
        assertEquals("A1B", request.room.getName(), "Business reservation should choose room type B");
    }

    @Test
    public void testAssignsLuxuryRoomForTourism() {
        Reservation tourismRes = new Reservation("Alice", "Smith", 2, false, Reservation.StayPurpose.TOURISM, 0);
        List<Room> rooms = List.of(roomE, roomL);

        AssignmentRequest request = strategy.createAssignmentRequest(rooms, tourismRes);
        assertEquals("A2L", request.room.getName(), "Tourism non-smoker without children should choose room type L");
    }

    @Test
    public void testAssignsEconomicRoomForFamily() {
        Reservation familyRes = new Reservation("Claire", "Dupont", 3, true, Reservation.StayPurpose.OTHER, 1);
        List<Room> rooms = List.of(roomL, roomE);

        AssignmentRequest request = strategy.createAssignmentRequest(rooms, familyRes);
        assertEquals("A3E", request.room.getName(), "Smoker with child should be assigned to room type E");
    }
}
