package com.example;

public class EmptyReservation extends Reservation{
    private static EmptyReservation instance;

    private EmptyReservation()
    {
        super("", "", 0, false, null, 0);
    }

    public static EmptyReservation getInstance()
    {
        if (instance == null)
        {
            instance = new EmptyReservation();
        }
        return instance;
    }

    @Override
    public String toString()
    {
        return "Empty reservation";
    }
}
