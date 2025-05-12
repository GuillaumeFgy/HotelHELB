package com.example;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class TicketGameView {

    private static final int DEFAULT_SPACING = 20;
    private static final int BUTTON_SPACING = 20;
    private static final int DOOR_SPACING = 10;
    private static final int PADDING = 20;
    private static final int FONT_SIZE_SCRAMBLED = 18;
    private static final String TITLE_BRONZE = "Bronze Ticket Game";
    private static final String TITLE_SILVER = "Silver Ticket Game";
    private static final String TITLE_GOLD = "Gold Ticket Game";
    private static final String PROMPT_CHOOSE_DOOR = "Choose a door:";
    private static final String PROMPT_SCRAMBLED_WORD = "A word has been scrambled. Try to guess the original!";
    private static final String PROMPT_YOUR_GUESS = "Your guess";
    private static final String BUTTON_VALIDATE = "Validate";
    private static final String BUTTON_KEEP = "Keep";
    private static final String BUTTON_SWITCH = "Switch";
    private static final String RESULT_HEADER = "Result";
    private static final String RESULT_SUCCESS = "Congratulations! You won.\nCode: ";
    private static final String RESULT_FAIL = "Sorry, that was not the right door.";
    private static final String ERROR_HEADER = "Error";
    private static final String ERROR_UNKNOWN_TICKET = "Unknown ticket type.";

    public static void show(Ticket ticket) {
        if (ticket instanceof BronzeTicket) {
            showBronzeGame((BronzeTicket) ticket);
        } else if (ticket instanceof SilverTicket) {
            showSilverGame((SilverTicket) ticket);
        } else if (ticket instanceof GoldTicket) {
            showGoldGame((GoldTicket) ticket);
        } else {
            showError(ERROR_UNKNOWN_TICKET);
        }
    }

    private static void showBronzeGame(BronzeTicket ticket) {
        Stage stage = createGameStage(TITLE_BRONZE);

        Label prompt = new Label(PROMPT_CHOOSE_DOOR);

        Button door1 = new Button("Door 1");
        Button door2 = new Button("Door 2");

        EventHandler<javafx.event.ActionEvent> handler = e -> {
            int chosen;
            if (e.getSource() == door1) {
                chosen = 0;
            } else {
                chosen = 1;
            }
            boolean won = ticket.playGame(chosen);
            showResultAlert(won, ticket.getCode());
            stage.close();
        };

        door1.setOnAction(handler);
        door2.setOnAction(handler);

        HBox buttonBox = new HBox(BUTTON_SPACING, door1, door2);
        buttonBox.setAlignment(javafx.geometry.Pos.CENTER);

        VBox root = new VBox(DEFAULT_SPACING, prompt, buttonBox);
        root.setPadding(new Insets(PADDING));
        root.setAlignment(javafx.geometry.Pos.CENTER);

        stage.setScene(new Scene(root));
        stage.showAndWait();
    }

    private static void showSilverGame(SilverTicket ticket) {
        Stage stage = createGameStage(TITLE_SILVER);

        Label instruction = new Label(PROMPT_SCRAMBLED_WORD);
        Label scrambledLabel = new Label(ticket.getScrambledWord());
        scrambledLabel.setStyle("-fx-font-size: " + FONT_SIZE_SCRAMBLED + "px; -fx-font-weight: bold;");

        TextField inputField = new TextField();
        inputField.setPromptText(PROMPT_YOUR_GUESS);

        Button validateButton = new Button(BUTTON_VALIDATE);

        validateButton.setOnAction(e -> {
            String guess = inputField.getText();
            boolean won = ticket.playGame(guess);
            showResultAlert(won, ticket.getCode());
            stage.close();
        });

        VBox layout = new VBox(DEFAULT_SPACING - 5, instruction, scrambledLabel, inputField, validateButton);
        layout.setAlignment(javafx.geometry.Pos.CENTER);
        layout.setPadding(new Insets(PADDING));

        stage.setScene(new Scene(layout));
        stage.showAndWait();
    }

    private static void showGoldGame(GoldTicket ticket) {
        Stage stage = createGameStage(TITLE_GOLD);

        VBox root = new VBox(DEFAULT_SPACING);
        root.setPadding(new Insets(PADDING));
        root.setAlignment(javafx.geometry.Pos.CENTER);

        Label instruction = new Label(PROMPT_CHOOSE_DOOR);
        HBox doorsBox = new HBox(DOOR_SPACING);
        doorsBox.setAlignment(javafx.geometry.Pos.CENTER);

        updateGoldUI(ticket, stage, root, instruction, doorsBox);

        stage.setScene(new Scene(root));
        stage.showAndWait();
    }

    private static void updateGoldUI(GoldTicket ticket, Stage stage, VBox root, Label instruction, HBox doorsBox) {
        doorsBox.getChildren().clear();

        for (int door : ticket.getRemainingDoors()) {
            Button button = new Button("Door " + (door + 1));
            button.setOnAction(e -> {
                ticket.setPlayerChoice(door);
                int eliminated = ticket.eliminateOneWrongDoor();
                showGoldChoicePhase(ticket, stage, root, instruction, doorsBox);
            });

            doorsBox.getChildren().add(button);
        }

        root.getChildren().setAll(instruction, doorsBox);
    }

    private static void showKeepOrSwitchButtons(GoldTicket ticket, Stage stage, VBox root, Label instruction, HBox doorsBox) {
        Button keep = new Button(BUTTON_KEEP);
        Button switchBtn = new Button(BUTTON_SWITCH);

        HBox choices = new HBox(BUTTON_SPACING, keep, switchBtn);
        choices.setAlignment(javafx.geometry.Pos.CENTER);

        keep.setOnAction(e -> {
            resolveGoldChoice(ticket, stage, root, instruction, doorsBox);
        });

        switchBtn.setOnAction(e -> {
            for (int d : ticket.getRemainingDoors()) {
                if (d != ticket.getPlayerChoice()) {
                    ticket.setPlayerChoice(d);
                    break;
                }
            }
            resolveGoldChoice(ticket, stage, root, instruction, doorsBox);
        });

        root.getChildren().setAll(instruction, choices);
    }

    private static void resolveGoldChoice(GoldTicket ticket, Stage stage, VBox root, Label instruction, HBox doorsBox) {
        if (ticket.getRemainingDoors().size() == 2) {
            boolean won = ticket.playGame(ticket.getPlayerChoice());
            showResultAlert(won, ticket.getCode());
            stage.close();
        } else {
            int eliminated = ticket.eliminateOneWrongDoor();
            instruction.setText("Door " + (eliminated + 1) + " is empty.\nDo you want to keep or switch?");
            showKeepOrSwitchButtons(ticket, stage, root, instruction, doorsBox);
        }
    }

    private static void showGoldChoicePhase(GoldTicket ticket, Stage stage, VBox root, Label instruction, HBox doorsBox) {
        doorsBox.getChildren().clear();
        instruction.setText("Choose a door:");

        for (int d : ticket.getRemainingDoors()) {
            Button choiceButton = new Button("Door " + (d + 1));
            choiceButton.setOnAction(e -> {
                ticket.setPlayerChoice(d);

                if (ticket.getRemainingDoors().size() > 2) {
                    int eliminated = ticket.eliminateOneWrongDoor();
                    showGoldChoicePhase(ticket, stage, root, instruction, doorsBox);
                } else {
                    boolean won = ticket.playGame(d);
                    showResultAlert(won, ticket.getCode());
                    stage.close();
                }
            });
            doorsBox.getChildren().add(choiceButton);
        }

        root.getChildren().setAll(instruction, doorsBox);
    }


    private static Stage createGameStage(String title) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(title);
        return stage;
    }

    private static void showResultAlert(boolean won, String code) {
        String msg = won
                ? RESULT_SUCCESS + code
                : RESULT_FAIL;

        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        alert.setHeaderText(RESULT_HEADER);
        alert.showAndWait();
    }

    private static void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.setHeaderText(ERROR_HEADER);
        alert.showAndWait();
    }
}
