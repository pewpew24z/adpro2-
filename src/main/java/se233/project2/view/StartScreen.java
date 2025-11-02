package se233.project2.view;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * StartScreen - หน้าจอเริ่มเกม
 */
public class StartScreen extends Pane {
    private Button startButton;
    private Runnable onStartGame;
    private Runnable onShowStartScreen; // ⭐ เพิ่มสำหรับปุ่ม Home

    public StartScreen(Image backgroundImage, Runnable onStartGame, Runnable onShowStartScreen) {
        this.onStartGame = onStartGame;
        this.onShowStartScreen = onShowStartScreen;
        this.setPrefSize(1280, 720);

        // Background
        if (backgroundImage != null) {
            ImageView bg = new ImageView(backgroundImage);
            bg.setFitWidth(1280);
            bg.setFitHeight(720);
            bg.setPreserveRatio(false);
            this.getChildren().add(bg);
        }

        // Start button
        startButton = new Button("START");
        startButton.setStyle(
                "-fx-font-size: 36px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-color: #FF6B6B; " +
                        "-fx-text-fill: white; " +
                        "-fx-padding: 20 60; " +
                        "-fx-background-radius: 10; " +
                        "-fx-border-radius: 10; " +
                        "-fx-border-color: white; " +
                        "-fx-border-width: 3;"
        );

        // Hover effect
        startButton.setOnMouseEntered(e ->
                startButton.setStyle(
                        "-fx-font-size: 36px; " +
                                "-fx-font-weight: bold; " +
                                "-fx-background-color: #FF4444; " +
                                "-fx-text-fill: white; " +
                                "-fx-padding: 20 60; " +
                                "-fx-background-radius: 10; " +
                                "-fx-border-radius: 10; " +
                                "-fx-border-color: yellow; " +
                                "-fx-border-width: 3;"
                )
        );

        startButton.setOnMouseExited(e ->
                startButton.setStyle(
                        "-fx-font-size: 36px; " +
                                "-fx-font-weight: bold; " +
                                "-fx-background-color: #FF6B6B; " +
                                "-fx-text-fill: white; " +
                                "-fx-padding: 20 60; " +
                                "-fx-background-radius: 10; " +
                                "-fx-border-radius: 10; " +
                                "-fx-border-color: white; " +
                                "-fx-border-width: 3;"
                )
        );

        startButton.setOnAction(e -> {
            if (onStartGame != null) {
                onStartGame.run();
            }
        });

        // Center button
        VBox buttonBox = new VBox(startButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPrefSize(1280, 720);
        buttonBox.setTranslateY(200); // Position lower on screen

        this.getChildren().add(buttonBox);
    }
}