package com.example;
import java.util.List;
import java.util.Random;

public interface AssignmentStrategy {
    String getStrategyDescription();
    AssignmentRequest createAssignmentRequest(List<Room> availableRooms, Reservation reservation);

    static final int[][] ADJACENT_OFFSETS = {
        {1, 0}, {-1, 0}, {0, 1}, {0, -1}
    };

    /* determineTargetType
    Inputs: reservation – reservation details (stay purpose, smoker, children).
    Outputs: 'B' – for business stays, 'L' – for quiet luxury stays, 'E' – for others.
    Description: Determines the preferred room type code (B/L/E) based on the reservation’s attributes. */
    static char determineTargetType(Reservation reservation) {
        switch (reservation.getStayPurpose()) { // Check the stay purpose
            case BUSINESS:
                return 'B'; // Business guests get Business room
            case TOURISM:
            case OTHER:
            default:
                if (!reservation.isSmoker() && reservation.getNumChildren() == 0) {
                    return 'L'; // Non-smoking solo adults get Luxury room
                } else {
                    return 'E'; // All others get Economic room
                }
        }
    }

    /* fallbackByType
    Inputs: rooms – list of candidate rooms; reservation – reservation to assign; type – desired room type.
    Outputs: an AssignmentRequest pairing the reservation with a matching or fallback room.
    Description: Searches for a room of the specified type; if none is found, assigns the first available room as fallback. */
    static AssignmentRequest fallbackByType(List<Room> rooms, Reservation reservation, char type) {
        for (Room room : rooms) { // Iterate through all candidate rooms
            if (room.getType() == type) { // Check if room type matches
                return new AssignmentRequest(reservation, room); // Return matching room
            }
        }
        return new AssignmentRequest(reservation, rooms.get(0)); // Fallback to first room if no match
    }

}


class RandomAssignment implements AssignmentStrategy {

    final Random random = new Random();

    @Override
    public String getStrategyDescription() {
        return "Random Assignment";
    }

    /* createAssignmentRequest
    Inputs: availableRooms – list of rooms to choose from; reservation – reservation to assign.
    Outputs: an AssignmentRequest with a randomly selected room.
    Description: Assigns the reservation to a random room from the list of available rooms. */
    @Override
    public AssignmentRequest createAssignmentRequest(List<Room> availableRooms, Reservation reservation) {
        return new AssignmentRequest(reservation, availableRooms.get(random.nextInt(availableRooms.size()))); // Pick random room
    }


}

class QuietZoneAssignment implements AssignmentStrategy {

    private final Hotel hotel;

    public QuietZoneAssignment(Hotel hotel) {
        this.hotel = hotel;
    }

    @Override
    public String getStrategyDescription() {
        return "Quiet Zone";
    }

    /* createAssignmentRequest
    Inputs: availableRooms – list of rooms to consider; reservation – reservation to assign.
    Outputs: an AssignmentRequest with a valid room, or fallback room if none meet constraints.
    Description: Tries to find a valid room based on quiet zone rules; falls back to the first room if none qualify. */
    @Override
    public AssignmentRequest createAssignmentRequest(List<Room> availableRooms, Reservation reservation) {
        for (Room room : availableRooms) {
            if (isValidRoom(room, reservation)) { // Check if room satisfies quiet zone conditions
                return new AssignmentRequest(reservation, room); // Return the first valid match
            }
        }

        return new AssignmentRequest(reservation, availableRooms.get(0)); // Fallback to first available room
    }


    /* isValidRoom
    Inputs: room – candidate room; reservation – reservation being evaluated.
    Outputs: true – if the room satisfies smoking and adjacency constraints; false – otherwise.
    Description: Checks if the room respects edge placement for smokers and avoids child/adult neighbor conflicts. */
    private boolean isValidRoom(Room room, Reservation reservation) {
        int row = room.getRow(); // Get room row
        int col = room.getCol(); // Get room column
        int numRows = hotel.getNumRows(); // Total rows on floor
        int numCols = hotel.getNumCols(); // Total columns on floor
        int floorLevel = room.getName().charAt(0) - 'A' + 1; // Extract floor level from room name

        if (reservation.isSmoker()) { // Rule 1: Smokers must be on the edge
            if (!(row == 0 || row == numRows - 1 || col == 0 || col == numCols - 1)) return false;
        }

        boolean currentHasChildren = reservation.getNumChildren() > 0; // Rule 2: no child/adult neighbor mix

        for (int[] offset : AssignmentStrategy.ADJACENT_OFFSETS) { // Check all 4 neighbors
            int neighborRow = row + offset[0];
            int neighborCol = col + offset[1];

            if (neighborRow >= 0 && neighborRow < numRows && neighborCol >= 0 && neighborCol < numCols) {
                Room neighbor = hotel.getFloor(floorLevel).getRoomAt(neighborRow, neighborCol); // Get neighbor room
                if (neighbor != null && neighbor.isReserved()) {
                    if (neighbor.hasChildren() != currentHasChildren) return false; // Conflict: child next to adult or vice versa
                }
            }
        }

        return true; // Room is valid
    }

}

class StayPurposeAssignment implements AssignmentStrategy {

    @Override
    public String getStrategyDescription() {
        return "Stay Purpose";
    }

   /* createAssignmentRequest
    Inputs: availableRooms – list of available rooms; reservation – reservation to assign.
    Outputs: an AssignmentRequest based on preferred room type.
    Description: Determines the target room type for the reservation and assigns a matching or fallback room. */
    @Override
    public AssignmentRequest createAssignmentRequest(List<Room> availableRooms, Reservation reservation) {
        char targetType = AssignmentStrategy.determineTargetType(reservation); // Determine desired room type
        return AssignmentStrategy.fallbackByType(availableRooms, reservation, targetType); // Assign matching or fallback room
    }

}

class SequentialAssignment implements AssignmentStrategy {

    private final Hotel hotel;

    public SequentialAssignment(Hotel hotel) {
        this.hotel = hotel;
    }

    @Override
    public String getStrategyDescription() {
        return "Sequential Assignment";
    }

    /* createAssignmentRequest
    Inputs: availableRooms – list of available rooms; reservation – reservation to assign.
    Outputs: an AssignmentRequest using sequential logic or fallback.
    Description: Tries to assign a room of the target type that is adjacent to another reserved room, floor by floor. Falls back if none match. */
    public AssignmentRequest createAssignmentRequest(List<Room> availableRooms, Reservation reservation) {
        char targetType = AssignmentStrategy.determineTargetType(reservation); // Determine preferred room type

        for (int floorNum = 1; floorNum <= hotel.getNumberOfFloors(); floorNum++) { // Loop over each floor
            Floor floor = hotel.getFloor(floorNum); // Get current floor
            for (Room room : floor.getRoomMap().values()) { // Check all rooms on floor
                if (!room.isReserved() && room.getType() == targetType && isAdjacentToReserved(room, floor)) {
                    return new AssignmentRequest(reservation, room); // Found a suitable room
                }
            }
        }

        return AssignmentStrategy.fallbackByType(availableRooms, reservation, targetType); // Fallback to general strategy
    }



    /* isAdjacentToReserved
    Inputs: room – room to evaluate; floor – floor where the room is located.
    Outputs: true – if at least one adjacent room is reserved; false – otherwise.
    Description: Checks the four adjacent cells around the room to see if any are currently reserved. */
    private boolean isAdjacentToReserved(Room room, Floor floor) {
        int row = room.getRow(); // Current room row
        int col = room.getCol(); // Current room column

        for (int[] offset : AssignmentStrategy.ADJACENT_OFFSETS) { // Loop over 4 directions
            Room neighbor = floor.getRoomAt(row + offset[0], col + offset[1]); // Get neighbor
            if (neighbor != null && neighbor.isReserved()) return true; // Found a reserved neighbor
        }

        return false; // No reserved neighbors found
    }

}






