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

   
    public void initView(Hotel hotel, Map<String, AssignmentStrategy> strategies) {
        prepareRoomButtonsMap(hotel); // ajout ici
    
        double topPadding = WINDOW_HEIGHT * 0.2;

        HBox mainBox = new HBox();
        VBox leftPanel = new VBox(roomSpacing);
        leftPanel.setPadding(new Insets(topPadding, 0.025 * WINDOW_WIDTH, 0.025 * WINDOW_HEIGHT, 0.025 * WINDOW_WIDTH));
        leftPanel.getChildren().add(displayColorCodes());
        leftPanel.getChildren().add(createFloorSelector(hotel));
    
        VBox rightPanel = new VBox(roomSpacing);
        rightPanel.setPadding(new Insets(topPadding, 0, 0, 0));
        strategySelector = new ComboBox<>();
        getStrategySelector().getItems().addAll(strategies.keySet());
        getStrategySelector().setValue("Random Assignment");

        sortSelector = new ComboBox<>();
        sortSelector.getItems().addAll("Sort by : Name");
        sortSelector.getItems().addAll("Sort by : Room");
        sortSelector.setValue("No sorting");
    
        reservationList = new VBox(roomSpacing);
        ScrollPane scrollPane = new ScrollPane(reservationList);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(WINDOW_HEIGHT * 0.6);
        scrollPane.setPrefWidth(WINDOW_WIDTH * 0.375);


        rightPanel.getChildren().addAll(verifyCodeButton, strategySelector, sortSelector, scrollPane);

    
        mainBox.getChildren().addAll(leftPanel, rightPanel);
        scene = new Scene(mainBox, WINDOW_WIDTH, WINDOW_HEIGHT);
    
        stage.setScene(scene);
        stage.show();
        updateFloorView(hotel, 0);
    }

    public void prepareRoomButtonsMap(Hotel hotel) {
        roomButtonsMap.clear();
    
        for (int floorIndex = 0; floorIndex < hotel.getNumberOfFloors(); floorIndex++) {
            Floor floor = hotel.getFloor(floorIndex + 1);
            for (Room room : floor.getRoomMap().values()) {
                Button roomButton = createStyledRoomButton(room);
                roomButtonsMap.put(room.getName(), roomButton);
            }
        }
    }
    

    public void updateFloorView(Hotel hotel, int floorIndex) {
        Floor floor = hotel.getFloor(floorIndex + 1);
        ArrayList<ArrayList<String>> layout = hotel.getFloorLayout();
    
        HBox root = (HBox) scene.getRoot();
        VBox leftPanel = (VBox) root.getChildren().get(0); // left = floor view
    
        leftPanel.getChildren().removeIf(node -> "roomRow".equals(node.getId()));
    
        for (int rowIndex = 0; rowIndex < layout.size(); rowIndex++) {
            HBox rowBox = new HBox(roomSpacing);
            rowBox.setId("roomRow");
    
            for (int colIndex = 0; colIndex < layout.get(rowIndex).size(); colIndex++) {
                String cell = layout.get(rowIndex).get(colIndex);
                if (!cell.equals("Z")) {
                    Room room = floor.getRoomAt(rowIndex, colIndex);
                    Button roomButton = roomButtonsMap.get(room.getName());
                    rowBox.getChildren().add(roomButton);
                } else {
                    rowBox.getChildren().add(createSpacer());
                }
            }
    
            leftPanel.getChildren().add(rowBox);
        }
    }

    private void setSquareSize(Region node, double size) {
        node.setPrefWidth(size);
        node.setPrefHeight(size);
    }

    private HBox displayColorCodes() {
        HBox legendBox = new HBox(roomSpacing);
        legendBox.setPadding(new Insets(WINDOW_HEIGHT * 0.025));
    
        legendBox.getChildren().addAll(
            createLabel("Luxury", luxuryStyle),
            createLabel("Business", businessStyle),
            createLabel("Economic", economicStyle)
        );
    
        return legendBox;
    }
    

    private HBox createLabel(String labelName, String style) {
        Button label = new Button(labelName);
        label.setDisable(true);
        label.setStyle("-fx-font-weight: bold; -fx-opacity: 1.0;");
        Region color = createColorSample(style);
    
        HBox container = new HBox();
        container.getChildren().addAll(label, color);
        return container;
    }
    
    
    private Region createColorSample(String style) {
        Region sample = new Region();
        setSquareSize(sample, roomsSize * 0.25);
        sample.setStyle(style + " -fx-border-color: black;");
        return sample;
    }
    

    private Button createStyledRoomButton(Room room) {
        Button button = new Button(room.getName());
        setSquareSize(button, roomsSize);
        button.setStyle(buildRoomButtonStyle(room.getType()));
        return button;
    }

    private Region createSpacer() {
        Region spacer = new Region();
        setSquareSize(spacer, roomsSize);
        spacer.setStyle("-fx-background-color: transparent;");
        return spacer;
    }

    private String buildRoomButtonStyle(char roomType) {
        return "-fx-font-weight: bold; -fx-border-color: black; -fx-border-width: 1px; " + getRoomColorStyle(roomType);
    }

    
    public Button getButton(String roomName)
    {
        return roomButtonsMap.get(roomName);
    }

    public void reserveRoom(String roomName)
    {
        Button roomButton = roomButtonsMap.get(roomName);
        roomButton.setStyle(reservedStyle);
    }

    public void freeRoom(String roomName, char type)
    {
        Button roomButton = roomButtonsMap.get(roomName);
        roomButton.setStyle(buildRoomButtonStyle(type));
    }

    private ComboBox<String> createFloorSelector(Hotel hotel) {
        floorSelector = new ComboBox<>();
    
        for (int i = 0; i < hotel.getNumberOfFloors(); i++) {
            String floorLabel = "Floor : " + Hotel.getLetterFromNumber(i);
            floorSelector.getItems().add(floorLabel);
        }
    
        floorSelector.setValue(floorSelector.getItems().get(0)); // sélection par défaut
        return floorSelector;
    }

    public Button createRefreshButton() {
        return new Button("↻");
    }

    public HBox createReservationEntry(String clientLabel, String roomName, String colorStyle, Button refreshButton) {
        Label nameLabel = new Label(clientLabel);
        Label roomLabel = new Label(roomName);
        roomLabel.setStyle(colorStyle + " -fx-padding: 5 10; -fx-font-weight: bold;");
    
        HBox box = new HBox(roomSpacing * 0.5);
        box.setPadding(new Insets(roomSpacing * 0.5));
        box.getChildren().addAll(nameLabel, roomLabel, refreshButton);
        box.setUserData(roomName);
        return box;
    }
    

    public void showReservations(List<AssignmentRequest> assignments) {
        reservationList.getChildren().clear();
    
        for (AssignmentRequest request : assignments) {
            showReservation(request.reservation, request.room);
        }
    }

    public HBox showReservation(Reservation res, Room assignedRoom)
    {
        String clientName = res.getFirstName().charAt(0) + ". " + res.getLastName();
        String roomName = assignedRoom.getName();
        String colorStyle = getRoomColorStyle(assignedRoom.getType());
        Button refreshButton = createRefreshButton(); 
        HBox reservationEntry = createReservationEntry(clientName, roomName, colorStyle, refreshButton);
        reservationList.getChildren().add(reservationEntry);
        return reservationEntry;
    }
    
    
    private String getRoomColorStyle(char roomType) {
        switch (roomType) {
            case 'L': return luxuryStyle;
            case 'B': return businessStyle;
            case 'E': return economicStyle;
            default: return defaultStyle;
        }
    }

    public void createCodeVerificationPopup() {
        verificationStage = new Stage();
        verificationStage.initModality(Modality.APPLICATION_MODAL);
        verificationStage.setTitle("Code Verification");

        Label infoLabel = new Label("A valid code must be 10 characters long.");
        codeInputField = new TextField();
        codeInputField.setPromptText("Enter discount code");

        resultLabel = new Label();
        verifyButton = new Button("Verify");

        VBox layout = new VBox(roomSpacing * 0.5, infoLabel, codeInputField, verifyButton, resultLabel);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, POPUP_WIDTH, POPUP_HEIGHT);
        verificationStage.setScene(scene);
        verificationStage.show();
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
