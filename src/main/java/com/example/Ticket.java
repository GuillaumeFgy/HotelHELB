package com.example;

public abstract class Ticket {
    protected final int discount;
    protected final String code;

    public Ticket(int discount, String code) {
        this.discount = discount;
        this.code = code;
    }

    public int getDiscount() {
        return discount;
    }

    public String getCode() {
        return code;
    }

    public abstract boolean playGame(Object input);
}


