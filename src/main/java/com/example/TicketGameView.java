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

    /* show
    Inputs: ticket – the Ticket instance (Bronze, Silver, or Gold).
    Outputs: none.
    Description: Dispatches to the correct mini-game UI based on the ticket type; shows an error if unknown. */
    public static void show(Ticket ticket) {
        if (ticket instanceof BronzeTicket) {
            showBronzeGame((BronzeTicket) ticket); // Launch bronze game
        } else if (ticket instanceof SilverTicket) {
            showSilverGame((SilverTicket) ticket); // Launch silver game
        } else if (ticket instanceof GoldTicket) {
            showGoldGame((GoldTicket) ticket); // Launch gold game
        } else {
            showError(ERROR_UNKNOWN_TICKET); // Invalid type
        }
    }

    /* showBronzeGame
    Inputs: ticket – BronzeTicket instance.
    Outputs: none.
    Description: Displays a simple two-door guessing game for bronze ticket. User clicks on one of the two doors. */
    private static void showBronzeGame(BronzeTicket ticket) {
        Stage stage = createGameStage(TITLE_BRONZE); // Setup modal window

        Label prompt = new Label(PROMPT_CHOOSE_DOOR); // "Choose a door" prompt

        Button door1 = new Button("Door 1");
        Button door2 = new Button("Door 2");

        EventHandler<javafx.event.ActionEvent> handler = e -> {
            int chosen = (e.getSource() == door1) ? 0 : 1; // Determine selected door
            boolean won = ticket.playGame(chosen); // Check result
            showResultAlert(won, ticket.getCode()); // Show outcome
            stage.close(); // Close game window
        };

        door1.setOnAction(handler); // Bind handlers
        door2.setOnAction(handler);

        HBox buttonBox = new HBox(BUTTON_SPACING, door1, door2); // Button layout
        buttonBox.setAlignment(javafx.geometry.Pos.CENTER);

        VBox root = new VBox(DEFAULT_SPACING, prompt, buttonBox); // Main layout
        root.setPadding(new Insets(PADDING));
        root.setAlignment(javafx.geometry.Pos.CENTER);

        stage.setScene(new Scene(root)); // Set scene and display
        stage.showAndWait();
    }

    /* showSilverGame
    Inputs: ticket – SilverTicket instance.
    Outputs: none.
    Description: Displays a scrambled word and input field; user must guess the original word. */
    private static void showSilverGame(SilverTicket ticket) {
        Stage stage = createGameStage(TITLE_SILVER); // Create popup window

        Label instruction = new Label(PROMPT_SCRAMBLED_WORD); // Prompt message
        Label scrambledLabel = new Label(ticket.getScrambledWord()); // Scrambled word
        scrambledLabel.setStyle("-fx-font-size: " + FONT_SIZE_SCRAMBLED + "px; -fx-font-weight: bold;");

        TextField inputField = new TextField();
        inputField.setPromptText(PROMPT_YOUR_GUESS); // Hint for input

        Button validateButton = new Button(BUTTON_VALIDATE); // Confirm guess
        validateButton.setOnAction(e -> {
            String guess = inputField.getText(); // Get guess
            boolean won = ticket.playGame(guess); // Check result
            showResultAlert(won, ticket.getCode()); // Show outcome
            stage.close(); // Close popup
        });

        VBox layout = new VBox(DEFAULT_SPACING - 5, instruction, scrambledLabel, inputField, validateButton);
        layout.setAlignment(javafx.geometry.Pos.CENTER);
        layout.setPadding(new Insets(PADDING));

        stage.setScene(new Scene(layout));
        stage.showAndWait(); // Show dialog
    }

    /* showGoldGame
    Inputs: ticket – GoldTicket instance.
    Outputs: none.
    Description: Displays door selection interface; user can pick and eliminate until 2 remain. */
    private static void showGoldGame(GoldTicket ticket) {
        Stage stage = createGameStage(TITLE_GOLD); // Create game window

        VBox root = new VBox(DEFAULT_SPACING);
        root.setPadding(new Insets(PADDING));
        root.setAlignment(javafx.geometry.Pos.CENTER);

        Label instruction = new Label(PROMPT_CHOOSE_DOOR); // Prompt message
        HBox doorsBox = new HBox(DOOR_SPACING);
        doorsBox.setAlignment(javafx.geometry.Pos.CENTER);

        updateGoldUI(ticket, stage, root, instruction, doorsBox); // Load initial door UI

        stage.setScene(new Scene(root));
        stage.showAndWait(); // Show dialog
    }

    /* updateGoldUI
    Inputs: ticket – current game state; stage – window; root – main layout; instruction – prompt label; doorsBox – container for doors.
    Outputs: none.
    Description: Updates the UI with current remaining doors and adds handlers to them. */
    private static void updateGoldUI(GoldTicket ticket, Stage stage, VBox root, Label instruction, HBox doorsBox) {
        doorsBox.getChildren().clear(); // Clear old buttons

        for (int door : ticket.getRemainingDoors()) {
            Button button = new Button("Door " + (door + 1)); // Label starts at 1
            button.setOnAction(e -> {
                ticket.setPlayerChoice(door); // Save choice
                int eliminated = ticket.eliminateOneWrongDoor(); // Remove wrong option
                showGoldChoicePhase(ticket, stage, root, instruction, doorsBox); // Continue game
            });

            doorsBox.getChildren().add(button); // Add button to layout
        }

        root.getChildren().setAll(instruction, doorsBox); // Update layout
    }

    /* showKeepOrSwitchButtons
    Inputs: ticket – GoldTicket instance; stage – window; root – layout; instruction – prompt; doorsBox – door buttons container.
    Outputs: none.
    Description: Shows buttons to let the user either keep their door choice or switch to the other. */
    private static void showKeepOrSwitchButtons(GoldTicket ticket, Stage stage, VBox root, Label instruction, HBox doorsBox) {
        Button keep = new Button(BUTTON_KEEP); // Keep current door
        Button switchBtn = new Button(BUTTON_SWITCH); // Switch to other

        HBox choices = new HBox(BUTTON_SPACING, keep, switchBtn);
        choices.setAlignment(javafx.geometry.Pos.CENTER);

        keep.setOnAction(e -> {
            resolveGoldChoice(ticket, stage, root, instruction, doorsBox); // Proceed with current choice
        });

        switchBtn.setOnAction(e -> {
            for (int d : ticket.getRemainingDoors()) {
                if (d != ticket.getPlayerChoice()) {
                    ticket.setPlayerChoice(d); // Switch to remaining door
                    break;
                }
            }
            resolveGoldChoice(ticket, stage, root, instruction, doorsBox); // Proceed with new choice
        });

        root.getChildren().setAll(instruction, choices); // Show options
    }


    /* resolveGoldChoice
    Inputs: ticket – the GoldTicket instance; stage – the game window; root – layout; instruction – label for instructions; doorsBox – door buttons container.
    Outputs: none.
    Description: Handles the logic when the player reaches the final two doors. Displays a "keep or switch" option or ends the game if the player has made a final choice. */
    private static void resolveGoldChoice(GoldTicket ticket, Stage stage, VBox root, Label instruction, HBox doorsBox) {
        if (ticket.getRemainingDoors().size() == 2) { // Final two doors
            boolean won = ticket.playGame(ticket.getPlayerChoice()); // Check if the player's choice is correct
            showResultAlert(won, ticket.getCode()); // Show result and close game
            stage.close();
        } else {
            int eliminated = ticket.eliminateOneWrongDoor(); // Eliminate one wrong door
            instruction.setText("Door " + (eliminated + 1) + " is empty.\nDo you want to keep or switch?"); // Update prompt
            showKeepOrSwitchButtons(ticket, stage, root, instruction, doorsBox); // Show keep/switch options
        }
    }

    /* showGoldChoicePhase
    Inputs: ticket – the GoldTicket instance; stage – the game window; root – layout; instruction – label for instructions; doorsBox – door buttons container.
    Outputs: none.
    Description: Displays the door selection phase where the player chooses a door from the remaining options. */
    private static void showGoldChoicePhase(GoldTicket ticket, Stage stage, VBox root, Label instruction, HBox doorsBox) {
        doorsBox.getChildren().clear(); // Clear previous buttons
        instruction.setText("Choose a door:"); // Prompt player to choose a door

        for (int d : ticket.getRemainingDoors()) { // Loop over remaining doors
            Button choiceButton = new Button("Door " + (d + 1)); // Create door button
            choiceButton.setOnAction(e -> {
                ticket.setPlayerChoice(d); // Set the player's choice
                if (ticket.getRemainingDoors().size() > 2) { // More than 2 doors left
                    int eliminated = ticket.eliminateOneWrongDoor(); // Eliminate one wrong door
                    showGoldChoicePhase(ticket, stage, root, instruction, doorsBox); // Recursively show next phase
                } else { // Only 2 doors left, final choice
                    boolean won = ticket.playGame(d); // Check if the final choice is the winning door
                    showResultAlert(won, ticket.getCode()); // Show result
                    stage.close(); // Close game
                }
            });
            doorsBox.getChildren().add(choiceButton); // Add door button to the layout
        }

        root.getChildren().setAll(instruction, doorsBox); // Update the UI with new buttons
    }

    /* createGameStage
    Inputs: title – the title of the game window.
    Outputs: a Stage representing the game window.
    Description: Creates and configures a new modal window for the game. */
    private static Stage createGameStage(String title) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL); // Make the window modal
        stage.setTitle(title); // Set the window title
        return stage; // Return the configured stage
    }

    /* showResultAlert
    Inputs: won – whether the player won; code – the discount code if won.
    Outputs: none.
    Description: Displays a result alert with a success or failure message based on whether the player won. */
    private static void showResultAlert(boolean won, String code) {
        String msg = won ? RESULT_SUCCESS + code : RESULT_FAIL; // Success or failure message
        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK); // Create alert
        alert.setHeaderText(RESULT_HEADER); // Set the header text
        alert.showAndWait(); // Show the alert and wait for user action
    }

    /* showError
    Inputs: message – the error message to display.
    Outputs: none.
    Description: Displays an error message in a modal popup. */
    private static void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK); // Create error alert
        alert.setHeaderText(ERROR_HEADER); // Set error header
        alert.showAndWait(); // Show the error alert
    }

}
