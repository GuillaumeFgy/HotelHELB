package com.example;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class SilverTicket extends Ticket {
    private final String word;
    private final String scrambledWord;

    private static final List<String> WORDS = Arrays.asList(
        "helb", "hotel", "vacation", "client", "java", "ticket", "luxury", "plaine", "view", "room", "bronze",
        "silver", "journey", "game", "open", "closed", "operator", "floor", "reservation", "logic", "constraint"
    );

    public SilverTicket(String code) {
        super(50, code);
        this.word = chooseRandomWord();
        this.scrambledWord = scramble(word);
    }

    @Override
    public boolean playGame(Object input) {
        if (!(input instanceof String)) {
            throw new IllegalArgumentException("Expected a String guess.");
        }
        String guess = ((String) input).trim().toLowerCase();
        return guess.equals(word.toLowerCase());
    }

    public String getScrambledWord() {
        return scrambledWord;
    }

    public String getOriginalWord() {
        return word;
    }

    private String chooseRandomWord() {
        Random random = new Random();
        return WORDS.get(random.nextInt(WORDS.size()));
    }

    private String scramble(String word) {
        List<String> letters = Arrays.asList(word.split(""));
        Collections.shuffle(letters);
        return String.join("", letters);
    }
}
