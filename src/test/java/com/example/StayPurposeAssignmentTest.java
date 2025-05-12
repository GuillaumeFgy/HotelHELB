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

    /* setUp
    Description: Sets up the test environment by initializing the assignment strategy and creating some test rooms. */
    @BeforeEach
    public void setUp() {
        strategy = new StayPurposeAssignment(); // Initialize the assignment strategy
        roomB = new Room("A1B", 0, 0, 'B'); // Business room
        roomL = new Room("A2L", 0, 1, 'L'); // Luxury room
        roomE = new Room("A3E", 0, 2, 'E'); // Economic room
    }

    /* testAssignsBusinessRoom
    Description: Tests that a business reservation is correctly assigned to a Business room. */
    @Test
    public void testAssignsBusinessRoom() {
        Reservation businessRes = new Reservation("Bob", "Martin", 1, false, Reservation.StayPurpose.BUSINESS, 0); // Business reservation
        List<Room> rooms = List.of(roomL, roomB); // Available rooms

        AssignmentRequest request = strategy.createAssignmentRequest(rooms, businessRes); // Assign room
        assertEquals("A1B", request.room.getName(), "Business reservation should choose room type B"); // Check that business room is chosen
    }

    /* testAssignsLuxuryRoomForTourism
    Description: Tests that a tourism reservation (non-smoker without children) is correctly assigned to a Luxury room. */
    @Test
    public void testAssignsLuxuryRoomForTourism() {
        Reservation tourismRes = new Reservation("Alice", "Smith", 2, false, Reservation.StayPurpose.TOURISM, 0); // Tourism reservation
        List<Room> rooms = List.of(roomE, roomL); // Available rooms

        AssignmentRequest request = strategy.createAssignmentRequest(rooms, tourismRes); // Assign room
        assertEquals("A2L", request.room.getName(), "Tourism non-smoker without children should choose room type L"); // Check that luxury room is chosen
    }

    /* testAssignsEconomicRoomForFamily
    Description: Tests that a family reservation (smoker with children) is correctly assigned to an Economic room. */
    @Test
    public void testAssignsEconomicRoomForFamily() {
        Reservation familyRes = new Reservation("Claire", "Dupont", 3, true, Reservation.StayPurpose.OTHER, 1); // Family reservation
        List<Room> rooms = List.of(roomL, roomE); // Available rooms

        AssignmentRequest request = strategy.createAssignmentRequest(rooms, familyRes); // Assign room
        assertEquals("A3E", request.room.getName(), "Smoker with child should be assigned to room type E"); // Check that economic room is chosen
    }

}
