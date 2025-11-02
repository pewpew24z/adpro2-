package se233.project2.controller;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * DrawingLoop - จัดการการเปลี่ยน Scene และ Stage
 * ใช้สำหรับการสลับระหว่างหน้าจอต่างๆ
 */
public class DrawingLoop {
    private Stage primaryStage;
    private Scene currentScene;

    public DrawingLoop(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    /**
     * สลับไปยัง Scene ใหม่
     */
    public void switchScene(Scene newScene) {
        this.currentScene = newScene;
        primaryStage.setScene(newScene);
        System.out.println("🎬 Scene switched");
    }

    /**
     * สลับไปยัง Pane ใหม่ (โดยใช้ Scene เดิม)
     */
    public void switchPane(Pane newPane) {
        if (currentScene != null) {
            currentScene.setRoot(newPane);
            newPane.requestFocus();
            System.out.println("🎬 Pane switched");
        }
    }

    /**
     * ดึง Scene ปัจจุบัน
     */
    public Scene getCurrentScene() {
        return currentScene;
    }

    /**
     * ดึง Stage หลัก
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * ตั้งชื่อ Stage
     */
    public void setTitle(String title) {
        primaryStage.setTitle(title);
    }

    /**
     * แสดง/ซ่อน Stage
     */
    public void show() {
        primaryStage.show();
    }

    public void hide() {
        primaryStage.hide();
    }

    /**
     * ตั้งค่า fullscreen
     */
    public void setFullscreen(boolean fullscreen) {
        primaryStage.setFullScreen(fullscreen);
    }

    /**
     * ตั้งค่าการ resize
     */
    public void setResizable(boolean resizable) {
        primaryStage.setResizable(resizable);
    }
}