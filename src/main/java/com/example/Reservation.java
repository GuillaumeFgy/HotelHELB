package com.example;

public class Reservation {

    private final String firstName;
    private final String lastName;
    private final int numPersons;
    private final boolean smoker;
    private final StayPurpose stayPurpose;
    private final int numChildren;

    /* StayPurpose
    Description: Enum representing the reason for a reservation stay – tourism, business, or other. */
    public enum StayPurpose {
        TOURISM, BUSINESS, OTHER;

        /* fromString
        Inputs: value – raw string describing the purpose of the stay (in French).
        Outputs: corresponding StayPurpose enum value.
        Description: Converts a French string to the matching enum value (default is OTHER). */
        public static StayPurpose fromString(String value) {
            String cleaned = value.trim().toLowerCase(); // Normalize input
            switch (cleaned) {
                case "tourisme": return TOURISM;
                case "affaire": return BUSINESS;
                case "autre": return OTHER;
                default: return OTHER; // Default fallback
            }
        }
    }


    public Reservation(String firstName, String lastName, int numPersons, boolean smoker, StayPurpose stayPurpose, int numChildren) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.numPersons = numPersons;
        this.smoker = smoker;
        this.stayPurpose = stayPurpose;
        this.numChildren = numChildren;
    }

    // Getters
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public int getNumPersons() { return numPersons; }
    public boolean isSmoker() { return smoker; }
    public StayPurpose getStayPurpose() { return stayPurpose; }
    public int getNumChildren() { return numChildren; }
}
