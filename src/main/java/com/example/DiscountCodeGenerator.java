package com.example;

import java.security.SecureRandom;

public class DiscountCodeGenerator {

    private static final SecureRandom random = new SecureRandom();
    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    /* generateCode
    Inputs: discountPercent – percentage discount to encode (e.g., 25, 50, 100).
    Outputs: a 10-character discount code with the discount encoded in the last character.
    Description: Generates a random alphanumeric discount code with an encoded discount marker at the end. */
    public static String generateCode(int discountPercent) {
        StringBuilder code = new StringBuilder(); // Initialize empty code

        for (int i = 0; i < 9; i++) {
            code.append(ALPHABET.charAt(random.nextInt(ALPHABET.length()))); // Append 9 random characters
        }

        char encoded = encodeDiscount(discountPercent); // Encode the discount value
        code.append(encoded); // Append encoded character to complete code

        return code.toString(); // Return final code
    }


    /* decodeDiscount
    Inputs: code – a 10-character discount code.
    Outputs: the decoded discount percentage (e.g., 25, 50, 100).
    Description: Validates the code length and extracts the encoded discount from the last character. */
    public static int decodeDiscount(String code) {
        if (code.length() != 10) throw new IllegalArgumentException("Invalid code length."); // Must be exactly 10 characters
        return decodeDiscountChar(code.charAt(9)); // Decode last character to get discount
    }


    /* encodeDiscount
    Inputs: discount – discount percentage to encode (expected: 25, 50, 100).
    Outputs: a character representing the encoded discount.
    Description: Maps specific discount values to fixed characters for code generation. */
    private static char encodeDiscount(int discount) {
        switch (discount) {
            case 25: return 'Q'; // Encode 25% as 'Q'
            case 50: return 'W'; // Encode 50% as 'W'
            case 100: return 'Z'; // Encode 100% as 'Z'
            default: throw new IllegalArgumentException("Invalid discount value."); // Invalid input
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
