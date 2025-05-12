package com.example;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class HotelController {

    private Hotel hotel;
    private HotelView view;
    private final String configurationFile = "src/main/java/com/example/configuration.hconfig";
    private final String reservationFile = "src/main/java/com/example/reservation.csv";
    private List<Reservation> reservations;
    private List<AssignmentRequest> assignments = new ArrayList<AssignmentRequest>();
    private final Map<String, AssignmentStrategy> strategies = new LinkedHashMap<>();

    private static final String SORT_BY_NAME = "Sort by : Name";
    private static final String SORT_BY_ROOM = "Sort by : Room";
    private static final int DISCOUNT_CODE_LENGTH = 10;
    private static final int POLL_INTERVAL_SECONDS = 2;

    /* HotelController constructor
    Inputs: view – the HotelView instance used for the UI.
    Outputs: none.
    Description: Initializes the controller, parses config, creates hotel and strategies, sets up UI and starts simulation. */
    public HotelController(HotelView view) {
        this.view = view; // Store reference to view
        ConfigurationParser configurationParser = new ConfigurationParser(configurationFile); // Load config
        hotel = new Hotel(configurationParser.getNumberOfFloors(), configurationParser.getFloorLayout(), this.view); // Create hotel
        initStrategies(); // Load available strategies
        view.initView(hotel, strategies); // Initialize UI
        setActions(); // Bind event handlers
        startSimulation(); // Begin periodic polling
    }

    /* displayUI
    Inputs: none.
    Outputs: none.
    Description: Updates the view with current reservations and enables interaction on them. */
    private void displayUI() {
        view.showReservations(assignments); // Show reservations on screen
        addClickHandlersToReservations(); // Bind click behavior to each reservation row
    }

    /* setActions
    Inputs: none.
    Outputs: none.
    Description: Binds all necessary UI actions like selectors, refresh, and discount code verification popup. */
    private void setActions() {
        floorSelectionAction(); // Handle floor changes
        refreshButtonAction(); // Bind refresh buttons
        sortSelectorAction(); // Enable reservation sorting
        addClickHandlersToRoomButtons(); // Make room buttons interactive

        view.getVerifyCodeButton().setOnAction(e -> {
            view.createCodeVerificationPopup(); // Show popup
            view.getVerifyButton().setOnAction(ev -> {
                String code = view.getCodeInputField().getText().trim(); // Read user input
                if (code.length() != DISCOUNT_CODE_LENGTH) {
                    view.showVerificationResult("❌ Invalid code length."); // Too short/long
                    return;
                }

                try {
                    int discount = DiscountCodeGenerator.decodeDiscount(code); // Try decode
                    view.showVerificationResult("✅ Valid code! Discount: " + discount + "%"); // Show result
                } catch (IllegalArgumentException ex) {
                    view.showVerificationResult("❌ Invalid code."); // Failed to decode
                }
            });
        });
    }


    /* floorSelectionAction
    Inputs: none.
    Outputs: none.
    Description: Updates the floor view in the UI when a new floor is selected from the dropdown. */
    private void floorSelectionAction() {
        view.getFloorSelector().setOnAction(event -> {
            int selectedIndex = view.getFloorSelector().getSelectionModel().getSelectedIndex(); // Get selected floor index
            view.updateFloorView(hotel, selectedIndex); // Refresh UI with new floor
        });
    }

    /* refreshButtonAction
    Inputs: none.
    Outputs: none.
    Description: Binds reassign logic to each reservation's refresh button in the UI list. */
    private void refreshButtonAction() {
        forEachReservationEntry((roomName, hbox) -> {
            AssignmentRequest request = findRequestByRoomName(roomName); // Match UI row to reservation
            Node last = hbox.getChildren().get(hbox.getChildren().size() - 1); // Get the refresh button
            if (last instanceof Button) {
                ((Button) last).setOnAction(e -> reassignReservation(request)); // Reassign on click
            }
        });
    }

    /* addClickHandlersToReservations
    Inputs: none.
    Outputs: none.
    Description: Binds mouse click events to each reservation row to open its detail view. */
    private void addClickHandlersToReservations() {
        forEachReservationEntry((roomName, hbox) -> {
            AssignmentRequest request = findRequestByRoomName(roomName); // Retrieve reservation from room name
            if (request != null) {
                hbox.setOnMouseClicked(e -> ReservationView.show(request, hotel, view, this, false)); // Open detail popup
            }
        });
    }


    /* sortSelectorAction
    Inputs: none.
    Outputs: none.
    Description: Sorts the reservation list by client name or room based on the selected option, then updates the UI. */
    private void sortSelectorAction() {
        view.getSortSelector().setOnAction(event -> {
            String selected = view.getSortSelector().getValue(); // Get selected sort option

            switch (selected) {
                case SORT_BY_NAME:
                    assignments.sort((a1, a2) -> a1.reservation.getLastName().compareToIgnoreCase(a2.reservation.getLastName())); // Sort by last name
                    break;
                case SORT_BY_ROOM:
                    assignments.sort((a1, a2) -> {
                        String r1 = a1.room.getName();
                        String r2 = a2.room.getName();

                        char floor1 = r1.charAt(0); // Extract floor letters
                        char floor2 = r2.charAt(0);

                        int floorIndex1 = floor1 - 'A'; // Convert to index
                        int floorIndex2 = floor2 - 'A';

                        int num1 = extractRoomNumber(r1); // Extract room numbers
                        int num2 = extractRoomNumber(r2);

                        if (floorIndex1 != floorIndex2) {
                            return Integer.compare(floorIndex1, floorIndex2); // Sort by floor
                        } else {
                            return Integer.compare(num1, num2); // Then by room number
                        }
                    });
                    break;
                default:
                    break;
            }

            view.showReservations(assignments); // Update list display
            refreshButtonAction(); // Rebind refresh buttons
            addClickHandlersToReservations(); // Rebind row click handlers
        });
    }


    /* extractRoomNumber
    Inputs: roomName – formatted room name (e.g., A12L).
    Outputs: the numeric part of the room name as integer.
    Description: Extracts and parses the room number from the full room name. */
    private int extractRoomNumber(String roomName) {
        String digits = roomName.substring(1, roomName.length() - 1); // Remove first and last char
        return Integer.parseInt(digits); // Convert to integer
    }

    /* initStrategies
    Inputs: none.
    Outputs: none.
    Description: Populates the strategy map with all available assignment strategies. */
    private void initStrategies() {
        strategies.put("Random Assignment", new RandomAssignment());
        strategies.put("Quiet Zone", new QuietZoneAssignment(hotel));
        strategies.put("Stay Purpose", new StayPurposeAssignment());
        strategies.put("Sequential Assignment", new SequentialAssignment(hotel));
    }

    /* getSelectedStrategy
    Inputs: none.
    Outputs: the currently selected AssignmentStrategy.
    Description: Retrieves the strategy selected by the user from the UI. */
    private AssignmentStrategy getSelectedStrategy() {
        String selected = view.getStrategySelector().getValue(); // Get selected strategy label
        return strategies.get(selected); // Return corresponding strategy
    }

    /* assignReservation
    Inputs: reservation – the reservation to assign.
    Outputs: the created AssignmentRequest.
    Description: Uses the selected strategy to assign a room, reserves it, and stores the request. */
    public AssignmentRequest assignReservation(Reservation reservation) {
        AssignmentRequest request = getSelectedStrategy().createAssignmentRequest(hotel.getAvailableRooms(), reservation); // Create request
        hotel.reserveRoom(request); // Apply reservation
        assignments.add(request); // Track request
        return request;
    }

    /* reassignReservation
    Inputs: request – the reservation to reassign.
    Outputs: none.
    Description: Frees the current room, removes the request, and reassigns the reservation to a new available room. */
    public void reassignReservation(AssignmentRequest request) {
        int index = findRequestIndexByRoomName(request.room.getName()); // Find current index
        if (index != -1) {
            hotel.freeRoom(request.room.getName()); // Free room
            assignments.remove(index); // Remove current assignment
            if (!hotel.getAvailableRooms().isEmpty()) {
                AssignmentRequest newRequest = assignReservation(request.reservation); // Create new assignment
                assignments.remove(newRequest); // Remove duplicate
                assignments.add(index, newRequest); // Insert at original index
            }
        }
    }

    /* findRequestIndexByRoomName
    Inputs: roomName – the name of the room.
    Outputs: index of the assignment in the list; -1 if not found.
    Description: Searches the assignment list for a reservation matching the given room name. */
    private int findRequestIndexByRoomName(String roomName) {
        for (int i = 0; i < assignments.size(); i++) {
            if (assignments.get(i).room.getName().equals(roomName)) {
                return i; // Found the index
            }
        }
        return -1; // Not found
    }

    /* updateReservationList
    Inputs: none.
    Outputs: none.
    Description: Goes through all reservations and assigns them if rooms are available. */
    public void updateReservationList() {
        for (Reservation res : reservations) {
            System.err.println(res.getFirstName()); // Debug output
            if (hotel.getAvailableRooms().size() > 0) {
                assignReservation(res); // Assign if a room is available
            }
        }
    }

    /* findRequestByRoomName
    Inputs: roomName – the name of the room.
    Outputs: the AssignmentRequest for the room, or null if not found.
    Description: Retrieves an assignment request based on the room's name. */
    private AssignmentRequest findRequestByRoomName(String roomName) {
        for (AssignmentRequest request : assignments) {
            if (request.room.getName().equals(roomName)) {
                return request; // Match found
            }
        }
        return null; // No match
    }

    /* startSimulation
    Inputs: none.
    Outputs: none.
    Description: Starts a recurring timer that checks for new reservations and refreshes the UI. */
    private void startSimulation() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(POLL_INTERVAL_SECONDS), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                pollForNewReservations(); // Check for new data
                displayUI(); // Refresh UI
                refreshButtonAction(); // Rebind buttons
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE); // Loop forever
        timeline.play(); // Start timer
    }

    /* pollForNewReservations
    Inputs: none.
    Outputs: none.
    Description: Reads the reservation file and assigns any new reservations to available rooms. */
    private void pollForNewReservations() {
        List<Reservation> latest = ReservationParser.parseFile(reservationFile); // Parse file

        for (Reservation res : latest) {
            if (!hotel.getAvailableRooms().isEmpty()) {
                assignReservation(res); // Assign new reservation
            }
        }
    }


    /* removeReservation
    Inputs: request – the reservation to remove.
    Outputs: none.
    Description: Removes the reservation from the assignment list and from the UI reservation list. */
    public void removeReservation(AssignmentRequest request) {
        assignments.removeIf(r -> r.equals(request)); // Remove from data list

        VBox reservationList = view.getReservationList(); // Get UI reservation list
        reservationList.getChildren().removeIf(node -> {
            if (node instanceof HBox) {
                Object userData = node.getUserData(); // Check if the HBox corresponds to the room
                return userData instanceof String && userData.equals(request.room.getName());
            }
            return false;
        });
    }

    /* addClickHandlersToRoomButtons
    Inputs: none.
    Outputs: none.
    Description: Binds actions to each room button to open the reservation popup if the room is reserved. */
    private void addClickHandlersToRoomButtons() {
        for (Floor floor : hotel.getFloorMap().values()) {
            for (Room room : floor.getRoomMap().values()) {
                Button roomButton = view.getButton(room.getName()); // Get the button linked to this room
                roomButton.setOnAction(e -> {
                    if (room.isReserved()) {
                        Reservation res = room.getReservation(); // Get current reservation
                        AssignmentRequest request = new AssignmentRequest(res, room); // Create assignment wrapper
                        ReservationView.show(request, hotel, view, this, true); // Show popup
                    }
                });
            }
        }
    }

    /* ReservationEntryHandler
    Inputs: roomName – name of the room; hbox – corresponding HBox UI element.
    Outputs: none (interface method).
    Description: Functional interface to apply logic to each reservation entry in the UI. */
    private static interface ReservationEntryHandler {
        void handle(String roomName, HBox hbox); // Functional method to handle reservation entry
    }

    /* forEachReservationEntry
    Inputs: handler – function to apply to each reservation UI entry.
    Outputs: none.
    Description: Iterates through all reservation entries in the UI and applies the given handler logic. */
    private void forEachReservationEntry(ReservationEntryHandler handler) {
        VBox reservationList = view.getReservationList(); // Get reservation panel

        for (javafx.scene.Node node : reservationList.getChildren()) {
            if (node instanceof HBox) {
                HBox hbox = (HBox) node;
                Object userData = hbox.getUserData(); // Get associated room name
                if (userData instanceof String) {
                    handler.handle((String) userData, hbox); // Apply handler function
                }
            }
        }
    }


}


