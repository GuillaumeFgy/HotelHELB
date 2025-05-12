package com.example;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ReservationParserTest {

    private Path tempFile;

    /* setUp
    Description: Creates a temporary CSV file for testing reservations. */
    @BeforeEach
    public void setUp() throws Exception {
        tempFile = Files.createTempFile("reservation_test_", ".csv"); // Create a temporary file for testing
    }

    /* testParseFileWithOneValidReservation
    Description: Tests that a valid reservation line is correctly parsed into a Reservation object. */
    @Test
    public void testParseFileWithOneValidReservation() throws Exception {
        writeLine("Alice,Dupont,2,fumeur,tourisme,1"); // Write a valid reservation line

        List<Reservation> result = ReservationParser.parseFile(tempFile.toString()); // Parse the file

        assertEquals(1, result.size(), "Should parse one valid reservation"); // Ensure one reservation is parsed
        assertEquals("Alice", result.get(0).getFirstName(), "First name should be Alice"); // Check first name
    }

    /* testParseFileWithInvalidReservation
    Description: Tests that an invalid reservation line (e.g., missing name or invalid counts) is not parsed. */
    @Test
    public void testParseFileWithInvalidReservation() throws Exception {
        writeLine("Bob,,5,fumeur,tourisme,5"); // Write an invalid reservation line (missing last name, invalid numbers)

        List<Reservation> result = ReservationParser.parseFile(tempFile.toString()); // Parse the file

        assertEquals(0, result.size(), "No valid reservation should be parsed"); // Ensure no valid reservations are parsed
    }

    /* writeLine
    Inputs: line – a string to write to the temporary CSV file.
    Outputs: none.
    Description: Helper method to write a line to the temporary file for testing. */
    private void writeLine(String line) throws Exception {
        try (PrintWriter writer = new PrintWriter(new FileWriter(tempFile.toFile()))) {
            writer.println(line); // Write the line to the temporary file
        }
    }

}
