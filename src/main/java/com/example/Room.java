package com.example;
public class Room {
    private final String name;
    private final int row;
    private final int col;
    private final char type; // 'E', 'B', 'L'

    private boolean reserved;
    private Reservation reservation;

    public Room(String name, int row, int col, char type) {
        this.name = name;
        this.row = row;
        this.col = col;
        this.type = type;

        this.reserved = false;
        this.reservation = EmptyReservation.getInstance();
    }

    public void reserveRoom(Reservation reservation)
    {
        this.reserved = true;
        this.reservation = reservation;
    }

    public void freeRoom()
    {
        this.reserved = false;
        this.reservation = EmptyReservation.getInstance();
    }

    public boolean hasChildren() {
        return reserved && reservation.getNumChildren() > 0;
    }

    public boolean isReserved(){ return reserved; }
    public String getName() { return name; }
    public int getRow() { return row; }
    public int getCol() { return col; }
    public char getType() { return type; }
    public Reservation getReservation(){ return reservation; }

}

