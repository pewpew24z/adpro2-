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
 * SmallBoss - บอสตัวเล็กที่กระโดดมาโจมตี
 * ใช้ small-boss2.png (sprite sheet 4 frames)
 */
public class SmallBoss extends Pane {
    private AnimatedSprite sprite;
    private AnimatedSprite weaponEffect;
    private Circle fallbackCircle;

    private Image spriteSheet;
    private Image weaponSprite;
    private Image bulletSprite;

    private double x, y;
    private double width = 120;
    private double height = 120;
    private int health;
    private int maxHealth;
    private boolean alive = true;

    // Animation
    private int animationTick = 0;
    private final int ANIMATION_SPEED = 6;

    // Movement - กระโดดมาหา player
    private double velocityX = 0;
    private double velocityY = 0;
    private double targetX = 400;
    private boolean hasJumped = false;
    private boolean onGround = false;
    private final double GRAVITY = 0.8;
    private final double JUMP_FORCE = -15;
    private final double GROUND_Y = 540;

    // Shooting
    private List<Bullet> bullets;
    private long lastShoot = 0;
    private long shootInterval = 2_000_000_000;

    // Sprite sheet (4 frames)
    // small-boss2.png: 4 frames แนวนอน
    // Frame 1: x=0, y=0, 38x56
    // Frame 2: x=47, y=0, 38x56
    // Frame 3: x=94, y=0, 38x56
    // Frame 4: x=140, y=0, 38x56
    private static final int SPRITE_WIDTH = 38;
    private static final int SPRITE_HEIGHT = 56;
    private static final int SPRITE_SPACING = 47;  // distance between frames
    private static final int TOTAL_FRAMES = 4;

    private static final int WEAPON_FRAMES = 4;
    private static final int WEAPON_WIDTH = 32;
    private static final int WEAPON_HEIGHT = 32;

    public SmallBoss(Image spriteSheet, Image weaponSprite, Image bulletSprite,
                     double x, double y, double targetX, int maxHealth) {
        this.x = x;
        this.y = y;
        this.targetX = targetX;
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
                    0,                  // offsetX
                    0,                  // offsetY
                    SPRITE_WIDTH,       // 38
                    SPRITE_HEIGHT,      // 56
                    SPRITE_SPACING,     // 47 (spacing between frames)
                    SPRITE_HEIGHT       // 56
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
            fallbackCircle = new Circle(width / 2, Color.PURPLE);
            this.getChildren().add(fallbackCircle);
        }
    }

    public void update(long now) {
        if (!alive) return;

        // Jump towards target
        if (!hasJumped) {
            velocityX = (targetX - x) / 60;
            velocityY = JUMP_FORCE;
            hasJumped = true;
            onGround = false;
        }

        // Apply gravity
        if (!onGround) {
            velocityY += GRAVITY;
            if (velocityY > 15) velocityY = 15;
        }

        // Update position
        x += velocityX;
        y += velocityY;

        // Check ground collision
        if (y >= GROUND_Y) {
            y = GROUND_Y;
            velocityY = 0;
            velocityX = 0;
            onGround = true;
        }

        this.setTranslateX(x);
        this.setTranslateY(y);

        // Update animation
        updateAnimation();

        // Shooting
        if (onGround && now - lastShoot > shootInterval) {
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

            if (weaponEffect != null && weaponEffect.isVisible()) {
                weaponEffect.tick();
            }
        }
    }

    private void shoot() {
        if (weaponEffect != null) {
            weaponEffect.setVisible(true);
            weaponEffect.reset();
        }

        double bulletX = x - 40;
        double bulletY = y + height / 2;

        Bullet bullet = new Bullet(
                bulletSprite,
                bulletX,
                bulletY,
                -6,
                0,
                false
        );

        bullets.add(bullet);

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