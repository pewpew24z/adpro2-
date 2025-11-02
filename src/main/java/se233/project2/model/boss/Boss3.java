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
 * - แข็งแกร่งกว่า SmallBoss
 * - ยิงกระสุนหลายทิศทาง (pattern attack)
 * - เคลื่อนที่แบบซิกแซก
 */
public class Boss3 extends Pane {
    private AnimatedSprite sprite;
    private Circle fallbackCircle;
    private AnimatedSprite weaponEffect;

    private Image spriteSheet;
    private Image weaponSprite;
    private Image bulletSprite;

    private double x, y;
    private double width = 180;  // ใหญ่กว่า SmallBoss
    private double height = 180;
    private int health;
    private int maxHealth;
    private boolean alive = true;

    // Animation
    private int animationTick = 0;
    private final int ANIMATION_SPEED = 6;

    // Movement (เดินจากขวามาซ้าย แล้วซิกแซก)
    private double velocityX = -2.0;
    private double velocityY = 2.0;
    private double minX = 700;  // หยุดตรงนี้แล้วเริ่มซิกแซก
    private double minY = 150;
    private double maxY = 450;
    private boolean reachedPosition = false;

    // Shooting
    private List<Bullet> bullets;
    private long lastShoot = 0;
    private long shootInterval = 1_500_000_000; // 1.5 วินาที (เร็วกว่า SmallBoss)
    private int shootPattern = 0; // 0=straight, 1=triple, 2=spread

    // Sprite sheet (4 frames เหมือน SmallBoss หรือปรับได้)
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
            // Fallback: วงกลมสีแดงเข้ม
            fallbackCircle = new Circle(width / 2, Color.DARKRED);
            this.getChildren().add(fallbackCircle);
        }
    }

    public void update(long now) {
        if (!alive) return;

        // Movement: เดินจากขวามาซ้ายก่อน
        if (!reachedPosition) {
            x += velocityX;
            if (x <= minX) {
                x = minX;
                reachedPosition = true;
            }
            this.setTranslateX(x);
        } else {
            // ซิกแซกขึ้นลง
            y += velocityY;
            if (y <= minY || y >= maxY) {
                velocityY = -velocityY;
            }
            this.setTranslateY(y);
        }

        // Update animation
        updateAnimation();

        // Shooting (pattern attack)
        if (reachedPosition && now - lastShoot > shootInterval) {
            shootPattern();
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

            if (weaponEffect != null && weaponEffect.isVisible()) {
                weaponEffect.tick();
            }
        }
    }

    private void shootPattern() {
        // Show weapon effect
        if (weaponEffect != null) {
            weaponEffect.setVisible(true);
            weaponEffect.reset();
        }

        double bulletX = x - 60;
        double bulletY = y + height / 2;

        // Cycle through patterns: straight → triple → spread
        if (shootPattern == 0) {
            // Straight shot
            bullets.add(new Bullet(bulletSprite, bulletX, bulletY, -7, 0, false));
        } else if (shootPattern == 1) {
            // Triple shot (straight + diagonal)
            bullets.add(new Bullet(bulletSprite, bulletX, bulletY, -7, 0, false));
            bullets.add(new Bullet(bulletSprite, bulletX, bulletY, -6, -2, false));
            bullets.add(new Bullet(bulletSprite, bulletX, bulletY, -6, 2, false));
        } else {
            // Spread shot (5 directions)
            bullets.add(new Bullet(bulletSprite, bulletX, bulletY, -7, 0, false));
            bullets.add(new Bullet(bulletSprite, bulletX, bulletY, -6, -3, false));
            bullets.add(new Bullet(bulletSprite, bulletX, bulletY, -6, 3, false));
            bullets.add(new Bullet(bulletSprite, bulletX, bulletY, -5, -5, false));
            bullets.add(new Bullet(bulletSprite, bulletX, bulletY, -5, 5, false));
        }

        shootPattern = (shootPattern + 1) % 3;

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