package com.example;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class ConfigurationParser {

    private int numberOfFloors;
    private ArrayList<ArrayList<String>> floorLayout = new ArrayList<>();

    public ConfigurationParser(String filename) {
        parse(filename);
    }

    public int getNumberOfFloors() {
        return numberOfFloors;
    }

    public ArrayList<ArrayList<String>> getFloorLayout() {
        return floorLayout;
    }

    /* parse
    Inputs: filename – path to the configuration file (.hconfig format).
    Outputs: none (fills numberOfFloors and floorLayout fields).
    Description: Reads the hotel layout config file, extracts number of floors and room types for each row. */
    private void parse(String filename) {
        try (Scanner scanner = new Scanner(new File(filename))) {
            if (!scanner.hasNextLine()) { throw new IllegalArgumentException("Missing number of floors."); } // File must have at least one line

            String firstLine = scanner.nextLine().trim(); // Read first line
            try {
                numberOfFloors = Integer.parseInt(firstLine); // Parse number of floors
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid number of floors."); // Must be a number
            }

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim(); // Read next line
                if (line.isEmpty()) continue; // Skip empty lines

                String[] tokens = line.split(","); // Split line into room codes
                ArrayList<String> row = new ArrayList<>();

                for (String token : tokens) {
                    token = token.trim();
                    if (!token.matches("[EBLZ]")) { // Validate room code
                        throw new IllegalArgumentException("Invalid room code: " + token);
                    }
                    row.add(token); // Add valid code to row
                }

                floorLayout.add(row); // Add parsed row to layout
            }

            if (floorLayout.isEmpty()) { throw new IllegalArgumentException("Missing floor layout."); } // At least one row required

        } catch (IOException e) {
            System.err.println("Error reading .hconfig: " + e.getMessage()); // Log error if file cannot be read
        }
    }

}

