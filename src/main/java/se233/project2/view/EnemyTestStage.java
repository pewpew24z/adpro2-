package se233.project2.view;

import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import se233.project2.controller.Updatable;
import se233.project2.model.enemy.RegularEnemy;
import se233.project2.model.enemy.SecondTierEnemy;

import java.util.ArrayList;
import java.util.List;

/**
 * MINIMAL WORKING EXAMPLE - ทดสอบ Enemy
 * Copy code นี้ไปใส่ใน GameStage.java ของคุณ
 */
public class EnemyTestStage extends Pane implements Updatable {

    private List<RegularEnemy> regularEnemies = new ArrayList<>();
    private List<SecondTierEnemy> secondTierEnemies = new ArrayList<>();

    private Image regularEnemySprite;
    private Image secondTierEnemySprite;
    private Image enemyBulletSprite;

    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;

    public EnemyTestStage() {
        this.setPrefSize(WIDTH, HEIGHT);
        this.setStyle("-fx-background-color: #87CEEB;"); // Sky blue background

        System.out.println("=== Enemy Test Stage ===");

        // Add ground for reference
        Rectangle ground = new Rectangle(0, HEIGHT - 50, WIDTH, 50);
        ground.setFill(Color.GREEN);
        this.getChildren().add(ground);

        // Load sprites (optional - จะใช้ fallback ถ้าไม่มี)
        loadSprites();

        // Spawn test enemies
        spawnTestEnemies();

        // Print debug info
        printDebugInfo();
    }

    private void loadSprites() {
        try {
            regularEnemySprite = new Image(
                    getClass().getResourceAsStream("/se233/project2/assets/enemies/regular_enemy.png")
            );
            System.out.println("✅ Regular enemy sprite loaded");
        } catch (Exception e) {
            System.out.println("⚠️ Regular enemy sprite not found - using red circle");
        }

        try {
            secondTierEnemySprite = new Image(
                    getClass().getResourceAsStream("/se233/project2/assets/enemies/secound-tier_enemy.png")
            );
            System.out.println("✅ Second-tier enemy sprite loaded");
        } catch (Exception e) {
            System.out.println("⚠️ Second-tier enemy sprite not found - using dark red circle");
        }

        try {
            enemyBulletSprite = new Image(
                    getClass().getResourceAsStream("/se233/project2/assets/enemies/enemy_bullet.png")
            );
            System.out.println("✅ Enemy bullet sprite loaded");
        } catch (Exception e) {
            System.out.println("⚠️ Enemy bullet sprite not found - using orange circle");
        }
    }

    private void spawnTestEnemies() {
        System.out.println("\n=== Spawning Test Enemies ===");

        // Test 1: Regular Enemy with sprites (or fallback)
        RegularEnemy enemy1 = new RegularEnemy(
                regularEnemySprite,
                enemyBulletSprite,
                150, 100,           // Position
                50, WIDTH - 50,     // X bounds
                50, 300,            // Y bounds
                3                   // HP
        );
        regularEnemies.add(enemy1);
        this.getChildren().add(enemy1);
        System.out.println("Regular Enemy 1 at (150, 100)");

        // Test 2: Regular Enemy without sprites (pure fallback test)
        RegularEnemy enemy2 = new RegularEnemy(
                null, null,         // Force fallback circle
                400, 120,
                50, WIDTH - 50,
                50, 300,
                3
        );
        regularEnemies.add(enemy2);
        this.getChildren().add(enemy2);
        System.out.println("Regular Enemy 2 (fallback) at (400, 120)");

        // Test 3: Second-Tier Enemy
        SecondTierEnemy enemy3 = new SecondTierEnemy(
                secondTierEnemySprite,
                enemyBulletSprite,
                650, 140,
                50, WIDTH - 50,
                50, 300,
                5
        );
        secondTierEnemies.add(enemy3);
        this.getChildren().add(enemy3);
        System.out.println("Second-Tier Enemy at (650, 140)");

        // Make sure enemies are in front
        for (RegularEnemy enemy : regularEnemies) {
            enemy.toFront();
        }
        for (SecondTierEnemy enemy : secondTierEnemies) {
            enemy.toFront();
        }

        System.out.println("\n✅ Total enemies spawned: " + (regularEnemies.size() + secondTierEnemies.size()));
    }

    private void printDebugInfo() {
        System.out.println("\n=== Debug Info ===");
        System.out.println("Stage size: " + WIDTH + "x" + HEIGHT);
        System.out.println("Regular enemies: " + regularEnemies.size());
        System.out.println("Second-tier enemies: " + secondTierEnemies.size());
        System.out.println("Children count: " + this.getChildren().size());

        for (int i = 0; i < regularEnemies.size(); i++) {
            RegularEnemy enemy = regularEnemies.get(i);
            System.out.println("Regular Enemy " + i + ":");
            System.out.println("  - Position: (" + enemy.getX() + ", " + enemy.getY() + ")");
            System.out.println("  - Size: " + enemy.getEnemyWidth() + "x" + enemy.getEnemyHeight());
            System.out.println("  - Visible: " + enemy.isVisible());
            System.out.println("  - Alive: " + enemy.isAlive());
        }

        for (int i = 0; i < secondTierEnemies.size(); i++) {
            SecondTierEnemy enemy = secondTierEnemies.get(i);
            System.out.println("Second-Tier Enemy " + i + ":");
            System.out.println("  - Position: (" + enemy.getX() + ", " + enemy.getY() + ")");
            System.out.println("  - Size: " + enemy.getEnemyWidth() + "x" + enemy.getEnemyHeight());
            System.out.println("  - Visible: " + enemy.isVisible());
            System.out.println("  - Alive: " + enemy.isAlive());
        }
    }

    public void update(long now) {
        // Update regular enemies
        for (RegularEnemy enemy : regularEnemies) {
            enemy.update(now);

            // Add bullets to scene
            for (var bullet : enemy.getBullets()) {
                if (!this.getChildren().contains(bullet)) {
                    this.getChildren().add(bullet);
                }
            }
        }

        // Update second-tier enemies
        for (SecondTierEnemy enemy : secondTierEnemies) {
            enemy.update(now);

            // Add bullets to scene
            for (var bullet : enemy.getBullets()) {
                if (!this.getChildren().contains(bullet)) {
                    this.getChildren().add(bullet);
                }
            }
        }
    }
}