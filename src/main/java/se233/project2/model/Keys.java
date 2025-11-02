package se233.project2.model;

import javafx.scene.input.KeyCode;
import java.util.HashMap;

public class Keys {
    private HashMap<KeyCode, Boolean> keys = new HashMap<>();

    public void update(KeyCode code, boolean isPressed) {
        keys.put(code, isPressed);
    }

    public boolean isPressed(KeyCode code) {
        return keys.getOrDefault(code, false);
    }
}