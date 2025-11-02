package se233.project2;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import se233.project2.controller.DrawingLoop;
import se233.project2.controller.GameLoop;
import se233.project2.view.GameStage;

public class   Launcher extends Application {
    private GameStage gameStage;
    private GameLoop gameLoop;
    private DrawingLoop drawingLoop;

    @Override
    public void start(Stage primaryStage) {
        // Create game stage
        gameStage = new GameStage();

           // Create scene
        Scene scene = new Scene(gameStage, GameStage.WIDTH, GameStage.HEIGHT);

        // Set up stage
        primaryStage.setTitle("Contra-Style Game");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        // Request focus for key events
        gameStage.requestFocus();

        // Start game loops
        gameLoop = new GameLoop(gameStage);
        drawingLoop = new DrawingLoop(gameStage);

        gameLoop.start();
        drawingLoop.start();
    }

    @Override
    public void stop() {
        if (gameLoop != null) {
            gameLoop.stop();
        }
        if (drawingLoop != null) {
            drawingLoop.stop();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}