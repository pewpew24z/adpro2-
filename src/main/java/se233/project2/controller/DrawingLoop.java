package se233.project2.controller;

import javafx.animation.AnimationTimer;
import se233.project2.view.GameStage;

public class DrawingLoop extends AnimationTimer {
    private GameStage gameStage;

    public DrawingLoop(GameStage gameStage) {
        this.gameStage = gameStage;
    }

    @Override
    public void handle(long now) {
    }
}