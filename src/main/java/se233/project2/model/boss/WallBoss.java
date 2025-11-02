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

public class WallBoss extends Pane {
    private Rectangle hitbox;
    private Rectangle healthBar;
    private Rectangle healthBarBg;
    private ImageView normalSprite;
    private ImageView deadSprite;

    private double bossX, bossY, bossWidth, bossHeight;
    private int health;
    private int maxHealth;
    private boolean alive = true;

    private long lastBossShoot = 0;
    private Random random;
    private List<Bullet> bossBullets;
    private Image bossBulletSprite;

    // ⭐ ตำแหน่งยิงคงที่ 2 จุด
    private static final double SHOOT_POS_1_X = 1032;
    private static final double SHOOT_POS_1_Y = 371;
    private static final double SHOOT_POS_2_X = 1068;
    private static final double SHOOT_POS_2_Y = 373;
    private boolean useFirstPosition = true; // สลับกันยิง

    private static final int HEALTH_BAR_WIDTH = 200;
    private static final int HEALTH_BAR_HEIGHT = 15;

    public WallBoss(double x, double y, double width, double height, int maxHealth,
                    Image normalImage, Image deadImage, Image bulletSprite) {
        this.bossX = x;
        this.bossY = y;
        this.bossWidth = width;
        this.bossHeight = height;
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.random = new Random();
        this.bossBullets = new ArrayList<>();
        this.bossBulletSprite = bulletSprite;

        // Normal sprite
        if (normalImage != null) {
            normalSprite = new ImageView(normalImage);
            normalSprite.setFitWidth(bossWidth);
            normalSprite.setFitHeight(bossHeight);
            normalSprite.setPreserveRatio(false);
        }

        // Dead sprite
        if (deadImage != null) {
            deadSprite = new ImageView(deadImage);
            deadSprite.setFitWidth(bossWidth);
            deadSprite.setFitHeight(bossHeight);
            deadSprite.setPreserveRatio(false);
            deadSprite.setVisible(false);
        }

        // Hitbox (invisible)
        hitbox = new Rectangle(bossWidth, bossHeight);
        hitbox.setFill(Color.TRANSPARENT);

        // Health bar
        healthBarBg = new Rectangle(HEALTH_BAR_WIDTH, HEALTH_BAR_HEIGHT, Color.DARKGRAY);
        healthBarBg.setTranslateY(-25);

        healthBar = new Rectangle(HEALTH_BAR_WIDTH, HEALTH_BAR_HEIGHT, Color.RED);
        healthBar.setTranslateY(-25);

        if (normalSprite != null) this.getChildren().add(normalSprite);
        if (deadSprite != null) this.getChildren().add(deadSprite);
        this.getChildren().addAll(hitbox, healthBarBg, healthBar);

        this.setTranslateX(bossX);
        this.setTranslateY(bossY);
    }

    public void update(long now) {
        if (!alive) return;

        // Random shooting (every 1-2 seconds)
        if (now - lastBossShoot > 1_000_000_000 + random.nextInt(1_000_000_000)) {
            shoot();
            lastBossShoot = now;
        }

        // Update bullets
        List<Bullet> toRemove = new ArrayList<>();
        for (Bullet b : bossBullets) {
            b.update();
            if (!b.isActive()) toRemove.add(b);
        }
        bossBullets.removeAll(toRemove);
    }

    private void shoot() {
        // ⭐ ยิงจาก 2 ตำแหน่งที่กำหนด (สลับกัน)
        double bulletX, bulletY;

        if (useFirstPosition) {
            bulletX = SHOOT_POS_1_X;
            bulletY = SHOOT_POS_1_Y;
            System.out.println("🔫 WallBoss shooting from Position 1: (" + bulletX + ", " + bulletY + ")");
        } else {
            bulletX = SHOOT_POS_2_X;
            bulletY = SHOOT_POS_2_Y;
            System.out.println("🔫 WallBoss shooting from Position 2: (" + bulletX + ", " + bulletY + ")");
        }

        // สลับตำแหน่งสำหรับครั้งถัดไป
        useFirstPosition = !useFirstPosition;

        // Create bullet with gravity (projectile)
        Bullet bullet = new Bullet(
                bossBulletSprite,
                bulletX,
                bulletY,
                false,  // ยิงไปทางซ้าย
                false   // boss bullet
        );

        // Add initial vertical velocity (เล็กน้อย เพื่อให้ตกแบบ projectile)
        bullet.setVerticalSpeed(-2 + random.nextDouble() * 2); // -2 to 0 (ตกช้าๆ)

        bossBullets.add(bullet);
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

            // Switch to dead sprite
            if (normalSprite != null) normalSprite.setVisible(false);
            if (deadSprite != null) deadSprite.setVisible(true);

            System.out.println("Wall Boss defeated!");
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

    // Getters (ใช้ชื่อที่ไม่ชนกับ Pane)
    public boolean isAlive() { return alive; }
    public List<Bullet> getBossBullets() { return bossBullets; }
    public void removeBullet(Bullet bullet) { bossBullets.remove(bullet); }
    public double getBossX() { return bossX; }
    public double getBossY() { return bossY; }
    public double getBossWidth() { return bossWidth; }
    public double getBossHeight() { return bossHeight; }
}