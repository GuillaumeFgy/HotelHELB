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
import javafx.animation.PauseTransition;
import javafx.util.Duration;


public class ReservationView {

    public static void show(AssignmentRequest request, Hotel hotel, HotelView view, HotelController controller, boolean fromRoomButton) {
        Stage detailStage = new Stage();
        detailStage.initModality(Modality.APPLICATION_MODAL);
        detailStage.setTitle("Reservation Details");
    
        Reservation res = request.reservation;
        Room assignedRoom = request.room;
    
        Label nameLabel = new Label("Client: " + res.getFirstName() + " " + res.getLastName());
        Label personsLabel = new Label("Persons: " + res.getNumPersons());
        Label childrenLabel = new Label("Children: " + res.getNumChildren());
        Label smokerLabel = new Label("Smoker: " + (res.isSmoker() ? "Yes" : "No"));
        Label purposeLabel = new Label("Stay Purpose: " + res.getStayPurpose().name());
    
        VBox box = new VBox(10, nameLabel, personsLabel, childrenLabel, smokerLabel, purposeLabel);
        box.setPadding(new Insets(20));
    
        if (fromRoomButton) {
            // LIBÉRATION de chambre
            Button releaseButton = new Button("Libérer la chambre");
            releaseButton.setOnAction(event -> {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Confirmer la libération de la chambre ?", ButtonType.YES, ButtonType.NO);
                confirm.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.YES) {
                        hotel.freeRoom(assignedRoom.getName());
                        view.freeRoom(assignedRoom.getName(), assignedRoom.getType());
                        controller.removeReservation(request);
                    
                        detailStage.hide(); // cache le stage
                    
                        Platform.runLater(() -> {
                            showRatingPopup(hotel, view, controller, assignedRoom, request);
                            detailStage.close(); // fermeture définitive
                        });
                    }
                    
                });
            });
    
            box.getChildren().add(releaseButton);
            detailStage.setScene(new Scene(box, 300, 250));
        } else {
            // RÉASSIGNATION de chambre
            TextField roomInput = new TextField(assignedRoom.getName());
            roomInput.setPromptText("Enter new room name");
    
            Button confirmButton = new Button("Confirm");
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
    
                hotel.freeRoom(assignedRoom.getName());
                hotel.reserveRoom(new AssignmentRequest(res, newRoom));
                view.freeRoom(assignedRoom.getName(), assignedRoom.getType());
                view.reserveRoom(newRoom.getName());
                controller.removeReservation(request);
                detailStage.close();
            });
    
            box.getChildren().addAll(new Label("Assign Room:"), roomInput, confirmButton);
            detailStage.setScene(new Scene(box, 300, 300));
        }
    
        detailStage.showAndWait();
    }

    

    private static void showRatingPopup(Hotel hotel, HotelView view, HotelController controller, Room room, AssignmentRequest request) {
        Stage ratingStage = new Stage();
        ratingStage.initModality(Modality.APPLICATION_MODAL);
        ratingStage.setTitle("Évaluez votre séjour");
    
        Label instruction = new Label("Merci pour votre séjour ! Cliquez pour évaluer :");
        instruction.setStyle("-fx-font-size: 16px;");
    
        HBox starsBox = new HBox(10);
        starsBox.setPadding(new Insets(10));
        starsBox.setAlignment(Pos.CENTER);
    
        // Chargement depuis le chemin relatif au dossier d'exécution
        String relativePath = "file:src/main/java/com/example/Images/";
        Image emptyStar = new Image(relativePath + "star_empty.png");
        Image filledStar = new Image(relativePath + "star_filled.png");
    
        Button[] stars = new Button[5];
    
        for (int i = 0; i < 5; i++) {
            ImageView imageView = new ImageView(emptyStar);
            imageView.setFitWidth(40);
            imageView.setFitHeight(40);
    
            Button star = new Button();
            star.setGraphic(imageView);
            star.setStyle("-fx-background-color: transparent; -fx-padding: 0;");
            final int index = i;
            star.setOnAction(e -> {
                for (int j = 0; j < 5; j++) {
                    ImageView iv = (ImageView) stars[j].getGraphic();
                    iv.setImage(j <= index ? filledStar : emptyStar);
                }
            
                int rating = index + 1; // since index is 0-based
                Ticket ticket = TicketFactory.createTicket(room, rating);
                ratingStage.hide(); // hide immediately to free UI thread

                Platform.runLater(() -> {
                    TicketGameView.show(ticket);
                    ratingStage.close(); // clean up after game is done
                });

            });
            
    
            stars[i] = star;
            starsBox.getChildren().add(star);
        }
    
        VBox box = new VBox(20, instruction, starsBox);
        box.setPadding(new Insets(20));
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: lightblue;");
    
        Scene scene = new Scene(box, 300, 200);
        ratingStage.setScene(scene);
        ratingStage.showAndWait();
    }
    


}
