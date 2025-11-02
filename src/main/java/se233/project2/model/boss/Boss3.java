package se233.project2.model.boss;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import se233.project2.model.AnimatedSprite;
import se233.project2.model.item.Bullet;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.List;

/**
 * Boss3 (Last Boss) - บอสตัวสุดท้ายของ Stage 3
 * - อยู่นิ่งที่ตำแหน่งขวาสุด
 * - ยิงกระสุนเล็งไปที่ player โดยตรง (animated bullet)
 * - กระสุนที่ตกพื้นจะ Boom
 */
public class Boss3 extends Pane {
    private AnimatedSprite sprite;
    private Circle fallbackCircle;
    private AnimatedSprite weaponEffect;

    private Image spriteSheet;
    private Image weaponSprite;
    private Image bulletSprite;

    private double x, y;
    private double width = 150;   // ลดขนาดจาก 180 → 150
    private double height = 150;  // ลดขนาดจาก 180 → 150
    private int health;
    private int maxHealth;
    private boolean alive = true;

    // Animation
    private int animationTick = 0;
    private final int ANIMATION_SPEED = 6;

    // Shooting
    private List<AnimatedBullet> bullets;
    private long lastShoot = 0;
    private long shootInterval = 1_200_000_000; // 1.2 วินาที

    // Player tracking
    private double playerX = 0;
    private double playerY = 0;

    // Sprite sheet
    private static final int SPRITE_WIDTH = 32;
    private static final int SPRITE_HEIGHT = 32;
    private static final int TOTAL_FRAMES = 4;

    private static final int WEAPON_FRAMES = 3;
    private static final int WEAPON_WIDTH = 32;
    private static final int WEAPON_HEIGHT = 32;

    public Boss3(Image spriteSheet, Image weaponSprite, Image bulletSprite,
                 double x, double y, int maxHealth) {
        this.x = x;
        this.y = y;
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
                weaponEffect.setFitWidth(100);
                weaponEffect.setFitHeight(100);
                weaponEffect.setPreserveRatio(true);
                weaponEffect.setSmooth(false);
                weaponEffect.setTranslateX(-80);
                weaponEffect.setTranslateY(height / 2 - 50);
                weaponEffect.setVisible(false);
                this.getChildren().add(weaponEffect);
            }
        } else {
            fallbackCircle = new Circle(width / 2, Color.DARKRED);
            this.getChildren().add(fallbackCircle);
        }
    }

    public void update(long now) {
        if (!alive) return;

        // ⭐ Boss3 stays stationary (no movement)

        // Update animation
        updateAnimation();

        // ⭐ Shooting - aim at player
        if (now - lastShoot > shootInterval) {
            shootAtPlayer();
            lastShoot = now;
        }

        // Update bullets
        List<AnimatedBullet> toRemove = new ArrayList<>();
        for (AnimatedBullet bullet : bullets) {
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

            if (weaponEffect != null && weaponEffect.isVisible()) {
                weaponEffect.tick();
            }
        }
    }

    private void shootAtPlayer() {
        // Show weapon effect
        if (weaponEffect != null) {
            weaponEffect.setVisible(true);
            weaponEffect.reset();
        }

        // ⭐ ยิงจากตรงกลางท้อง (center of the boss)
        double bulletX = x + (width / 2);
        double bulletY = y + (height / 2);

        // ⭐ Calculate direction to player
        double dx = playerX - bulletX;
        double dy = playerY - bulletY;
        double distance = Math.sqrt(dx * dx + dy * dy);

        // Normalize and set speed
        double speed = 7;
        double vx = (dx / distance) * speed;
        double vy = (dy / distance) * speed;

        // Create animated bullet aiming at player
        AnimatedBullet bullet = new AnimatedBullet(bulletSprite, bulletX, bulletY, vx, vy);
        bullets.add(bullet);

        // Hide weapon effect
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

    public void setPlayerPosition(double playerX, double playerY) {
        this.playerX = playerX;
        this.playerY = playerY;
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
    public List<Bullet> getBullets() {
        // Convert to List<Bullet> for compatibility
        return new ArrayList<>(bullets);
    }
    public void removeBullet(Bullet bullet) { bullets.remove(bullet); }
    public double getX() { return x; }
    public double getY() { return y; }
    public double getBossWidth() { return width; }
    public double getBossHeight() { return height; }
    public int getHealth() { return health; }

    /**
     * AnimatedBullet - Inner class สำหรับกระสุนที่มี animation (4 frames)
     * boss3_bullet.png: 4 frames แนวนอน
     */
    private class AnimatedBullet extends Bullet {
        private AnimatedSprite bulletSprite;
        private int animTick = 0;
        private final int ANIM_SPEED = 4;

        public AnimatedBullet(Image sprite, double x, double y, double vx, double vy) {
            super(sprite, x, y, vx, vy, false);

            if (sprite != null) {
                this.getChildren().clear();
                // Assuming 4 frames in boss3_bullet.png
                bulletSprite = new AnimatedSprite(
                        sprite,
                        4,      // totalColumns = 4 frames
                        4,      // frameCount = 4 frames
                        0,      // offsetX = 0
                        0,      // offsetY = 0
                        32,     // width (adjust based on your sprite)
                        32,     // height
                        32,     // spacingX
                        32      // spacingY
                );
                bulletSprite.setFitWidth(40);
                bulletSprite.setFitHeight(40);
                bulletSprite.setPreserveRatio(true);
                bulletSprite.setSmooth(false);
                this.getChildren().add(bulletSprite);
            }
        }

        @Override
        public void update() {
            super.update();

            if (bulletSprite != null) {
                animTick++;
                if (animTick >= ANIM_SPEED) {
                    animTick = 0;
                    bulletSprite.tick();
                }
            }
        }
    }
}