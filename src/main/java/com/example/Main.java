package com.example;

import javafx.application.Application;
import javafx.stage.Stage;
 
public class Main extends Application {
 
    /* start
    Inputs: primaryStage – the main JavaFX window provided at app launch.
    Outputs: none.
    Description: Entry point for JavaFX; initializes the HotelView and its controller. */
    @Override
    public void start(Stage primaryStage) throws Exception {
        HotelView view = new HotelView(primaryStage); // Create main view
        HotelController controller = new HotelController(view); // Bind controller to view
    }

    
    public static void main(String[] args) {
        launch(args);
    }
}
