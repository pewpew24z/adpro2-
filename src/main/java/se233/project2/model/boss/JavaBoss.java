package se233.project2.model.boss;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import se233.project2.model.AnimatedSprite;
import se233.project2.model.item.Bullet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * JavaBoss - Stage 2 boss with animation
 * - โผล่เข้ามาจากมุมขวาบนสุด
 * - sprite sheet 2 frames (ขนาดไม่เท่ากัน)
 * - bullet sprite sheet 4 frames (animated) - ตกลงบนพื้น
 * - อ้าปากตอนยิงเท่านั้น
 */
public class JavaBoss extends Pane {
    private Rectangle hitbox;
    private Rectangle healthBar;
    private Rectangle healthBarBg;
    private ImageView sprite;
    private Image spriteSheet;
    private AnimatedSprite weaponSprite;

    private double bossX;
    private double bossY;
    private double targetX = 1100; // ตำแหน่งเป้าหมาย
    private double targetY = 100;
    private double bossWidth = 200;
    private double bossHeight = 300;
    private int health;
    private int maxHealth;
    private boolean alive = true;

    // Entry animation - เคลื่อนที่เข้ามา
    private boolean isEntering = true;
    private double entrySpeed = 4.0;

    // Animation - 2 frames with different sizes
    private boolean isMouthOpen = false;
    private int mouthOpenTimer = 0;
    private final int MOUTH_OPEN_DURATION = 15; // frames to keep mouth open

    // Shooting
    private long lastShoot = 0;
    private long shootInterval = 1_000_000_000;
    private Random random;
    private List<Bullet> bullets;
    private Image bulletSpriteImage;
    private int shotPattern = 0;

    // Sprite frames - different sizes
    private static final int FRAME1_X = 0;
    private static final int FRAME1_Y = 0;
    private static final int FRAME1_WIDTH = 112;
    private static final int FRAME1_HEIGHT = 113;

    private static final int FRAME2_X = 112;
    private static final int FRAME2_Y = 0;
    private static final int FRAME2_WIDTH = 113;
    private static final int FRAME2_HEIGHT = 113;

    private static final int HEALTH_BAR_WIDTH = 200;
    private static final int HEALTH_BAR_HEIGHT = 15;

    public JavaBoss(double x, double y, double width, double height, int maxHealth,
                    Image normalImage, Image deadImage, Image weaponImage, Image bulletSprite) {
        // เริ่มต้นนอกจอขวาบน
        this.bossX = 1400;
        this.bossY = 100;
        this.targetX = x;
        this.targetY = y;
        this.bossWidth = width;
        this.bossHeight = height;
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.bulletSpriteImage = bulletSprite;
        this.spriteSheet = normalImage;
        this.random = new Random();
        this.bullets = new ArrayList<>();

        // Boss sprite - ใช้ ImageView แทน AnimatedSprite เพื่อรองรับ frames ที่มีขนาดต่างกัน
        if (normalImage != null) {
            sprite = new ImageView(normalImage);
            sprite.setFitWidth(bossWidth);
            sprite.setFitHeight(bossHeight);
            sprite.setPreserveRatio(false);
            sprite.setSmooth(false);

            // เริ่มต้นที่ Frame 1 (ปิดปาก)
            sprite.setViewport(new Rectangle2D(FRAME1_X, FRAME1_Y, FRAME1_WIDTH, FRAME1_HEIGHT));
            this.getChildren().add(sprite);
        }

        // Weapon sprite
        if (weaponImage != null) {
            weaponSprite = new AnimatedSprite(
                    weaponImage,
                    1, 1, 0, 0,
                    64, 64, 64, 64
            );
            weaponSprite.setFitWidth(100);
            weaponSprite.setFitHeight(100);
            weaponSprite.setTranslateX(-50);
            weaponSprite.setTranslateY(bossHeight / 2 - 50);
            this.getChildren().add(weaponSprite);
        }

        // Hitbox
        hitbox = new Rectangle(bossWidth, bossHeight);
        hitbox.setFill(Color.TRANSPARENT);

        // Health bar
        healthBarBg = new Rectangle(HEALTH_BAR_WIDTH, HEALTH_BAR_HEIGHT, Color.DARKGRAY);
        healthBarBg.setTranslateY(-25);

        healthBar = new Rectangle(HEALTH_BAR_WIDTH, HEALTH_BAR_HEIGHT, Color.ORANGE);
        healthBar.setTranslateY(-25);

        this.getChildren().addAll(hitbox, healthBarBg, healthBar);

        this.setTranslateX(bossX);
        this.setTranslateY(bossY);
    }

    public void update(long now) {
        if (!alive) return;

        // Entry animation - เคลื่อนที่เข้ามาจากขวา
        if (isEntering) {
            if (bossX > targetX) {
                bossX -= entrySpeed;
                if (bossX <= targetX) {
                    bossX = targetX;
                    isEntering = false;
                }
                this.setTranslateX(bossX);
            } else {
                isEntering = false;
            }
            return; // ยังไม่ยิงในระหว่างเข้ามา
        }

        // จัดการ animation ของปาก
        if (isMouthOpen) {
            mouthOpenTimer++;
            if (mouthOpenTimer >= MOUTH_OPEN_DURATION) {
                closeMouth();
            }
        }

        // Shooting with patterns (หลังจากเข้ามาถึงแล้ว)
        if (now - lastShoot > shootInterval) {
            openMouth(); // อ้าปากก่อนยิง
            shootPattern();
            lastShoot = now;
            shotPattern = random.nextInt(3);
            shootInterval = 800_000_000L + random.nextInt(700_000_000);
        }

        // Update bullets
        Iterator<Bullet> it = bullets.iterator();
        while (it.hasNext()) {
            Bullet b = it.next();
            b.update();
            if (!b.isActive()) {
                it.remove();
            }
        }
    }

    /**
     * เปิดปาก (เปลี่ยนเป็น Frame 2)
     */
    private void openMouth() {
        if (sprite != null && !isMouthOpen) {
            sprite.setViewport(new Rectangle2D(FRAME2_X, FRAME2_Y, FRAME2_WIDTH, FRAME2_HEIGHT));
            isMouthOpen = true;
            mouthOpenTimer = 0;
        }
    }

    /**
     * ปิดปาก (กลับไปที่ Frame 1)
     */
    private void closeMouth() {
        if (sprite != null && isMouthOpen) {
            sprite.setViewport(new Rectangle2D(FRAME1_X, FRAME1_Y, FRAME1_WIDTH, FRAME1_HEIGHT));
            isMouthOpen = false;
            mouthOpenTimer = 0;
        }
    }

    private void shootPattern() {
        double bulletX = bossX;
        double bulletY = bossY + bossHeight / 2;

        switch (shotPattern) {
            case 0: // Single - ยิงแบบโค้ง (มี gravity)
                createBullet(bulletX, bulletY, -7, -2);
                break;

            case 1: // Spread (3) - ยิงแบบโค้งกระจาย
                createBullet(bulletX, bulletY - 20, -7, -3);
                createBullet(bulletX, bulletY, -7, -2);
                createBullet(bulletX, bulletY + 20, -7, -1);
                break;

            case 2: // Rapid (5) - ยิงรัวแบบโค้ง
                for (int i = 0; i < 5; i++) {
                    createBullet(bulletX, bulletY + (i * 15 - 30), -7, -2);
                }
                break;
        }
    }

    private void createBullet(double x, double y, double vx, double vy) {
        AnimatedBullet bullet = new AnimatedBullet(bulletSpriteImage, x, y, vx, vy);
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
            if (sprite != null) sprite.setVisible(false);
            if (weaponSprite != null) weaponSprite.setVisible(false);
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

    /**
     * AnimatedBullet - Inner class สำหรับกระสุนที่มี animation (4 frames)
     * bullet-java-boss.png: 4 frames แนวนอน
     * - Frame 1: x=0, y=0, width=24, height=31
     * - Frame 2: x=25, y=0, width=24, height=31
     * - Frame 3: x=50, y=0, width=24, height=31
     * - Frame 4: x=75, y=0, width=24, height=31
     */
    private class AnimatedBullet extends Bullet {
        private AnimatedSprite bulletSprite;
        private int animTick = 0;
        private final int ANIM_SPEED = 3;

        public AnimatedBullet(Image sprite, double x, double y, double vx, double vy) {
            super(sprite, x, y, vx, vy, false);

            if (sprite != null) {
                this.getChildren().clear();
                // bullet-java-boss.png: 4 frames, แต่ละ frame 24x31, spacing = 25 (มีช่องว่าง 1px)
                bulletSprite = new AnimatedSprite(
                        sprite,
                        4,      // totalColumns = 4 frames
                        4,      // frameCount = 4 frames
                        0,      // offsetX = 0
                        0,      // offsetY = 0
                        24,     // width = 24
                        31,     // height = 31
                        25,     // spacingX = 25 (24 + 1px border)
                        31      // spacingY = 31
                );
                bulletSprite.setFitWidth(36);
                bulletSprite.setFitHeight(46);
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