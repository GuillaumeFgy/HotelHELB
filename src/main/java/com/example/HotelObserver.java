package com.example;

public interface HotelObserver {

    public void reserveRoom(String roomName);
    public void freeRoom(String roomName, char type);
    
}
