package se233.project2;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import se233.project2.controller.GameLoop;
import se233.project2.controller.SoundController;
import se233.project2.view.GameStage;
import se233.project2.view.StartScreen;

public class Launcher extends Application {
    private GameStage gameStage;
    private GameLoop gameLoop;
    private Scene scene;
    private Stage primaryStage;
    private StartScreen startScreen;
    private SoundController soundController;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        soundController = SoundController.getInstance();

        // Load start screen background
        Image startBg = loadImage("stage/StartScreen.png");

        // Create start screen
        startScreen = new StartScreen(startBg, this::startGame, this::showStartScreen);
        scene = new Scene(startScreen, 1280, 720);

        // Set up stage
        primaryStage.setTitle("Contra-Style Game");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        // ⭐ เล่นเพลงหน้า Start Screen
        soundController.playStartScreenMusic();
    }

    private void startGame() {
        // ⭐ หยุดเพลงหน้า Start Screen
        soundController.stopStartScreenMusic();

        // Create game stage
        gameStage = new GameStage(this::showStartScreen);
        scene.setRoot(gameStage);

        // Request focus for key events
        gameStage.requestFocus();

        // Start game loop
        gameLoop = new GameLoop(gameStage);
        gameLoop.start();
    }

    // ⭐ Method สำหรับย้อนกลับไปหน้า Start Screen
    private void showStartScreen() {
        // Stop game loop if running
        if (gameLoop != null) {
            gameLoop.stop();
        }

        // Create new start screen
        Image startBg = loadImage("stage/StartScreen.png");
        startScreen = new StartScreen(startBg, this::startGame, this::showStartScreen);
        scene.setRoot(startScreen);

        // Play start screen music
        soundController.playStartScreenMusic();
    }

    private Image loadImage(String filename) {
        try {
            String path = "/se233/project2/assets/" + filename;
            var stream = getClass().getResourceAsStream(path);
            if (stream != null) return new Image(stream);
        } catch (Exception e) {
            System.err.println("Error loading " + filename);
        }
        return null;
    }

    @Override
    public void stop() {
        if (gameLoop != null) {
            gameLoop.stop();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}