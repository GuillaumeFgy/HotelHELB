package com.example;

public class AssignmentRequest {
    public Reservation reservation;
    public Room room;

    public AssignmentRequest(Reservation reservation, Room room) 
    {
        this.reservation = reservation;
        this.room = room;
    }


    /* equals
    Inputs: Object o – the object to compare with this AssignmentRequest.
    Outputs: boolean – true if both reservation and room name match; false otherwise.
    Description: Compares this AssignmentRequest with another to check if they represent the same reservation and room. */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true; // Same instance, they are equal
        if (o == null || getClass() != o.getClass()) return false; // Null or different type, not equal
        AssignmentRequest that = (AssignmentRequest) o; // Cast to AssignmentRequest
        return reservation.equals(that.reservation) && room.getName().equals(that.room.getName()); // Compare reservation and room name
    }


    /* hashCode
    Inputs: none.
    Outputs: int – the computed hash code combining reservation and room name.
    Description: Generates a hash code for this AssignmentRequest using its reservation and room name. */
    @Override
    public int hashCode() {
        return reservation.hashCode() * 31 + room.getName().hashCode(); // Multiply reservation hash by 31 and add room name hash
    }


}
