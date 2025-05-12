package com.example;

import java.io.File;
import java.util.Objects;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ReservationView {

    private static final int POPUP_WIDTH = 300;
    private static final int POPUP_HEIGHT = 300;

    private static final int DEFAULT_SPACING = 10;
    private static final int SECTION_SPACING = 20;
    private static final int STAR_SIZE = 40;

    private static final String BUTTON_CONFIRM_TEXT = "Confirm";
    private static final String LABEL_ASSIGN_ROOM = "Assign a new room:";
    private static final String LABEL_RELEASE_ROOM = "Release room";
    private static final String ALERT_CONFIRM_RELEASE = "Are you sure you want to release the room?";
    private static final String POPUP_TITLE_RESERVATION = "Reservation Details";
    private static final String POPUP_TITLE_RATING = "Rate Your Stay";
    private static final String LABEL_THANK_YOU = "Thank you for your stay! Click to rate:";



    private static final String IMAGE_PATH = "file:src/main/java/com/example/Images/";


    /* show
    Inputs: request – reservation to display; hotel – hotel model; view – UI view; controller – main controller; fromRoomButton – whether popup is from room click.
    Outputs: none.
    Description: Displays a popup to manage a reservation; allows release or reassignment depending on origin. */
    public static void show(AssignmentRequest request, Hotel hotel, HotelView view, HotelController controller, boolean fromRoomButton) {
        Reservation res = request.reservation; // Get reservation
        VBox box = createReservationInfoBox(res); // Create info box

        Scene scene = new Scene(box, POPUP_WIDTH, POPUP_HEIGHT); // Setup popup
        Stage detailStage = createModalStage(POPUP_TITLE_RESERVATION, scene);

        if (fromRoomButton) {
            handleRoomRelease(hotel, view, controller, request, detailStage); // Add release option
        } else {
            handleRoomReassignment(hotel, view, controller, request, box, detailStage); // Add reassignment option
        }

        detailStage.showAndWait(); // Show dialog
    }

    /* handleRoomRelease
    Inputs: hotel, view, controller – app components; request – reservation to release; detailStage – popup stage.
    Outputs: none.
    Description: Adds a release button to the popup that frees the room and triggers a rating prompt. */
    private static void handleRoomRelease(Hotel hotel, HotelView view, HotelController controller, AssignmentRequest request, Stage detailStage) {
        Room assignedRoom = request.room;

        Button releaseButton = new Button(LABEL_RELEASE_ROOM); // Create release button
        releaseButton.setOnAction(event -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, ALERT_CONFIRM_RELEASE, ButtonType.YES, ButtonType.NO); // Confirm dialog
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    freeReservation(hotel, view, controller, request); // Free room
                    detailStage.hide(); // Hide popup

                    Platform.runLater(() -> {
                        showRatingPopup(hotel, view, controller, assignedRoom, request); // Trigger rating flow
                        detailStage.close(); // Close stage
                    });
                }
            });
        });

        VBox box = (VBox) detailStage.getScene().getRoot(); // Get root container
        box.getChildren().add(releaseButton); // Add button
    }

    /* handleRoomReassignment
    Inputs: hotel, view, controller – app components; request – reservation to reassign; box – popup content; detailStage – popup stage.
    Outputs: none.
    Description: Adds a form to allow users to reassign the reservation to another available room. */
    private static void handleRoomReassignment(Hotel hotel, HotelView view, HotelController controller, AssignmentRequest request, VBox box, Stage detailStage) {
        Reservation res = request.reservation;
        Room assignedRoom = request.room;

        TextField roomInput = new TextField(assignedRoom.getName()); // Input for new room
        roomInput.setPromptText("Enter new room name");

        Button confirmButton = new Button(BUTTON_CONFIRM_TEXT);
        confirmButton.setDisable(hotel.getRoom(roomInput.getText()).isReserved()); // Disable if already reserved

        roomInput.textProperty().addListener((obs, oldText, newText) -> {
            try {
                Room testRoom = hotel.getRoom(newText);
                confirmButton.setDisable(testRoom.isReserved()); // Enable only if room is available
            } catch (Exception ex) {
                confirmButton.setDisable(true); // Invalid room name
            }
        });

        confirmButton.setOnAction(e -> {
            String newRoomName = roomInput.getText();
            Room newRoom = hotel.getRoom(newRoomName);

            freeReservation(hotel, view, controller, request); // Free current
            hotel.reserveRoom(new AssignmentRequest(res, newRoom)); // Assign new
            view.reserveRoom(newRoom.getName()); // Update UI

            detailStage.close(); // Close popup
        });

        box.getChildren().addAll(new Label(LABEL_ASSIGN_ROOM), roomInput, confirmButton); // Add form to popup
    }

    /* createModalStage
    Inputs: title – window title; scene – content to show.
    Outputs: configured Stage.
    Description: Creates a modal popup window with given scene and title. */
    private static Stage createModalStage(String title, Scene scene) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(title); 
        stage.setScene(scene); 
        return stage;
    }


    /* createReservationInfoBox
    Inputs: res – the reservation to display.
    Outputs: VBox – container with formatted reservation details.
    Description: Builds a vertical layout showing the reservation information (name, persons, children, smoker, purpose). */
    private static VBox createReservationInfoBox(Reservation res) {
        Label nameLabel = new Label("Client: " + res.getFirstName() + " " + res.getLastName());
        Label personsLabel = new Label("Persons: " + res.getNumPersons());
        Label childrenLabel = new Label("Children: " + res.getNumChildren());
        Label smokerLabel = new Label("Smoker: " + (res.isSmoker() ? "Yes" : "No"));
        Label purposeLabel = new Label("Stay Purpose: " + res.getStayPurpose().name());

        VBox box = new VBox(DEFAULT_SPACING, nameLabel, personsLabel, childrenLabel, smokerLabel, purposeLabel);
        box.setPadding(new Insets(SECTION_SPACING));
        return box;
    }

    /* freeReservation
    Inputs: hotel, view, controller – system components; request – reservation to cancel.
    Outputs: none.
    Description: Frees a room, updates the view, and removes the reservation from the system. */
    private static void freeReservation(Hotel hotel, HotelView view, HotelController controller, AssignmentRequest request) {
        Room room = request.room;
        hotel.freeRoom(room.getName());
        view.freeRoom(room.getName(), room.getType());
        controller.removeReservation(request);
    }

    /* showRatingPopup
    Inputs: hotel, view, controller – system components; room – the room to rate; request – previous reservation.
    Outputs: none.
    Description: Displays a popup for rating the stay, which may trigger a ticket game. */
    private static void showRatingPopup(Hotel hotel, HotelView view, HotelController controller, Room room, AssignmentRequest request) {
        Label instruction = createRatingInstructionLabel(); // "Thank you" message
        HBox starsBox = createStarsBox(); // Row of stars
        VBox layout = createRatingLayout(instruction, starsBox); // Vertical layout with spacing

        Scene scene = new Scene(layout, POPUP_WIDTH, POPUP_HEIGHT);
        Stage ratingStage = createModalStage(POPUP_TITLE_RATING, scene); // Popup window

        setupStarButtons(starsBox, room, ratingStage); // Enable star interactions
        ratingStage.showAndWait(); // Display
    }

    /* createRatingInstructionLabel
    Inputs: none.
    Outputs: Label – formatted thank you message.
    Description: Returns a label prompting the user to rate their stay. */
    private static Label createRatingInstructionLabel() {
        Label instruction = new Label(LABEL_THANK_YOU);
        instruction.setStyle("-fx-font-size: 16px;");
        return instruction;
    }

    /* createStarsBox
    Inputs: none.
    Outputs: HBox – horizontal container for stars.
    Description: Creates an empty container to host clickable star buttons. */
    private static HBox createStarsBox() {
        HBox starsBox = new HBox(DEFAULT_SPACING);
        starsBox.setPadding(new Insets(DEFAULT_SPACING));
        starsBox.setAlignment(Pos.CENTER);
        return starsBox;
    }

    /* createRatingLayout
    Inputs: instruction – rating message; starsBox – container of star buttons.
    Outputs: VBox – full layout for rating popup.
    Description: Builds the vertical layout combining text and star buttons. */
    private static VBox createRatingLayout(Label instruction, HBox starsBox) {
        VBox layout = new VBox(SECTION_SPACING, instruction, starsBox);
        layout.setPadding(new Insets(SECTION_SPACING));
        layout.setAlignment(Pos.CENTER);
        return layout;
    }

    /* setupStarButtons
    Inputs: starsBox – where to place the star buttons; room – rated room; ratingStage – popup window.
    Outputs: none.
    Description: Adds 5 clickable stars to the box. Once clicked, they trigger ticket creation and launch the game view. */
    private static void setupStarButtons(HBox starsBox, Room room, Stage ratingStage) {
        Image emptyStar = new Image(IMAGE_PATH + "star_empty.png");
        Image filledStar = new Image(IMAGE_PATH + "star_filled.png");
        Button[] stars = new Button[5];

        for (int i = 0; i < 5; i++) {
            ImageView imageView = new ImageView(emptyStar);
            imageView.setFitWidth(STAR_SIZE);
            imageView.setFitHeight(STAR_SIZE);

            Button star = new Button();
            star.setGraphic(imageView);
            star.setStyle("-fx-background-color: transparent; -fx-padding: 0;");
            final int index = i;

            star.setOnAction(e -> {
                for (int j = 0; j < 5; j++) {
                    ImageView iv = (ImageView) stars[j].getGraphic();
                    iv.setImage(j <= index ? filledStar : emptyStar); // Update stars visually
                }

                int rating = index + 1;
                Ticket ticket = TicketFactory.createTicket(room, rating); // Generate ticket
                ratingStage.hide(); // Close rating popup

                Platform.runLater(() -> {
                    TicketGameView.show(ticket); // Launch game
                    ratingStage.close();
                });
            });

            stars[i] = star;
            starsBox.getChildren().add(star); // Add star to row
        }
    }


}
