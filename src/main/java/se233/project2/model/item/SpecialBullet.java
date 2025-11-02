package se233.project2.model.item;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * SpecialBullet - กระสุนพิเศษที่ยิงกระจายรอบตัว
 * - ยิงออก 8 ทิศทาง
 * - แรงกว่ากระสุนธรรมดา (damage = 3)
 * - ใช้เวลา 1.5 วินาที
 */
public class SpecialBullet extends Pane {
    private ImageView imageView;
    private Circle fallbackCircle;

    private double x, y;
    private double speedX, speedY;
    private boolean active = true;
    private int damage = 3; // แรงกว่ากระสุนธรรมดา

    private static final int BULLET_SIZE = 32; // ใหญ่กว่ากระสุนธรรมดา
    private long creationTime;
    private static final long DURATION = 1_500_000_000L; // 1.5 วินาที

    public SpecialBullet(Image sprite, double x, double y, double speedX, double speedY) {
        this.x = x;
        this.y = y;
        this.speedX = speedX;
        this.speedY = speedY;
        this.creationTime = System.nanoTime();

        setupSprite(sprite);
        this.setTranslateX(x);
        this.setTranslateY(y);
    }

    private void setupSprite(Image sprite) {
        if (sprite != null) {
            try {
                imageView = new ImageView(sprite);
                imageView.setFitWidth(BULLET_SIZE);
                imageView.setFitHeight(BULLET_SIZE);
                imageView.setPreserveRatio(true);
                imageView.setSmooth(false);
                this.getChildren().add(imageView);
            } catch (Exception e) {
                createFallbackCircle();
            }
        } else {
            createFallbackCircle();
        }
    }

    private void createFallbackCircle() {
        fallbackCircle = new Circle(BULLET_SIZE / 2);
        fallbackCircle.setFill(Color.PURPLE);
        this.getChildren().clear();
        this.getChildren().add(fallbackCircle);
    }

    public void update() {
        x += speedX;
        y += speedY;

        this.setTranslateX(x);
        this.setTranslateY(y);

        // ตรวจสอบอายุของกระสุน
        long currentTime = System.nanoTime();
        if (currentTime - creationTime >= DURATION) {
            active = false;
        }

        // Deactivate if out of bounds
        if (x < -50 || x > 1330 || y < -50 || y > 770) {
            active = false;
        }
    }

    public void deactivate() {
        this.active = false;
    }

    // Getters
    public boolean isActive() { return active; }
    public double getX() { return x; }
    public double getY() { return y; }
    public double getCenterX() { return x + BULLET_SIZE / 2; }
    public double getCenterY() { return y + BULLET_SIZE / 2; }
    public int getDamage() { return damage; }
}