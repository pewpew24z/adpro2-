package se233.project2.controller;

import javafx.animation.AnimationTimer;

public class GameLoop extends AnimationTimer {
    private Updatable stage;
    private long lastUpdate = 0;
    private final long FRAME_TIME = 16_666_666; // ~60 FPS

    public GameLoop(Updatable stage) {
        this.stage = stage;
    }

    @Override
    public void handle(long now) {
        if (now - lastUpdate >= FRAME_TIME) {
            stage.update(now);
            lastUpdate = now;
        }
    }
}