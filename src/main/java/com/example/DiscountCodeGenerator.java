package com.example;

import java.security.SecureRandom;

public class DiscountCodeGenerator {

    private static final SecureRandom random = new SecureRandom();
    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    // Encodes discount in a private way (simple XOR encoding here for illustration)
    public static String generateCode(int discountPercent) {
        StringBuilder code = new StringBuilder();

        for (int i = 0; i < 9; i++) {
            code.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
        }

        // Encode the discount in the last char (e.g., XOR with a fixed key)
        char encoded = encodeDiscount(discountPercent);
        code.append(encoded);

        return code.toString();
    }

    public static int decodeDiscount(String code) {
        if (code.length() != 10) throw new IllegalArgumentException("Invalid code length.");
        return decodeDiscountChar(code.charAt(9));
    }

    private static char encodeDiscount(int discount) {
        switch (discount) {
            case 25: return 'Q'; // Arbitrary encoding
            case 50: return 'W';
            case 100: return 'Z';
            default: throw new IllegalArgumentException("Invalid discount value.");
        }
    }

    private static int decodeDiscountChar(char ch) {
        switch (ch) {
            case 'Q': return 25;
            case 'W': return 50;
            case 'Z': return 100;
            default: throw new IllegalArgumentException("Invalid encoded character.");
        }
    }
}
