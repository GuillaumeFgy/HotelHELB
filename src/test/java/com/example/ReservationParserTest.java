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

    @BeforeEach
    public void setUp() throws Exception {
        tempFile = Files.createTempFile("reservation_test_", ".csv");
    }

    @Test
    public void testParseFileWithOneValidReservation() throws Exception {
        writeLine("Alice,Dupont,2,fumeur,tourisme,1");

        List<Reservation> result = ReservationParser.parseFile(tempFile.toString());

        assertEquals(1, result.size(), "Should parse one valid reservation");
        assertEquals("Alice", result.get(0).getFirstName(), "First name should be Alice");
    }

    @Test
    public void testParseFileWithInvalidReservation() throws Exception {
        writeLine("Bob,,5,fumeur,tourisme,5"); // nom vide, 5 personnes, 5 enfants (invalide)

        List<Reservation> result = ReservationParser.parseFile(tempFile.toString());

        assertEquals(0, result.size(), "No valid reservation should be parsed");
    }

    private void writeLine(String line) throws Exception {
        try (PrintWriter writer = new PrintWriter(new FileWriter(tempFile.toFile()))) {
            writer.println(line);
        }
    }
}
