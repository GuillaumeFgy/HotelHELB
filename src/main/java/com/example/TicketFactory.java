package com.example;

import java.util.Random;

public class TicketFactory {

    private static final Random random = new Random();

    public static Ticket createTicket(Room room, int rating) {
        int roomWeight = getRoomWeight(room.getType());
        int score = roomWeight * rating;

        int roll = random.nextInt(100); // 0 to 99
        String code = DiscountCodeGenerator.generateCode(getDiscount(score)); // Placeholder

        if (score <= 6) {
            if (roll < 70) return new BronzeTicket(code);
            else return new SilverTicket(code);
        } else if (score <= 10) {
            if (roll < 25) return new GoldTicket(code, getFloorNumber(room));
            else if (roll < 75) return new SilverTicket(code);
            else return new BronzeTicket(code);
        } else {
            if (roll < 60) return new GoldTicket(code, getFloorNumber(room));
            else if (roll < 90) return new SilverTicket(code);
            else return new BronzeTicket(code);
        }
    }

    private static int getRoomWeight(char type) {
        switch (type) {
            case 'L': return 3;
            case 'B': return 2;
            case 'E': return 1;
            default: throw new IllegalArgumentException("Invalid room type: " + type);
        }
    }

    private static int getFloorNumber(Room room) {
        return room.getName().charAt(0) - 'A' + 1;
    }

    private static int getDiscount(int score) {
        if (score <= 6) return 25;
        if (score <= 10) return 50;
        return 100;
    }
}
