package com.example;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class TicketGameView {

    public static void show(Ticket ticket) {
        if (ticket instanceof BronzeTicket) {
            showBronzeGame((BronzeTicket) ticket);
        } else if (ticket instanceof SilverTicket) {
            showSilverGame((SilverTicket) ticket);
        } else if (ticket instanceof GoldTicket) {
            showGoldGame((GoldTicket) ticket);
        } else {
            showError("Unknown ticket type.");
        }
    }

    private static void showBronzeGame(BronzeTicket ticket) {
        Stage stage = createGameStage("Bronze Ticket Game");

        Label prompt = new Label("Choose a door:");

        Button door1 = new Button("Door 1");
        Button door2 = new Button("Door 2");

        EventHandler<javafx.event.ActionEvent> handler = e -> {
            int chosen = (e.getSource() == door1) ? 0 : 1;
            boolean won = ticket.playGame(chosen);

            showResultAlert(won, ticket.getCode());
            stage.close();
        };

        door1.setOnAction(handler);
        door2.setOnAction(handler);

        HBox buttonBox = new HBox(20, door1, door2);
        buttonBox.setAlignment(javafx.geometry.Pos.CENTER);

        VBox root = new VBox(20, prompt, buttonBox);
        root.setPadding(new Insets(20));
        root.setAlignment(javafx.geometry.Pos.CENTER);

        stage.setScene(new Scene(root));
        stage.showAndWait();
    }

    private static void showSilverGame(SilverTicket ticket) {
        Stage stage = createGameStage("Silver Ticket Game");

        Label instruction = new Label("Un mot a été mélangé. Retrouvez l’original !");
        Label scrambledLabel = new Label(ticket.getScrambledWord());
        scrambledLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TextField inputField = new TextField();
        inputField.setPromptText("Votre proposition");

        Button validateButton = new Button("Valider");

        validateButton.setOnAction(e -> {
            String guess = inputField.getText();
            boolean won = ticket.playGame(guess);

            showResultAlert(won, ticket.getCode());
            stage.close();
        });

        VBox layout = new VBox(15, instruction, scrambledLabel, inputField, validateButton);
        layout.setAlignment(javafx.geometry.Pos.CENTER);
        layout.setPadding(new Insets(20));

        stage.setScene(new Scene(layout));
        stage.showAndWait();
    }

    private static void showGoldGame(GoldTicket ticket) {
        Stage stage = createGameStage("Gold Ticket Game");
    
        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setAlignment(javafx.geometry.Pos.CENTER);
    
        Label instruction = new Label("Choisissez une porte parmi celles proposées :");
        HBox doorsBox = new HBox(10);
        doorsBox.setAlignment(javafx.geometry.Pos.CENTER);
    
        updateGoldUI(ticket, stage, root, instruction, doorsBox);
    
        stage.setScene(new Scene(root));
        stage.showAndWait();
    }

    private static void updateGoldUI(GoldTicket ticket, Stage stage, VBox root, Label instruction, HBox doorsBox) {
        doorsBox.getChildren().clear();
    
        for (int door : ticket.getRemainingDoors()) {
            Button button = new Button("Porte " + (door + 1));
            button.setOnAction(e -> {
                if (ticket.getPlayerChoice() == -1) {
                    ticket.setPlayerChoice(door);
                    int eliminated = ticket.eliminateOneWrongDoor();
                    instruction.setText("La porte " + (eliminated + 1) + " est vide.\nSouhaitez-vous garder votre choix ou changer ?");
                    showKeepOrSwitchButtons(ticket, stage, root, instruction, doorsBox);
                }
            });
            doorsBox.getChildren().add(button);
        }
    
        root.getChildren().setAll(instruction, doorsBox);
    }

    private static void showKeepOrSwitchButtons(GoldTicket ticket, Stage stage, VBox root, Label instruction, HBox doorsBox) {
        Button keep = new Button("Garder");
        Button switchBtn = new Button("Changer");
    
        HBox choices = new HBox(20, keep, switchBtn);
        choices.setAlignment(javafx.geometry.Pos.CENTER);
    
        keep.setOnAction(e -> {
            if (ticket.getRemainingDoors().size() == 2) {
                boolean won = ticket.playGame(ticket.getPlayerChoice());
                showResultAlert(won, ticket.getCode());
                stage.close();
            } else {
                int eliminated = ticket.eliminateOneWrongDoor();
                instruction.setText("La porte " + (eliminated + 1) + " est vide.\nSouhaitez-vous garder ou changer ?");
                showKeepOrSwitchButtons(ticket, stage, root, instruction, doorsBox);
            }
        });
    
        switchBtn.setOnAction(e -> {
            for (int d : ticket.getRemainingDoors()) {
                if (d != ticket.getPlayerChoice()) {
                    ticket.setPlayerChoice(d);
                    break;
                }
            }
    
            if (ticket.getRemainingDoors().size() == 2) {
                boolean won = ticket.playGame(ticket.getPlayerChoice());
                showResultAlert(won, ticket.getCode());
                stage.close();
            } else {
                int eliminated = ticket.eliminateOneWrongDoor();
                instruction.setText("La porte " + (eliminated + 1) + " est vide.\nSouhaitez-vous garder ou changer ?");
                showKeepOrSwitchButtons(ticket, stage, root, instruction, doorsBox);
            }
        });
    
        root.getChildren().setAll(instruction, choices);
    }
    
    // 🔁 Shared logic to create a modal stage
    private static Stage createGameStage(String title) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(title);
        return stage;
    }

    // 🔁 Shared logic to show result
    private static void showResultAlert(boolean won, String code) {
        String msg = won
            ? "Félicitations ! Vous avez gagné.\nCode : " + code
            : "Dommage, ce n'était pas la bonne porte.";

        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        alert.setHeaderText("Résultat");
        alert.showAndWait();
    }

    private static void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.setHeaderText("Erreur");
        alert.showAndWait();
    }
}
