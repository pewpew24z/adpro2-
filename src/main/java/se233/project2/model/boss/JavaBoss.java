package se233.project2.model.boss;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import se233.project2.model.item.Bullet;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class JavaBoss extends Pane {
    private Rectangle hitbox;
    private Rectangle healthBar;
    private Rectangle healthBarBg;
    private ImageView normalSprite;
    private ImageView deadSprite;
    private ImageView weaponSprite; // อาวุธของ boss

    private double bossX, bossY, bossWidth, bossHeight;
    private int health;
    private int maxHealth;
    private boolean alive = true;

    // Movement (JavaBoss เคลื่อนที่ได้)
    private double velocityY = 2;
    private double topBound, bottomBound;

    // Shooting patterns
    private long lastShoot = 0;
    private long shootInterval = 800_000_000; // 0.8 seconds
    private Random random;
    private List<Bullet> bullets;
    private Image bulletSprite;
    private int shotPattern = 0; // 0=single, 1=spread, 2=rapid

    private static final int HEALTH_BAR_WIDTH = 200;
    private static final int HEALTH_BAR_HEIGHT = 15;

    public JavaBoss(double x, double y, double width, double height, int maxHealth,
                    Image normalImage, Image deadImage, Image weaponImage, Image bulletSprite) {
        this.bossX = x;
        this.bossY = y;
        this.bossWidth = width;
        this.bossHeight = height;
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.bulletSprite = bulletSprite;
        this.random = new Random();
        this.bullets = new ArrayList<>();

        // Movement bounds
        this.topBound = 100;
        this.bottomBound = 500;

        // Sprites setup
        if (normalImage != null) {
            normalSprite = new ImageView(normalImage);
            normalSprite.setFitWidth(bossWidth);
            normalSprite.setFitHeight(bossHeight);
            normalSprite.setPreserveRatio(false);
        }

        if (deadImage != null) {
            deadSprite = new ImageView(deadImage);
            deadSprite.setFitWidth(bossWidth);
            deadSprite.setFitHeight(bossHeight);
            deadSprite.setPreserveRatio(false);
            deadSprite.setVisible(false);
        }

        if (weaponImage != null) {
            weaponSprite = new ImageView(weaponImage);
            weaponSprite.setFitWidth(80);
            weaponSprite.setFitHeight(80);
            weaponSprite.setTranslateX(-40); // position weapon
            weaponSprite.setTranslateY(bossHeight / 2 - 40);
        }

        // Hitbox
        hitbox = new Rectangle(bossWidth, bossHeight);
        hitbox.setFill(Color.TRANSPARENT);

        // Health bar
        healthBarBg = new Rectangle(HEALTH_BAR_WIDTH, HEALTH_BAR_HEIGHT, Color.DARKGRAY);
        healthBarBg.setTranslateY(-25);

        healthBar = new Rectangle(HEALTH_BAR_WIDTH, HEALTH_BAR_HEIGHT, Color.ORANGE);
        healthBar.setTranslateY(-25);

        // Add to pane
        if (normalSprite != null) this.getChildren().add(normalSprite);
        if (deadSprite != null) this.getChildren().add(deadSprite);
        if (weaponSprite != null) this.getChildren().add(weaponSprite);
        this.getChildren().addAll(hitbox, healthBarBg, healthBar);

        this.setTranslateX(bossX);
        this.setTranslateY(bossY);
    }

    public void update(long now) {
        if (!alive) return;

        // Vertical movement
        bossY += velocityY;
        if (bossY <= topBound || bossY >= bottomBound) {
            velocityY = -velocityY;
        }
        this.setTranslateY(bossY);

        // Shooting with patterns
        if (now - lastShoot > shootInterval) {
            shootPattern();
            lastShoot = now;

            // Change pattern randomly
            shotPattern = random.nextInt(3);
        }

        // Update bullets
        List<Bullet> toRemove = new ArrayList<>();
        for (Bullet b : bullets) {
            b.update();
            if (!b.isActive()) toRemove.add(b);
        }
        bullets.removeAll(toRemove);
    }

    private void shootPattern() {
        double bulletX = bossX;
        double bulletY = bossY + bossHeight / 2;

        switch (shotPattern) {
            case 0: // Single straight shot
                createBullet(bulletX, bulletY, 0, 0);
                break;

            case 1: // Spread shot (3 bullets)
                createBullet(bulletX, bulletY, 0, -3); // up-left
                createBullet(bulletX, bulletY, 0, 0);  // straight
                createBullet(bulletX, bulletY, 0, 3);  // down-left
                break;

            case 2: // Rapid fire (5 bullets with slight delay)
                for (int i = 0; i < 5; i++) {
                    createBullet(bulletX, bulletY + (i * 10 - 20), 0, 0);
                }
                break;
        }
    }

    private void createBullet(double x, double y, double offsetX, double offsetY) {
        Bullet bullet = new Bullet(
                bulletSprite,
                x,
                y,
                false,  // shoot left
                false   // enemy bullet
        );

        // Set custom velocity if needed
        if (offsetY != 0) {
            bullet.setVerticalSpeed(offsetY);
        }

        bullets.add(bullet);
    }

    public void takeDamage(int damage) {
        if (!alive) return;

        health -= damage;
        if (health < 0) health = 0;

        double healthPercent = (double) health / maxHealth;
        healthBar.setWidth(HEALTH_BAR_WIDTH * healthPercent);

        if (health <= 0) {
            alive = false;
            hitbox.setVisible(false);
            healthBar.setVisible(false);
            healthBarBg.setVisible(false);

            if (normalSprite != null) normalSprite.setVisible(false);
            if (weaponSprite != null) weaponSprite.setVisible(false);
            if (deadSprite != null) deadSprite.setVisible(true);

            System.out.println("Java Boss defeated!");
        }
    }

    public boolean checkBulletCollision(Bullet bullet) {
        if (!alive || !bullet.isPlayerBullet()) return false;

        double bx = bullet.getCenterX();
        double by = bullet.getCenterY();

        boolean hit = bx >= bossX && bx <= bossX + bossWidth &&
                by >= bossY && by <= bossY + bossHeight;

        if (hit) {
            takeDamage(1);
            bullet.deactivate();
            return true;
        }
        return false;
    }

    // Getters
    public boolean isAlive() { return alive; }
    public List<Bullet> getBullets() { return bullets; }
    public void removeBullet(Bullet bullet) { bullets.remove(bullet); }
    public double getBossX() { return bossX; }
    public double getBossY() { return bossY; }
    public double getBossWidth() { return bossWidth; }
    public double getBossHeight() { return bossHeight; }
}