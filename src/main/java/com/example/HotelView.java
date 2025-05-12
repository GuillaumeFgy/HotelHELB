package com.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class HotelView implements HotelObserver {

    private Stage stage;
    private Scene scene;

    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 800;

    private static final int POPUP_WIDTH = (int)(WINDOW_WIDTH * 0.375);
    private static final int POPUP_HEIGHT = (int)(WINDOW_HEIGHT * 0.25);

    private final Map<String, Button> roomButtonsMap;
    private ComboBox<String> floorSelector;
    private VBox reservationList;
    private ComboBox<String> strategySelector;
    private ComboBox<String> sortSelector;
    private Button verifyCodeButton = new Button("Verify Code");



    private final int roomsSize = (int)(WINDOW_WIDTH * 0.1); // 10% width
    private final int roomSpacing = (int)(WINDOW_WIDTH * 0.025); // 2.5% width

    private final String economicStyle = "-fx-background-color:rgb(255, 174, 68);";
    private final String businessStyle = "-fx-background-color:rgb(139, 224, 253);";
    private final String luxuryStyle = "-fx-background-color:rgb(201, 101, 255);";
    private final String defaultStyle = "-fx-background-color: #d3d3d3;";
    private final String reservedStyle = "-fx-background-color: #ff0000; ";

    private Stage verificationStage;
    private TextField codeInputField;
    private Label resultLabel;
    private Button verifyButton;


    public HotelView(Stage stage){
        this.stage = stage;
        this.roomButtonsMap = new HashMap<String, Button>();
    }

   
    /* initView
    Inputs: hotel – the Hotel model; strategies – map of available assignment strategies.
    Outputs: none.
    Description: Initializes the full GUI layout with floor view, strategy selector, reservation list, and scrollable panel. */
    public void initView(Hotel hotel, Map<String, AssignmentStrategy> strategies) {
        prepareRoomButtonsMap(hotel); // Preload all room buttons

        double topPadding = WINDOW_HEIGHT * 0.2; // Space at top for layout

        HBox mainBox = new HBox(); // Root layout
        VBox leftPanel = new VBox(roomSpacing); // Left: floors
        leftPanel.setPadding(new Insets(topPadding, 0.025 * WINDOW_WIDTH, 0.025 * WINDOW_HEIGHT, 0.025 * WINDOW_WIDTH));
        leftPanel.getChildren().add(displayColorCodes()); // Legend
        leftPanel.getChildren().add(createFloorSelector(hotel)); // Floor selector

        VBox rightPanel = new VBox(roomSpacing); // Right: reservations & controls
        rightPanel.setPadding(new Insets(topPadding, 0, 0, 0));
        strategySelector = new ComboBox<>();
        getStrategySelector().getItems().addAll(strategies.keySet()); // Populate strategy dropdown
        getStrategySelector().setValue("Random Assignment"); // Default value

        sortSelector = new ComboBox<>();
        sortSelector.getItems().addAll("Sort by : Name", "Sort by : Room");
        sortSelector.setValue("No sorting"); // Default value

        reservationList = new VBox(roomSpacing); // Reservation display area
        ScrollPane scrollPane = new ScrollPane(reservationList); // Scrollable panel
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(WINDOW_HEIGHT * 0.6);
        scrollPane.setPrefWidth(WINDOW_WIDTH * 0.375);

        rightPanel.getChildren().addAll(verifyCodeButton, strategySelector, sortSelector, scrollPane);
        mainBox.getChildren().addAll(leftPanel, rightPanel); // Combine panels

        scene = new Scene(mainBox, WINDOW_WIDTH, WINDOW_HEIGHT); // Final scene setup
        stage.setScene(scene);
        stage.show();
        updateFloorView(hotel, 0); // Display first floor
    }

    /* prepareRoomButtonsMap
    Inputs: hotel – the Hotel model.
    Outputs: none.
    Description: Creates and stores styled buttons for each room in a map, keyed by room name. */
    public void prepareRoomButtonsMap(Hotel hotel) {
        roomButtonsMap.clear(); // Reset map

        for (int floorIndex = 0; floorIndex < hotel.getNumberOfFloors(); floorIndex++) {
            Floor floor = hotel.getFloor(floorIndex + 1);
            for (Room room : floor.getRoomMap().values()) {
                Button roomButton = createStyledRoomButton(room); // Create button for room
                roomButtonsMap.put(room.getName(), roomButton); // Store it in the map
            }
        }
    }

    /* updateFloorView
    Inputs: hotel – the Hotel model; floorIndex – index of floor to display.
    Outputs: none.
    Description: Updates the visual grid of rooms shown for the selected floor. */
    public void updateFloorView(Hotel hotel, int floorIndex) {
        Floor floor = hotel.getFloor(floorIndex + 1); // Get current floor
        ArrayList<ArrayList<String>> layout = hotel.getFloorLayout(); // Room layout

        HBox root = (HBox) scene.getRoot();
        VBox leftPanel = (VBox) root.getChildren().get(0); // Floor view on left

        leftPanel.getChildren().removeIf(node -> "roomRow".equals(node.getId())); // Remove old rows

        for (int rowIndex = 0; rowIndex < layout.size(); rowIndex++) {
            HBox rowBox = new HBox(roomSpacing);
            rowBox.setId("roomRow"); // Tag for future removal

            for (int colIndex = 0; colIndex < layout.get(rowIndex).size(); colIndex++) {
                String cell = layout.get(rowIndex).get(colIndex);
                if (!cell.equals("Z")) {
                    Room room = floor.getRoomAt(rowIndex, colIndex);
                    Button roomButton = roomButtonsMap.get(room.getName()); // Get matching button
                    rowBox.getChildren().add(roomButton); // Add room button
                } else {
                    rowBox.getChildren().add(createSpacer()); // Add empty space
                }
            }

            leftPanel.getChildren().add(rowBox); // Add row to floor view
        }
    }


    /* setSquareSize
    Inputs: node – UI component; size – target size for width and height.
    Outputs: none.
    Description: Sets both the width and height of a Region to create a square. */
    private void setSquareSize(Region node, double size) {
        node.setPrefWidth(size); // Set width
        node.setPrefHeight(size); // Set height
    }

    /* displayColorCodes
    Inputs: none.
    Outputs: an HBox containing visual legends for room types.
    Description: Creates and returns a horizontal box showing the color codes for each room type. */
    private HBox displayColorCodes() {
        HBox legendBox = new HBox(roomSpacing); // Horizontal container for color legend
        legendBox.setPadding(new Insets(WINDOW_HEIGHT * 0.025)); // Add spacing

        legendBox.getChildren().addAll(
            createLabel("Luxury", luxuryStyle),   // Add luxury legend
            createLabel("Business", businessStyle), // Add business legend
            createLabel("Economic", economicStyle)  // Add economic legend
        );

        return legendBox;
    }

    /* createLabel
    Inputs: labelName – name text to show; style – background style for color block.
    Outputs: HBox containing the label and corresponding color sample.
    Description: Builds a labeled color indicator for a room type legend. */
    private HBox createLabel(String labelName, String style) {
        Button label = new Button(labelName); // Label as button
        label.setDisable(true); // Disable interaction
        label.setStyle("-fx-font-weight: bold; -fx-opacity: 1.0;"); // Make label stand out
        Region color = createColorSample(style); // Color block

        HBox container = new HBox(); // Horizontal container
        container.getChildren().addAll(label, color); // Add both to layout
        return container;
    }

    /* createColorSample
    Inputs: style – CSS background style.
    Outputs: a styled square Region.
    Description: Creates a small square block with a given style for color legends. */
    private Region createColorSample(String style) {
        Region sample = new Region(); // Simple empty region
        setSquareSize(sample, roomsSize * 0.25); // Set size
        sample.setStyle(style + " -fx-border-color: black;"); // Add border and background
        return sample;
    }

    /* createStyledRoomButton
    Inputs: room – the Room model to create a button for.
    Outputs: a Button with proper name, size and style.
    Description: Creates a visual button for a room with its name and color style. */
    private Button createStyledRoomButton(Room room) {
        Button button = new Button(room.getName()); // Label with room name
        setSquareSize(button, roomsSize); // Make it square
        button.setStyle(buildRoomButtonStyle(room.getType())); // Style based on type
        return button;
    }

    /* createSpacer
    Inputs: none.
    Outputs: an invisible square region.
    Description: Creates a transparent placeholder in the grid for non-room cells. */
    private Region createSpacer() {
        Region spacer = new Region(); // Empty region
        setSquareSize(spacer, roomsSize); // Make it square
        spacer.setStyle("-fx-background-color: transparent;"); // Transparent background
        return spacer;
    }

    /* buildRoomButtonStyle
    Inputs: roomType – the character representing room type ('L', 'B', 'E').
    Outputs: a full CSS style string.
    Description: Builds the CSS string for a room button based on its type. */
    private String buildRoomButtonStyle(char roomType) {
        return "-fx-font-weight: bold; -fx-border-color: black; -fx-border-width: 1px; " + getRoomColorStyle(roomType); // Full style
    }

    public Button getButton(String roomName) {
        return roomButtonsMap.get(roomName);
    }


    /* reserveRoom
    Inputs: roomName – name of the room to mark as reserved.
    Outputs: none.
    Description: Updates the UI button style for the specified room to indicate it's reserved. */
    public void reserveRoom(String roomName) {
        Button roomButton = roomButtonsMap.get(roomName); // Get button
        roomButton.setStyle(reservedStyle); // Apply reserved style
    }

    /* freeRoom
    Inputs: roomName – name of the room to release; type – room type ('L', 'B', 'E').
    Outputs: none.
    Description: Restores the button style of the room after it has been freed. */
    public void freeRoom(String roomName, char type) {
        Button roomButton = roomButtonsMap.get(roomName); // Get button
        roomButton.setStyle(buildRoomButtonStyle(type)); // Re-apply type-specific style
    }

    /* createFloorSelector
    Inputs: hotel – hotel model to read floor count from.
    Outputs: ComboBox with floor labels.
    Description: Builds a dropdown for switching between hotel floors. */
    private ComboBox<String> createFloorSelector(Hotel hotel) {
        floorSelector = new ComboBox<>();
        for (int i = 0; i < hotel.getNumberOfFloors(); i++) {
            String floorLabel = "Floor : " + Hotel.getLetterFromNumber(i); // Format label
            floorSelector.getItems().add(floorLabel); // Add to dropdown
        }
        floorSelector.setValue(floorSelector.getItems().get(0)); // Set default selection
        return floorSelector;
    }

    /* createRefreshButton
    Inputs: none.
    Outputs: a Button with a refresh icon.
    Description: Creates a button used to reassign a reservation to a new room. */
    public Button createRefreshButton() {
        return new Button("↻"); // Unicode refresh symbol
    }

    /* createReservationEntry
    Inputs: clientLabel – short client name; roomName – assigned room; colorStyle – room type style; refreshButton – button to reassign.
    Outputs: an HBox representing a reservation entry.
    Description: Builds the visual component for a single reservation line. */
    public HBox createReservationEntry(String clientLabel, String roomName, String colorStyle, Button refreshButton) {
        Label nameLabel = new Label(clientLabel); // Client name
        Label roomLabel = new Label(roomName); // Room name
        roomLabel.setStyle(colorStyle + " -fx-padding: 5 10; -fx-font-weight: bold;"); // Room type color

        HBox box = new HBox(roomSpacing * 0.5); // Horizontal container
        box.setPadding(new Insets(roomSpacing * 0.5));
        box.getChildren().addAll(nameLabel, roomLabel, refreshButton); // Add components
        box.setUserData(roomName); // Store room reference for logic
        return box;
    }

    /* showReservations
    Inputs: assignments – list of current reservation-room assignments.
    Outputs: none.
    Description: Clears and repopulates the right panel with all reservations. */
    public void showReservations(List<AssignmentRequest> assignments) {
        reservationList.getChildren().clear(); // Remove old entries
        for (AssignmentRequest request : assignments) {
            showReservation(request.reservation, request.room); // Add each entry
        }
    }

    /* showReservation
    Inputs: res – Reservation object; assignedRoom – room currently assigned.
    Outputs: the HBox created and displayed for the reservation.
    Description: Creates and displays a reservation entry in the list. */
    public HBox showReservation(Reservation res, Room assignedRoom) {
        String clientName = res.getFirstName().charAt(0) + ". " + res.getLastName(); // Short name
        String roomName = assignedRoom.getName(); // Room label
        String colorStyle = getRoomColorStyle(assignedRoom.getType()); // Get type color
        Button refreshButton = createRefreshButton(); // Add refresh option
        HBox reservationEntry = createReservationEntry(clientName, roomName, colorStyle, refreshButton);
        reservationList.getChildren().add(reservationEntry); // Add to UI
        return reservationEntry;
    }

    /* getRoomColorStyle
    Inputs: roomType – character representing room type.
    Outputs: CSS style string.
    Description: Returns the color style associated with the room type. */
    private String getRoomColorStyle(char roomType) {
        switch (roomType) {
            case 'L': return luxuryStyle;
            case 'B': return businessStyle;
            case 'E': return economicStyle;
            default: return defaultStyle;
        }
    }

    /* createCodeVerificationPopup
    Inputs: none.
    Outputs: none.
    Description: Opens a popup for users to enter and validate a discount code. */
    public void createCodeVerificationPopup() {
        verificationStage = new Stage();
        verificationStage.initModality(Modality.APPLICATION_MODAL);
        verificationStage.setTitle("Code Verification");

        Label infoLabel = new Label("A valid code must be 10 characters long."); // Instruction
        codeInputField = new TextField();
        codeInputField.setPromptText("Enter discount code"); // Placeholder

        resultLabel = new Label();
        verifyButton = new Button("Verify");

        VBox layout = new VBox(roomSpacing * 0.5, infoLabel, codeInputField, verifyButton, resultLabel);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, POPUP_WIDTH, POPUP_HEIGHT); // Create scene
        verificationStage.setScene(scene);
        verificationStage.show(); // Show popup
    }


    public ComboBox<String> getFloorSelector() { return floorSelector; }
    public VBox getReservationList() { return reservationList; }
    public ComboBox<String> getStrategySelector() { return strategySelector; }
    public ComboBox<String> getSortSelector() { return sortSelector; }
    public Button getVerifyCodeButton() { return verifyCodeButton; }
    public Button getVerifyButton() { return verifyButton; }
    public TextField getCodeInputField() { return codeInputField; }
    public void showVerificationResult(String text) { resultLabel.setText(text); }
    public void closeVerificationPopup() { verificationStage.close(); }
   
}
