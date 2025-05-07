package com.example;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ReservationParser {

    public static List<Reservation> parseFile(String filename) {
        List<Reservation> validReservations = new ArrayList<>();
        List<String> invalidLines = new ArrayList<>();

        try (Scanner scanner = new Scanner(new File(filename))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;

                try {
                    Reservation res = parseLine(line);
                    if (isValid(res)) {
                        validReservations.add(res);
                    } else {
                        invalidLines.add(line);
                    }
                } catch (Exception e) {
                    invalidLines.add(line); // ligne invalide ou mal formée
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading reservation file: " + e.getMessage());
        }

        // Réécrit le fichier avec uniquement les réservations invalides
        try (PrintWriter writer = new PrintWriter(new File(filename))) {
            for (String line : invalidLines) {
                writer.println(line);
            }
        } catch (IOException e) {
            System.err.println("Error writing reservation file: " + e.getMessage());
        }

        return validReservations;
    }


    private static Reservation parseLine(String line) {
        String[] tokens = line.split(",");
        if (tokens.length != 6) throw new IllegalArgumentException("Invalid CSV line: " + line);

        String firstName = tokens[0].trim();
        String lastName = tokens[1].trim();
        int numPersons = Integer.parseInt(tokens[2].trim());
        boolean smoker = tokens[3].trim().equalsIgnoreCase("fumeur");
        Reservation.StayPurpose purpose = Reservation.StayPurpose.fromString(tokens[4].trim());
        int numChildren = Integer.parseInt(tokens[5].trim());

        return new Reservation(firstName, lastName, numPersons, smoker, purpose, numChildren);
    }

    private static boolean isValid(Reservation res) {
        int total = res.getNumPersons();
        int children = res.getNumChildren();
        return total >= 1 && total <= 4 && children < total
                && !res.getFirstName().isEmpty() && !res.getLastName().isEmpty();
    }
}
