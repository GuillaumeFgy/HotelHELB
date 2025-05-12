package com.example;
public class Room {
    private final String name;
    private final int row;
    private final int col;
    private final char type; // 'E', 'B', 'L'

    private boolean reserved;
    private Reservation reservation;

    /* Room constructor
    Inputs: name – room identifier; row – row position; col – column position; type – room type ('L', 'B', 'E').
    Outputs: none.
    Description: Initializes a Room with its position, type, and sets it as unreserved with an EmptyReservation. */
    public Room(String name, int row, int col, char type) {
        this.name = name;
        this.row = row;
        this.col = col;
        this.type = type;

        this.reserved = false; // Room starts as unreserved
        this.reservation = EmptyReservation.getInstance(); // Placeholder reservation
    }

    /* reserveRoom
    Inputs: reservation – the reservation to assign to this room.
    Outputs: none.
    Description: Marks the room as reserved and stores the reservation object. */
    public void reserveRoom(Reservation reservation) {
        this.reserved = true; // Mark as reserved
        this.reservation = reservation; // Assign actual reservation
    }

    /* freeRoom
    Inputs: none.
    Outputs: none.
    Description: Frees the room and replaces the current reservation with an EmptyReservation. */
    public void freeRoom() {
        this.reserved = false; // Mark as available
        this.reservation = EmptyReservation.getInstance(); // Reset reservation to empty
    }

    /* hasChildren
    Inputs: none.
    Outputs: true if the room is reserved and the reservation includes children.
    Description: Checks whether the current reservation has one or more children. */
    public boolean hasChildren() {
        return reserved && reservation.getNumChildren() > 0; // Only true if room is reserved and has kids
    }


    public boolean isReserved(){ return reserved; }
    public String getName() { return name; }
    public int getRow() { return row; }
    public int getCol() { return col; }
    public char getType() { return type; }
    public Reservation getReservation(){ return reservation; }

}

