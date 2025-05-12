package com.example;
import java.util.List;
import java.util.Random;

public interface AssignmentStrategy {
    String getStrategyDescription();
    AssignmentRequest createAssignmentRequest(List<Room> availableRooms, Reservation reservation);

    static final int[][] ADJACENT_OFFSETS = {
        {1, 0}, {-1, 0}, {0, 1}, {0, -1}
    };

    static char determineTargetType(Reservation reservation) {
        switch (reservation.getStayPurpose()) {
            case BUSINESS:
                return 'B';
            case TOURISM:
            case OTHER:
            default:
                if (!reservation.isSmoker() && reservation.getNumChildren() == 0) {
                    return 'L';
                } else {
                    return 'E';
                }
        }
    }

    static AssignmentRequest fallbackByType(List<Room> rooms, Reservation reservation, char type) {
        for (Room room : rooms) {
            if (room.getType() == type) {
                return new AssignmentRequest(reservation, room);
            }
        }
        return new AssignmentRequest(reservation, rooms.get(0));
    }

}


class RandomAssignment implements AssignmentStrategy {

    final Random random = new Random();

    @Override
    public String getStrategyDescription() {
        return "Random Assignment";
    }

    @Override
    public AssignmentRequest createAssignmentRequest(List<Room> availableRooms, Reservation reservation) { 
        return new AssignmentRequest(reservation,availableRooms.get(random.nextInt(availableRooms.size())));
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

    @Override
    public AssignmentRequest createAssignmentRequest(List<Room> availableRooms, Reservation reservation) {
        for (Room room : availableRooms) {
            if (isValidRoom(room, reservation)) {
                return new AssignmentRequest(reservation, room);
            }
        }

        // Fallback si aucune chambre ne respecte tout
        return new AssignmentRequest(reservation, availableRooms.get(0));
    }

    private boolean isValidRoom(Room room, Reservation reservation) {
        int row = room.getRow();
        int col = room.getCol();
        int numRows = hotel.getNumRows();
        int numCols = hotel.getNumCols();
        int floorLevel = room.getName().charAt(0) - 'A' + 1;

        // Règle 1 : Fumeurs doivent être sur un bord
        if (reservation.isSmoker()) {
            if (!(row == 0 || row == numRows - 1 || col == 0 || col == numCols - 1)) {
                return false;
            }
        }

        // Règle 2 : familles avec enfants et adultes seuls ne doivent pas être voisins
        boolean currentHasChildren = reservation.getNumChildren() > 0;

        for (int[] offset : AssignmentStrategy.ADJACENT_OFFSETS) {
            int neighborRow = row + offset[0];
            int neighborCol = col + offset[1];

            if (neighborRow >= 0 && neighborRow < numRows && neighborCol >= 0 && neighborCol < numCols) {
                Room neighbor = hotel.getFloor(floorLevel).getRoomAt(neighborRow, neighborCol);
                if (neighbor != null && neighbor.isReserved()) {
                    if (neighbor.hasChildren() != currentHasChildren) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}

class StayPurposeAssignment implements AssignmentStrategy {

    @Override
    public String getStrategyDescription() {
        return "Stay Purpose";
    }

   @Override
    public AssignmentRequest createAssignmentRequest(List<Room> availableRooms, Reservation reservation) {
        char targetType = AssignmentStrategy.determineTargetType(reservation);
        return AssignmentStrategy.fallbackByType(availableRooms, reservation, targetType);
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

    public AssignmentRequest createAssignmentRequest(List<Room> availableRooms, Reservation reservation) {
    char targetType = AssignmentStrategy.determineTargetType(reservation);

    for (int floorNum = 1; floorNum <= hotel.getNumberOfFloors(); floorNum++) {
        Floor floor = hotel.getFloor(floorNum);
        for (Room room : floor.getRoomMap().values()) {
            if (!room.isReserved() && room.getType() == targetType && isAdjacentToReserved(room, floor)) {
                return new AssignmentRequest(reservation, room);
            }
        }
    }

    return AssignmentStrategy.fallbackByType(availableRooms, reservation, targetType);
}


    private boolean isAdjacentToReserved(Room room, Floor floor) {
        int row = room.getRow();
        int col = room.getCol();

        for (int[] offset : AssignmentStrategy.ADJACENT_OFFSETS) {
            Room neighbor = floor.getRoomAt(row + offset[0], col + offset[1]);
            if (neighbor != null && neighbor.isReserved()) {
                return true;
            }
        }
        return false;
    }
}






