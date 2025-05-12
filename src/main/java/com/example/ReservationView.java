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


    public static void show(AssignmentRequest request, Hotel hotel, HotelView view, HotelController controller, boolean fromRoomButton) {
        Reservation res = request.reservation;
        VBox box = createReservationInfoBox(res);

        Scene scene = new Scene(box, POPUP_WIDTH, POPUP_HEIGHT);
        Stage detailStage = createModalStage(POPUP_TITLE_RESERVATION, scene);

        if (fromRoomButton) {
            handleRoomRelease(hotel, view, controller, request, detailStage);
        } else {
            handleRoomReassignment(hotel, view, controller, request, box, detailStage);
        }

        detailStage.showAndWait();
    }

    private static void handleRoomRelease(Hotel hotel, HotelView view, HotelController controller, AssignmentRequest request, Stage detailStage) {
        Room assignedRoom = request.room;

        Button releaseButton = new Button(LABEL_RELEASE_ROOM);        
        releaseButton.setOnAction(event -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, ALERT_CONFIRM_RELEASE, ButtonType.YES, ButtonType.NO);
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    freeReservation(hotel, view, controller, request);
                    detailStage.hide();

                    Platform.runLater(() -> {
                        showRatingPopup(hotel, view, controller, assignedRoom, request);
                        detailStage.close();
                    });
                }
            });
        });

        VBox box = (VBox) detailStage.getScene().getRoot();
        box.getChildren().add(releaseButton);
    }

    private static void handleRoomReassignment(Hotel hotel, HotelView view, HotelController controller, AssignmentRequest request, VBox box, Stage detailStage) {
        Reservation res = request.reservation;
        Room assignedRoom = request.room;

        TextField roomInput = new TextField(assignedRoom.getName());
        roomInput.setPromptText("Enter new room name");

        Button confirmButton = new Button(BUTTON_CONFIRM_TEXT);
        confirmButton.setDisable(hotel.getRoom(roomInput.getText()).isReserved());

        roomInput.textProperty().addListener((obs, oldText, newText) -> {
            try {
                Room testRoom = hotel.getRoom(newText);
                confirmButton.setDisable(testRoom.isReserved());
            } catch (Exception ex) {
                confirmButton.setDisable(true);
            }
        });

        confirmButton.setOnAction(e -> {
            String newRoomName = roomInput.getText();
            Room newRoom = hotel.getRoom(newRoomName);

            freeReservation(hotel, view, controller, request);
            hotel.reserveRoom(new AssignmentRequest(res, newRoom));
            view.reserveRoom(newRoom.getName());

            detailStage.close();
        });

        box.getChildren().addAll(new Label(LABEL_ASSIGN_ROOM), roomInput, confirmButton);
    }




    private static Stage createModalStage(String title, Scene scene) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(title);
        stage.setScene(scene);
        return stage;
    }

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

    private static void freeReservation(Hotel hotel, HotelView view, HotelController controller, AssignmentRequest request) {
        Room room = request.room;
        hotel.freeRoom(room.getName());
        view.freeRoom(room.getName(), room.getType());
        controller.removeReservation(request);
    }  

    private static void showRatingPopup(Hotel hotel, HotelView view, HotelController controller, Room room, AssignmentRequest request) {
        Label instruction = createRatingInstructionLabel();
        HBox starsBox = createStarsBox();
        VBox layout = createRatingLayout(instruction, starsBox);

        Scene scene = new Scene(layout, POPUP_WIDTH, POPUP_HEIGHT);
        Stage ratingStage = createModalStage(POPUP_TITLE_RATING, scene);

        setupStarButtons(starsBox, room, ratingStage);
        ratingStage.showAndWait();
    }

    private static Label createRatingInstructionLabel() {
        Label instruction = new Label(LABEL_THANK_YOU);
        instruction.setStyle("-fx-font-size: 16px;");
        return instruction;
    }

    private static HBox createStarsBox() {
        HBox starsBox = new HBox(DEFAULT_SPACING);
        starsBox.setPadding(new Insets(DEFAULT_SPACING));
        starsBox.setAlignment(Pos.CENTER);
        return starsBox;
    }

    private static VBox createRatingLayout(Label instruction, HBox starsBox) {
        VBox layout = new VBox(SECTION_SPACING, instruction, starsBox);
        layout.setPadding(new Insets(SECTION_SPACING));
        layout.setAlignment(Pos.CENTER);
        return layout;
    }

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
                    iv.setImage(j <= index ? filledStar : emptyStar);
                }

                int rating = index + 1;
                Ticket ticket = TicketFactory.createTicket(room, rating);
                ratingStage.hide();

                Platform.runLater(() -> {
                    TicketGameView.show(ticket);
                    ratingStage.close();
                });
            });

            stars[i] = star;
            starsBox.getChildren().add(star);
        }
    }

}
