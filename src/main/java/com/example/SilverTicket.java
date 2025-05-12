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

    /* playGame
    Inputs: input – user guess (should be a String).
    Outputs: true if guess matches the original word; false otherwise.
    Description: Validates that the input is a string and checks if it matches the original word (case-insensitive). */
    @Override
    public boolean playGame(Object input) {
        if (!(input instanceof String)) {
            throw new IllegalArgumentException("Expected a String guess."); // Input must be a string
        }
        String guess = ((String) input).trim().toLowerCase(); // Clean and normalize
        return guess.equals(word.toLowerCase()); // Check against original word
    }

    /* getScrambledWord
    Inputs: none.
    Outputs: the scrambled version of the word.
    Description: Returns the word in its scrambled form, used in the guessing game. */
    public String getScrambledWord() {
        return scrambledWord;
    }

    /* getOriginalWord
    Inputs: none.
    Outputs: the original word.
    Description: Returns the original word to be guessed (used for validation). */
    public String getOriginalWord() {
        return word;
    }

    /* chooseRandomWord
    Inputs: none.
    Outputs: a randomly selected word from the predefined list.
    Description: Picks a word from the static list of possible words. */
    private String chooseRandomWord() {
        Random random = new Random();
        return WORDS.get(random.nextInt(WORDS.size())); // Pick random index
    }

    /* scramble
    Inputs: word – the original word.
    Outputs: a shuffled (scrambled) version of the word.
    Description: Randomly shuffles the letters of the input word to create a challenge. */
    private String scramble(String word) {
        List<String> letters = Arrays.asList(word.split("")); // Split into individual letters
        Collections.shuffle(letters); // Shuffle letters randomly
        return String.join("", letters); // Recombine into a scrambled string
    }

}
