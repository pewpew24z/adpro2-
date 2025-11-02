package se233.project2.model.item;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * Bullet class - รองรับกระสุนทุกแบบ
 */
public class Bullet extends Pane {
    private ImageView imageView;
    private Circle fallbackCircle;

    private double x, y;
    private double speedX, speedY;
    private boolean active = true;
    private boolean isPlayerBullet;

    private static final int BULLET_SIZE = 24; // ⭐ ขนาดใหญ่ขึ้น
    private static final double GRAVITY = 0.3;

    /**
     * Constructor แบบง่าย (horizontal)
     */
    public Bullet(Image sprite, double x, double y, boolean movingRight, boolean isPlayerBullet) {
        this(sprite, x, y, movingRight ? 10 : -10, 0, isPlayerBullet);
    }

    /**
     * Constructor แบบกำหนด velocity (สำหรับเล็งยิง)
     */
    public Bullet(Image sprite, double x, double y, double speedX, double speedY, boolean isPlayerBullet) {
        this.x = x;
        this.y = y;
        this.speedX = speedX;
        this.speedY = speedY;
        this.isPlayerBullet = isPlayerBullet;

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
        fallbackCircle.setFill(isPlayerBullet ? Color.YELLOW : Color.ORANGE);
        this.getChildren().clear();
        this.getChildren().add(fallbackCircle);
    }

    /**
     * Update bullet position (like professor's update method)
     */
    public void update() {
        x += speedX;
        y += speedY;

        // Apply gravity to enemy bullets for projectile motion
        if (!isPlayerBullet) {
            speedY += GRAVITY;
        }

        this.setTranslateX(x);
        this.setTranslateY(y);

        // Deactivate if out of bounds
        if (x < -50 || x > 1330 || y < -50 || y > 770) {
            active = false;
        }
    }

    public boolean checkGroundCollision(int groundY) {
        return y >= groundY;
    }
    // ✅ ใช้สำหรับกำหนดความเร็วแนวตั้ง (vy)
    public void setVerticalSpeed(double speedY) {
        this.speedY = speedY;
    }


    public void deactivate() {
        this.active = false;
    }
    public void setVelocity(double vx, double vy) {
        this.speedX = vx;
        this.speedY = vy;
    }

    // Getters
    public boolean isActive() { return active; }
    public boolean isPlayerBullet() { return isPlayerBullet; }
    public double getX() { return x; }
    public double getY() { return y; }
    public double getCenterX() { return x + BULLET_SIZE / 2; }
    public double getCenterY() { return y + BULLET_SIZE / 2; }
    public double getSpeedX() { return speedX; }
    public double getSpeedY() { return speedY; }
}