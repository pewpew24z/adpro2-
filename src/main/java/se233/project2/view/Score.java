package se233.project2.view;

import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class Score extends Label {
    private int score;

    public Score(int x, int y) {
        this.score = 0;
        this.setTranslateX(x);
        this.setTranslateY(y);
        this.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        this.setTextFill(Color.WHITE);
        updateDisplay();
    }

    public void addScore(int points) {
        score += points;
        updateDisplay();
    }

    public void resetScore() {
        score = 0;
        updateDisplay();
    }

    private void updateDisplay() {
        this.setText("SCORE: " + score);
    }

    public int getScore() {
        return score;
    }
}