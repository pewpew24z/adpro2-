package se233.project2.model.boss;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import se233.project2.model.AnimatedSprite;
import se233.project2.model.item.Bullet;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.List;

/**
 * SmallBoss - บอสตัวเล็กที่เดินมาจากขวา
 * - มี 4 frames animation
 * - ยิงไฟแนวตรง (weapon_small-boss sprite sheet 3 frames)
 * - ระยะพ่นไฟสั้น
 */
public class SmallBoss extends Pane {
    private Rectangle healthBar;
    private Rectangle healthBarBg;
    private AnimatedSprite sprite;
    private Circle fallbackCircle;
    private AnimatedSprite weaponEffect; // เอฟเฟกต์พ่นไฟ

    private Image spriteSheet;
    private Image weaponSprite;
    private Image bulletSprite;

    private double x, y;
    private double width = 120;  // ขนาดพอเหมาะ
    private double height = 120;
    private int health;
    private int maxHealth;
    private boolean alive = true;

    // Animation
    private int animationTick = 0;
    private final int ANIMATION_SPEED = 6;

    // Movement (เดินจากขวามาซ้าย)
    private double velocityX = -1.5;  // ช้าๆ เพื่อให้หลบได้
    private double minX;  // ตำแหน่งที่จะหยุด (รับจาก constructor)
    private boolean reachedPosition = false;

    // Shooting
    private List<Bullet> bullets;
    private long lastShoot = 0;
    private long shootInterval = 2_000_000_000; // 2 วินาที (ช้า)

    // Sprite sheet
    private static final int SPRITE_WIDTH = 32;
    private static final int SPRITE_HEIGHT = 32;
    private static final int TOTAL_FRAMES = 4;

    private static final int WEAPON_FRAMES = 3;
    private static final int WEAPON_WIDTH = 32;
    private static final int WEAPON_HEIGHT = 32;

    // Fire range
    private static final double FIRE_RANGE = 250; // ระยะพ่นไฟ (ไม่ไกลมาก)

    public SmallBoss(Image spriteSheet, Image weaponSprite, Image bulletSprite,
                     double x, double y, double minX, int maxHealth) {
        this.x = x;
        this.y = y;
        this.minX = minX;  // รับค่า minX จาก parameter
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.spriteSheet = spriteSheet;
        this.weaponSprite = weaponSprite;
        this.bulletSprite = bulletSprite;
        this.bullets = new ArrayList<>();

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

            // Setup weapon effect (ซ่อนไว้ก่อน)
            if (weaponSprite != null) {
                weaponEffect = new AnimatedSprite(
                        weaponSprite,
                        WEAPON_FRAMES,
                        WEAPON_FRAMES,
                        0, 0,
                        WEAPON_WIDTH,
                        WEAPON_HEIGHT,
                        WEAPON_WIDTH,
                        WEAPON_HEIGHT
                );
                weaponEffect.setFitWidth(80);
                weaponEffect.setFitHeight(80);
                weaponEffect.setPreserveRatio(true);
                weaponEffect.setSmooth(false);
                weaponEffect.setTranslateX(-60); // ออกมาทางซ้าย (ปาก)
                weaponEffect.setTranslateY(height / 2 - 40);
                weaponEffect.setVisible(false);
                this.getChildren().add(weaponEffect);
            }
        } else {
            // Fallback circle
            fallbackCircle = new Circle(width / 2, Color.PURPLE);
            this.getChildren().add(fallbackCircle);
        }
    }

    public void update(long now) {
        if (!alive) return;

        // Movement: เดินจากขวามาซ้ายจนถึง position
        if (!reachedPosition) {
            x += velocityX;
            if (x <= minX) {
                x = minX;
                reachedPosition = true;
                velocityX = 0;
            }
            this.setTranslateX(x);
        }

        // Update animation
        updateAnimation();

        // Shooting (ยิงช้าๆ)
        if (reachedPosition && now - lastShoot > shootInterval) {
            shoot();
            lastShoot = now;
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

            // Animate weapon effect too if visible
            if (weaponEffect != null && weaponEffect.isVisible()) {
                weaponEffect.tick();
            }
        }
    }

    private void shoot() {
        // Show weapon effect
        if (weaponEffect != null) {
            weaponEffect.setVisible(true);
            weaponEffect.reset();
        }

        // Create fire bullet (แนวตรงไปทางซ้าย)
        double bulletX = x - 40;  // ออกจากปาก
        double bulletY = y + height / 2;

        Bullet bullet = new Bullet(
                bulletSprite,
                bulletX,
                bulletY,
                -6,  // ไปทางซ้ายเร็วปานกลาง
                0,   // แนวตรง
                false // boss bullet
        );

        bullets.add(bullet);

        // Hide weapon effect after a short delay
        new Thread(() -> {
            try {
                Thread.sleep(300);
                if (weaponEffect != null) {
                    weaponEffect.setVisible(false);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
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

    public boolean checkPlayerCollision(double playerX, double playerY,
                                        double playerWidth, double playerHeight) {
        if (!alive) return false;

        return x < playerX + playerWidth &&
                x + width > playerX &&
                y < playerY + playerHeight &&
                y + height > playerY;
    }

    // Getters
    public boolean isAlive() { return alive; }
    public List<Bullet> getBullets() { return bullets; }
    public void removeBullet(Bullet bullet) { bullets.remove(bullet); }
    public double getX() { return x; }
    public double getY() { return y; }
    public double getBossWidth() { return width; }
    public double getBossHeight() { return height; }
    public int getHealth() { return health; }
}