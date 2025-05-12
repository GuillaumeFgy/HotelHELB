package com.example;

import java.util.Random;

public class TicketFactory {

    private static final Random random = new Random();

    // Configurable variables
    private static final int LOW_SCORE_THRESHOLD = 6;
    private static final int MID_SCORE_THRESHOLD = 10;

    private static final int BRONZE_ROLL_THRESHOLD_LOW = 70;
    private static final int GOLD_ROLL_THRESHOLD_MID = 25;
    private static final int SILVER_ROLL_THRESHOLD_MID = 75;
    private static final int GOLD_ROLL_THRESHOLD_HIGH = 60;
    private static final int SILVER_ROLL_THRESHOLD_HIGH = 90;

    private static final int LOW_SCORE_DISCOUNT = 25;
    private static final int MID_SCORE_DISCOUNT = 50;
    private static final int HIGH_SCORE_DISCOUNT = 100;

    public static Ticket createTicket(Room room, int rating) {
        int roomWeight = getRoomWeight(room.getType());
        int score = roomWeight * rating;

        int roll = random.nextInt(100); // Random roll between 0–99
        String code = DiscountCodeGenerator.generateCode(getDiscount(score));

        if (score <= LOW_SCORE_THRESHOLD) {
            if (roll < BRONZE_ROLL_THRESHOLD_LOW) return new BronzeTicket(code);
            else return new SilverTicket(code);
        } else if (score <= MID_SCORE_THRESHOLD) {
            if (roll < GOLD_ROLL_THRESHOLD_MID) return new GoldTicket(code, getFloorNumber(room));
            else if (roll < SILVER_ROLL_THRESHOLD_MID) return new SilverTicket(code);
            else return new BronzeTicket(code);
        } else {
            if (roll < GOLD_ROLL_THRESHOLD_HIGH) return new GoldTicket(code, getFloorNumber(room));
            else if (roll < SILVER_ROLL_THRESHOLD_HIGH) return new SilverTicket(code);
            else return new BronzeTicket(code);
        }
    }

    private static int getRoomWeight(char type) {
        switch (type) {
            case 'L': return 3; // Luxury
            case 'B': return 2; // Business
            case 'E': return 1; // Economic
            default: throw new IllegalArgumentException("Invalid room type: " + type);
        }
    }

    private static int getFloorNumber(Room room) {
        return room.getName().charAt(0) - 'A' + 1;
    }

    private static int getDiscount(int score) {
        if (score <= LOW_SCORE_THRESHOLD) return LOW_SCORE_DISCOUNT;
        if (score <= MID_SCORE_THRESHOLD) return MID_SCORE_DISCOUNT;
        return HIGH_SCORE_DISCOUNT;
    }
}