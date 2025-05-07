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

    private void parse(String filename) {
        try (Scanner scanner = new Scanner(new File(filename))) {
            if (!scanner.hasNextLine()) {
                throw new IllegalArgumentException("Missing number of floors.");
            }

            // First line = number of floors
            String firstLine = scanner.nextLine().trim();
            try {
                numberOfFloors = Integer.parseInt(firstLine);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid number of floors.");
            }

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;

                String[] tokens = line.split(",");
                ArrayList<String> row = new ArrayList<>();

                for (String token : tokens) {
                    token = token.trim();
                    if (!token.matches("[EBLZ]")) {
                        throw new IllegalArgumentException("Invalid room code: " + token);
                    }
                    row.add(token);
                }

                floorLayout.add(row);
            }

            if (floorLayout.isEmpty()) {
                throw new IllegalArgumentException("Missing floor layout.");
            }

            // Optional: check that all rows have same length
            int expectedCols = floorLayout.get(0).size();
            for (ArrayList<String> row : floorLayout) {
                if (row.size() != expectedCols) {
                    throw new IllegalArgumentException("Inconsistent row length in floor layout.");
                }
            }

        } catch (IOException e) {
            System.err.println("Error reading .hconfig: " + e.getMessage());
        }
    }
}

