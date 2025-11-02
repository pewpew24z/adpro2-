package se233.project2.view;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * GameUIHandler - จัดการ UI elements (Lives, Score, Stage)
 */
public class GameUIHandler {
    private Pane gamePane;
    private Score scoreLabel;
    private Text stageLabel;
    private Text waveLabel;

    private List<ImageView> liveIcons;
    private Image liveIcon;

    public GameUIHandler(Pane gamePane, Image liveIconImage) {
        this.gamePane = gamePane;
        this.liveIcon = liveIconImage;
        this.liveIcons = new ArrayList<>();
    }

    public void initialize(int stage) {
        // Score
        scoreLabel = new Score(20, 20);

        // Stage label
        stageLabel = new Text("STAGE " + stage);
        stageLabel.setFont(Font.font("Arial", 32));
        stageLabel.setFill(Color.YELLOW);
        stageLabel.setTranslateX(1280 / 2 - 80);
        stageLabel.setTranslateY(50);

        // Wave label
        waveLabel = new Text("Clear Enemies!");
        waveLabel.setFont(Font.font("Arial", 24));
        waveLabel.setFill(Color.ORANGE);
        waveLabel.setTranslateX(1280 / 2 - 100);
        waveLabel.setTranslateY(100);

        gamePane.getChildren().addAll(scoreLabel, stageLabel, waveLabel);
    }

    public void createLiveIcons(int lives) {
        for (ImageView icon : liveIcons) {
            gamePane.getChildren().remove(icon);
        }
        liveIcons.clear();

        for (int i = 0; i < lives; i++) {
            ImageView icon = new ImageView(liveIcon);
            icon.setFitWidth(32);
            icon.setFitHeight(32);
            icon.setPreserveRatio(true);
            icon.setTranslateX(1280 - 150 + (i * 40));
            icon.setTranslateY(20);
            liveIcons.add(icon);
            gamePane.getChildren().add(icon);
        }
    }

    public void updateStageLabel(int stage) {
        stageLabel.setText("STAGE " + stage);
    }

    public void updateWaveLabel(String text, Color color) {
        waveLabel.setText(text);
        waveLabel.setFill(color);
    }

    public void addScore(int points) {
        scoreLabel.addScore(points);
    }

    public void clear() {
        gamePane.getChildren().remove(scoreLabel);
        gamePane.getChildren().remove(stageLabel);
        gamePane.getChildren().remove(waveLabel);
        for (ImageView icon : liveIcons) {
            gamePane.getChildren().remove(icon);
        }
        liveIcons.clear();
    }

    public int getScore() {
        return scoreLabel.getScore();
    }
}