package se233.project2.model.enemy;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import se233.project2.model.AnimatedSprite;
import se233.project2.model.item.Bullet;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SecondTierEnemy extends Pane {
    private AnimatedSprite sprite;
    private Circle fallbackCircle;
    private Image spriteSheet;
    private Image bulletSprite;

    private double x, y;
    private double width = 100;  // ⭐ ใหญ่กว่า regular (80 → 100)
    private double height = 100;
    private int health;
    private int maxHealth;
    private boolean alive = true;

    // Animation
    private int animationTick = 0;
    private final int ANIMATION_SPEED = 6; // เร็วกว่า regular นิดหน่อย

    // Movement (ลอยช้าๆ)
    private double velocityX = 1;
    private double velocityY = 0.5;
    private double minX, maxX, minY, maxY;

    // Shooting
    private List<Bullet> bullets;
    private long lastShoot = 0;
    private long shootInterval = 1_200_000_000;
    private Random random;

    // Sprite sheet
    private static final int SPRITE_WIDTH = 32;
    private static final int SPRITE_HEIGHT = 32;
    private static final int TOTAL_FRAMES = 4;

    // Player reference
    private double playerX = 0;
    private double playerY = 0;

    public SecondTierEnemy(Image spriteSheet, Image bulletSprite, double x, double y,
                           double minX, double maxX, double minY, double maxY, int maxHealth) {
        this.x = x;
        this.y = y;
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.spriteSheet = spriteSheet;
        this.bulletSprite = bulletSprite;
        this.bullets = new ArrayList<>();
        this.random = new Random();

        setupSprite();
        this.setTranslateX(x);
        this.setTranslateY(y);
    }

    private void setupSprite() {
        if (spriteSheet != null) {
            sprite = new AnimatedSprite(
                    spriteSheet,
                    TOTAL_FRAMES,
                    TOTAL_FRAMES,
                    0, 0,
                    SPRITE_WIDTH,
                    SPRITE_HEIGHT,
                    SPRITE_WIDTH,
                    SPRITE_HEIGHT
            );

            sprite.setFitWidth(width);
            sprite.setFitHeight(height);
            sprite.setPreserveRatio(true);
            sprite.setSmooth(false);
            this.getChildren().add(sprite);
        } else {
            fallbackCircle = new Circle(width / 2, Color.DARKRED);
            this.getChildren().add(fallbackCircle);
        }
    }

    public void update(long now) {
        if (!alive) return;

        // ⭐ ลอยช้าๆ (เคลื่อนไหวนิดหน่อย)
        x += velocityX;
        y += velocityY;

        if (x <= minX || x >= maxX - width) {
            velocityX = -velocityX;
        }
        if (y <= minY || y >= maxY - height) {
            velocityY = -velocityY;
        }

        this.setTranslateX(x);
        this.setTranslateY(y);

        // Update animation
        updateAnimation();

        // Shooting pattern
        if (now - lastShoot > shootInterval) {
            shootPattern();
            lastShoot = now;
            shootInterval = 800_000_000L + random.nextInt(1_200_000_000);
        }

        // Update bullets
        List<Bullet> toRemove = new ArrayList<>();
        for (Bullet bullet : bullets) {
            bullet.update();
            if (!bullet.isActive()) {
                toRemove.add(bullet);
            }
        }
        bullets.removeAll(toRemove);
    }

    private void updateAnimation() {
        if (sprite == null) return;

        animationTick++;
        if (animationTick >= ANIMATION_SPEED) {
            animationTick = 0;
            sprite.tick();
        }
    }

    private void shootPattern() {
        int pattern = random.nextInt(3);

        switch (pattern) {
            case 0:
                shootAimed(); // เล็งไปที่ player
                break;
            case 1:
                shootSpread(); // กระจาย 3 ทาง
                break;
            case 2:
                shootRapid(); // ยิงเร็ว 2 นัด
                break;
        }
    }

    private void shootAimed() {
        double bulletX = x + width / 2;
        double bulletY = y + height;

        double dx = playerX - bulletX;
        double dy = playerY - bulletY;
        double distance = Math.sqrt(dx * dx + dy * dy);

        double speed = 6;
        double vx = (dx / distance) * speed;
        double vy = (dy / distance) * speed;

        bullets.add(new Bullet(bulletSprite, bulletX, bulletY, vx, vy, false));
    }

    private void shootSpread() {
        double bulletX = x + width / 2;
        double bulletY = y + height;

        // Center
        double dx = playerX - bulletX;
        double dy = playerY - bulletY;
        double distance = Math.sqrt(dx * dx + dy * dy);
        double speed = 6;
        double vx = (dx / distance) * speed;
        double vy = (dy / distance) * speed;

        bullets.add(new Bullet(bulletSprite, bulletX, bulletY, vx, vy, false));

        // Left spread
        bullets.add(new Bullet(bulletSprite, bulletX, bulletY, vx - 2, vy, false));

        // Right spread
        bullets.add(new Bullet(bulletSprite, bulletX, bulletY, vx + 2, vy, false));
    }

    private void shootRapid() {
        shootAimed();
        // Second bullet with slight delay (handled by random interval)
    }

    public void takeDamage(int damage) {
        if (!alive) return;

        health -= damage;
        if (health <= 0) {
            health = 0;
            alive = false;
            this.setVisible(false);
        }
    }

    public boolean checkBulletCollision(Bullet bullet) {
        if (!alive || !bullet.isPlayerBullet()) return false;

        double bx = bullet.getCenterX();
        double by = bullet.getCenterY();

        boolean hit = bx >= x && bx <= x + width &&
                by >= y && by <= y + height;

        if (hit) {
            takeDamage(1);
            bullet.deactivate();
            return true;
        }
        return false;
    }

    public boolean checkPlayerCollision(double playerX, double playerY, double playerWidth, double playerHeight) {
        if (!alive) return false;

        return x < playerX + playerWidth &&
                x + width > playerX &&
                y < playerY + playerHeight &&
                y + height > playerY;
    }

    public void setPlayerPosition(double playerX, double playerY) {
        this.playerX = playerX;
        this.playerY = playerY;
    }

    // Getters
    public boolean isAlive() { return alive; }
    public List<Bullet> getBullets() { return bullets; }
    public void removeBullet(Bullet bullet) { bullets.remove(bullet); }
    public double getX() { return x; }
    public double getY() { return y; }
    public double getEnemyWidth() { return width; }
    public double getEnemyHeight() { return height; }
    public int getHealth() { return health; }
}