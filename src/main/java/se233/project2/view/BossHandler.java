package se233.project2.view;

import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import se233.project2.model.boss.*;
import se233.project2.model.item.Bullet;
import se233.project2.model.effect.Explosion;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * BossHandler - จัดการบอสทั้งหมด
 */
public class BossHandler {
    private Pane gamePane;
    private WallBoss wallBoss;
    private JavaBoss javaBoss;
    private List<SmallBoss> smallBosses;
    private List<SmallBoss> activeSmallBosses;  // ⭐ Track currently active small bosses
    private Boss3 boss3;

    private Image wallBossNormalSprite, wallBossDeadSprite, wallBossBulletSprite;
    private Image javaBossNormalSprite, javaBossDeadSprite, javaBossWeaponSprite, javaBossBulletSprite;
    private Image smallBossSprite, smallBossWeaponSprite, smallBossBulletSprite;
    private Image boss3Sprite, boss3WeaponSprite, boss3BulletSprite;

    private int smallBossWave = 0;

    public BossHandler(Pane gamePane,
                       Image wbNormal, Image wbDead, Image wbBullet,
                       Image jbNormal, Image jbDead, Image jbWeapon, Image jbBullet,
                       Image sbSprite, Image sbWeapon, Image sbBullet,
                       Image b3Sprite, Image b3Weapon, Image b3Bullet) {
        this.gamePane = gamePane;
        this.smallBosses = new ArrayList<>();
        this.activeSmallBosses = new ArrayList<>();

        this.wallBossNormalSprite = wbNormal;
        this.wallBossDeadSprite = wbDead;
        this.wallBossBulletSprite = wbBullet;

        this.javaBossNormalSprite = jbNormal;
        this.javaBossDeadSprite = jbDead;
        this.javaBossWeaponSprite = jbWeapon;
        this.javaBossBulletSprite = jbBullet;

        this.smallBossSprite = sbSprite;
        this.smallBossWeaponSprite = sbWeapon;
        this.smallBossBulletSprite = sbBullet;

        this.boss3Sprite = b3Sprite;
        this.boss3WeaponSprite = b3Weapon;
        this.boss3BulletSprite = b3Bullet;
    }

    public void spawnWallBoss() {
        clearAll();
        wallBoss = new WallBoss(1050, 200, 180, 400, 30,
                wallBossNormalSprite, wallBossDeadSprite, wallBossBulletSprite);
        gamePane.getChildren().add(wallBoss);
    }

    public void spawnJavaBoss() {
        clearAll();
        javaBoss = new JavaBoss(1100, 100, 200, 300, 50,
                javaBossNormalSprite, javaBossDeadSprite, javaBossWeaponSprite, javaBossBulletSprite);
        gamePane.getChildren().add(javaBoss);
    }

    // ⭐ Spawn small bosses ONE AT A TIME for each wave
    public void spawnSmallBossWave() {
        if (smallBossWave >= 3) return;

        double startX = 1400;  // Start off-screen right
        double bossY = 460;

        // Target X positions for each wave
        double[] targetPositions = {
                200,   // Wave 1 - land at position 200
                600,   // Wave 2 - land at position 600
                1000   // Wave 3 - land at position 1000
        };

        double targetX = targetPositions[smallBossWave];

        SmallBoss boss = new SmallBoss(
                smallBossSprite, smallBossWeaponSprite, smallBossBulletSprite,
                startX, bossY, targetX, 10
        );

        smallBosses.add(boss);
        activeSmallBosses.add(boss);
        gamePane.getChildren().add(boss);

        System.out.println("🔥 Spawned SmallBoss wave " + (smallBossWave + 1) + " targeting X=" + targetX);

        smallBossWave++;
    }

    public void spawnBoss3() {
        clearAll();
        // Stage 3 platform: y=587
        // Boss3 width=150, height=150
        // Center boss horizontally: x = (1280 - 150) / 2 = 565
        // Place boss on platform: y = 587 - 150 = 437
        boss3 = new Boss3(boss3Sprite, boss3WeaponSprite, boss3BulletSprite,
                565, 437, 50);
        gamePane.getChildren().add(boss3);
    }

    public void update(long now) {
        if (wallBoss != null && wallBoss.isAlive()) wallBoss.update(now);
        if (javaBoss != null && javaBoss.isAlive()) javaBoss.update(now);

        // ⭐ Only update active small bosses
        for (SmallBoss boss : activeSmallBosses) {
            if (boss.isAlive()) boss.update(now);
        }

        if (boss3 != null && boss3.isAlive()) boss3.update(now);
    }

    public void updateBossBullets(List<Explosion> explosions, Image explosionSprite, int groundY) {
        if (wallBoss != null) {
            updateWallBossBullets(wallBoss, explosions, explosionSprite, groundY);
        }

        if (javaBoss != null) {
            updateGenericBossBullets(javaBoss.getBullets(), javaBoss.isAlive(), explosions, explosionSprite, groundY);
        }

        // ⭐ Update small boss bullets with cleanup check
        for (SmallBoss boss : activeSmallBosses) {
            updateGenericBossBullets(boss.getBullets(), boss.isAlive(), explosions, explosionSprite, groundY);
        }

        if (boss3 != null) {
            updateBoss3Bullets(boss3.getBullets(), boss3.isAlive(), explosions, explosionSprite, groundY);
        }
    }

    private void updateWallBossBullets(WallBoss boss, List<Explosion> explosions, Image explosionSprite, int groundY) {
        final double P1_X = 543, P1_Y = 516, P1_W = 168;
        final double P2_X = 716, P2_Y = 552, P2_W = 568;

        Iterator<Bullet> it = boss.getBossBullets().iterator();
        while (it.hasNext()) {
            Bullet bullet = it.next();

            if (!gamePane.getChildren().contains(bullet)) {
                gamePane.getChildren().add(bullet);
            }

            double bx = bullet.getX(), by = bullet.getY();

            boolean hit1 = by >= P1_Y && bx >= P1_X && bx <= P1_X + P1_W;
            boolean hit2 = by >= P2_Y && bx >= P2_X && bx <= P2_X + P2_W;

            // ⭐ Clean up if boss is dead OR bullet hits something
            if (!boss.isAlive() || hit1 || hit2 || bullet.checkGroundCollision(groundY) || !bullet.isActive()) {
                if (hit1 || hit2 || bullet.checkGroundCollision(groundY)) {
                    createExplosion(bx, by, explosions, explosionSprite);
                }
                gamePane.getChildren().remove(bullet);
                it.remove();
            }
        }
    }

    private void updateGenericBossBullets(List<Bullet> bullets, boolean bossAlive,
                                          List<Explosion> explosions, Image explosionSprite, int groundY) {
        // Stage 2 Platform collision area
        final double PLATFORM_X = 196;
        final double PLATFORM_Y = 503;
        final double PLATFORM_WIDTH = 1084;

        Iterator<Bullet> it = bullets.iterator();
        while (it.hasNext()) {
            Bullet bullet = it.next();

            if (!gamePane.getChildren().contains(bullet)) {
                gamePane.getChildren().add(bullet);
            }

            double bx = bullet.getX();
            double by = bullet.getY();

            boolean hitPlatform = by >= PLATFORM_Y &&
                    bx >= PLATFORM_X &&
                    bx <= PLATFORM_X + PLATFORM_WIDTH;

            // ⭐ Clean up if boss is dead OR bullet hits something
            if (!bossAlive || !bullet.isActive() || bullet.checkGroundCollision(groundY) || hitPlatform) {
                if (bullet.checkGroundCollision(groundY) || hitPlatform) {
                    createExplosion(bullet.getX(), bullet.getY(), explosions, explosionSprite);
                }
                gamePane.getChildren().remove(bullet);
                it.remove();
            }
        }
    }

    // ⭐ Boss3 bullets track player and explode on ground
    private void updateBoss3Bullets(List<Bullet> bullets, boolean bossAlive,
                                    List<Explosion> explosions, Image explosionSprite, int groundY) {
        // Stage 3 platform: x=0, y=585, width=1280, height=135
        final double PLATFORM_X = 0;
        final double PLATFORM_Y = 585;
        final double PLATFORM_WIDTH = 1280;

        Iterator<Bullet> it = bullets.iterator();
        while (it.hasNext()) {
            Bullet bullet = it.next();

            if (!gamePane.getChildren().contains(bullet)) {
                gamePane.getChildren().add(bullet);
            }

            double bx = bullet.getX();
            double by = bullet.getY();

            // Check platform collision
            boolean hitPlatform = by >= PLATFORM_Y &&
                    bx >= PLATFORM_X &&
                    bx <= PLATFORM_X + PLATFORM_WIDTH;

            // ⭐ Clean up if boss is dead OR bullet hits something
            if (!bossAlive || !bullet.isActive() || bullet.checkGroundCollision(groundY) || hitPlatform) {
                if (bullet.checkGroundCollision(groundY) || hitPlatform) {
                    createExplosion(bullet.getX(), bullet.getY(), explosions, explosionSprite);
                }
                gamePane.getChildren().remove(bullet);
                it.remove();
            }
        }
    }

    private void createExplosion(double x, double y, List<Explosion> explosions, Image sprite) {
        Explosion exp = new Explosion(sprite, x, y);
        explosions.add(exp);
        gamePane.getChildren().add(exp);
    }

    public void clearAll() {
        if (wallBoss != null) {
            cleanupBossBullets(wallBoss.getBossBullets());
            gamePane.getChildren().remove(wallBoss);
        }
        if (javaBoss != null) {
            cleanupBossBullets(javaBoss.getBullets());
            gamePane.getChildren().remove(javaBoss);
        }
        for (SmallBoss boss : smallBosses) {
            cleanupBossBullets(boss.getBullets());
            gamePane.getChildren().remove(boss);
        }
        if (boss3 != null) {
            cleanupBossBullets(boss3.getBullets());
            gamePane.getChildren().remove(boss3);
        }

        wallBoss = null;
        javaBoss = null;
        smallBosses.clear();
        activeSmallBosses.clear();
        boss3 = null;
        smallBossWave = 0;
    }

    // ⭐ Helper method to clean up bullets
    private void cleanupBossBullets(List<Bullet> bullets) {
        for (Bullet bullet : bullets) {
            gamePane.getChildren().remove(bullet);
        }
        bullets.clear();
    }

    public boolean isCurrentBossDefeated(int stage) {
        if (stage == 1 && wallBoss != null) return !wallBoss.isAlive();
        if (stage == 2 && javaBoss != null) return !javaBoss.isAlive();
        if (stage == 3) {
            boolean allSmallBossesDefeated = smallBosses.stream().allMatch(b -> !b.isAlive());
            if (boss3 != null) return !boss3.isAlive();
            return allSmallBossesDefeated && smallBossWave >= 3;
        }
        return false;
    }

    public boolean shouldSpawnNextSmallBossWave() {
        // ⭐ Spawn next wave only if current wave is defeated and we haven't spawned all 3 waves
        return smallBossWave < 3 && activeSmallBosses.stream().allMatch(b -> !b.isAlive());
    }

    // Getters
    public WallBoss getWallBoss() { return wallBoss; }
    public JavaBoss getJavaBoss() { return javaBoss; }
    public List<SmallBoss> getSmallBosses() { return smallBosses; }
    public List<SmallBoss> getActiveSmallBosses() { return activeSmallBosses; }
    public Boss3 getBoss3() { return boss3; }
    public int getSmallBossWave() { return smallBossWave; }
}