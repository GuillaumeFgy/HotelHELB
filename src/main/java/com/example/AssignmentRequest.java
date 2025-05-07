package com.example;

public class AssignmentRequest {
    public Reservation reservation;
    public Room room;

    public AssignmentRequest(Reservation reservation, Room room) 
    {
        this.reservation = reservation;
        this.room = room;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AssignmentRequest that = (AssignmentRequest) o;
        return reservation.equals(that.reservation) && room.getName().equals(that.room.getName());
    }

    @Override
    public int hashCode() {
        return reservation.hashCode() * 31 + room.getName().hashCode();
    }

}
