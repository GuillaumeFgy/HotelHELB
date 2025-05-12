package com.example;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ReservationParser {

    /* parseFile
    Inputs: filename – path to the CSV reservation file.
    Outputs: list of valid Reservation objects.
    Description: Parses the file, filters valid reservations, and overwrites the file with only invalid lines. */
    public static List<Reservation> parseFile(String filename) {
        List<Reservation> validReservations = new ArrayList<>(); // Store accepted reservations
        List<String> invalidLines = new ArrayList<>(); // Store rejected lines

        try (Scanner scanner = new Scanner(new File(filename))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim(); // Read and clean line
                if (line.isEmpty()) continue; // Skip blank lines

                try {
                    Reservation res = parseLine(line); // Try parsing line
                    if (isValid(res)) {
                        validReservations.add(res); // Add if valid
                    } else {
                        invalidLines.add(line); // Log invalid
                    }
                } catch (Exception e) {
                    invalidLines.add(line); // Malformed or exception
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading reservation file: " + e.getMessage()); // Read error
        }

        try (PrintWriter writer = new PrintWriter(new File(filename))) {
            for (String line : invalidLines) {
                writer.println(line); // Rewrite file with only invalid lines
            }
        } catch (IOException e) {
            System.err.println("Error writing reservation file: " + e.getMessage()); // Write error
        }

        return validReservations; // Return parsed list
    }

    /* parseLine
    Inputs: line – one line from the CSV file.
    Outputs: a Reservation object.
    Description: Splits a CSV line and builds a Reservation from the values. */
    private static Reservation parseLine(String line) {
        String[] tokens = line.split(","); // Expect 6 tokens
        if (tokens.length != 6) throw new IllegalArgumentException("Invalid CSV line: " + line);

        String firstName = tokens[0].trim();
        String lastName = tokens[1].trim();
        int numPersons = Integer.parseInt(tokens[2].trim());
        boolean smoker = tokens[3].trim().equalsIgnoreCase("fumeur"); // French word
        Reservation.StayPurpose purpose = Reservation.StayPurpose.fromString(tokens[4].trim());
        int numChildren = Integer.parseInt(tokens[5].trim());

        return new Reservation(firstName, lastName, numPersons, smoker, purpose, numChildren);
    }

    /* isValid
    Inputs: res – a Reservation to check.
    Outputs: true if valid; false otherwise.
    Description: Verifies that the reservation meets constraints (person count, child count, names not empty). */
    private static boolean isValid(Reservation res) {
        int total = res.getNumPersons();
        int children = res.getNumChildren();
        return total >= 1 && total <= 4 && children < total
                && !res.getFirstName().isEmpty() && !res.getLastName().isEmpty();
    }

}
