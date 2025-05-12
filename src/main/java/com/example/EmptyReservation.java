package com.example;

public class EmptyReservation extends Reservation{
    private static EmptyReservation instance;

    private EmptyReservation()
    {
        super("", "", 0, false, null, 0);
    }

    /* getInstance
    Inputs: none.
    Outputs: the single instance of EmptyReservation.
    Description: Implements the Singleton pattern to return the only instance of EmptyReservation. */
    public static EmptyReservation getInstance() {
        if (instance == null) { instance = new EmptyReservation(); } // Create instance if not already created
        return instance; // Return the singleton instance
    }


    @Override
    public String toString()
    {
        return "Empty reservation";
    }
}
