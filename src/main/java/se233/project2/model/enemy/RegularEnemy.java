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

public class RegularEnemy extends Pane {
    private AnimatedSprite sprite;
    private Circle fallbackCircle;
    private Image spriteSheet;
    private Image bulletSprite;

    private double x, y;
    private double width = 80;  // ⭐ เพิ่มจาก 40 → 80
    private double height = 80;
    private int health;
    private int maxHealth;
    private boolean alive = true;

    // Animation
    private int animationTick = 0;
    private final int ANIMATION_SPEED = 8;

    // Shooting
    private List<Bullet> bullets;
    private long lastShoot = 0;
    private long shootInterval = 1_500_000_000; // 1.5 seconds
    private Random random;

    // Sprite sheet dimensions (⭐ 3 frames ไม่ใช่ 4)
    private static final int SPRITE_WIDTH = 32;
    private static final int SPRITE_HEIGHT = 32;
    private static final int TOTAL_FRAMES = 3;  // ⭐ แก้จาก 4 → 3

    // Player reference (for aiming)
    private double playerX = 0;
    private double playerY = 0;

    public RegularEnemy(Image spriteSheet, Image bulletSprite, double x, double y,
                        double minX, double maxX, double minY, double maxY, int maxHealth) {
        this.x = x;
        this.y = y;
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
            // ⭐ สร้าง AnimatedSprite (4 frames)
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
            // Fallback: red circle
            fallbackCircle = new Circle(width / 2, Color.RED);
            this.getChildren().add(fallbackCircle);
        }
    }

    public void update(long now) {
        if (!alive) return;

        // ⭐ ลอยอยู่กับที่ (ไม่เคลื่อนที่)
        // Update animation
        updateAnimation();

        // ⭐ ยิงเล็งไปที่ player
        if (now - lastShoot > shootInterval) {
            shoot();
            lastShoot = now;
            shootInterval = 1_000_000_000L + random.nextInt(1_500_000_000);
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

    private void shoot() {
        double bulletX = x + width / 2;
        double bulletY = y + height;

        // ⭐ คำนวณทิศทางไปหา player
        double dx = playerX - bulletX;
        double dy = playerY - bulletY;
        double distance = Math.sqrt(dx * dx + dy * dy);

        // Normalize และกำหนดความเร็ว
        double speed = 5;
        double vx = (dx / distance) * speed;
        double vy = (dy / distance) * speed;

        // ✅ สร้างกระสุน (ของศัตรู → ไม่ใช่กระสุน player)
        Bullet bullet = new Bullet(
                bulletSprite,
                bulletX,
                bulletY,
                false,  // ไม่จำเป็นต้อง movingRight
                false   // กระสุนศัตรู
        );

        // ✅ ตั้งค่าความเร็วให้กระสุน
        bullet.setVelocity(vx, vy);

        // ✅ เพิ่มกระสุนลงในลิสต์
        bullets.add(bullet);
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

    // ⭐ Update player position (เรียกจาก GameStage)
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