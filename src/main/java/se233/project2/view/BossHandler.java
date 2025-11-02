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

    public void spawnSmallBossWave() {
        if (smallBossWave >= 3) return;

        double bossY = 460;
        double[] xPositions = {1350, 1450};
        double[] minXPositions = {
                smallBossWave == 0 ? 150 : (smallBossWave == 1 ? 550 : 950),
                smallBossWave == 0 ? 230 : (smallBossWave == 1 ? 630 : 1030)
        };

        for (int i = 0; i < 2; i++) {
            SmallBoss boss = new SmallBoss(
                    smallBossSprite, smallBossWeaponSprite, smallBossBulletSprite,
                    xPositions[i], bossY, minXPositions[i], 10
            );
            smallBosses.add(boss);
            gamePane.getChildren().add(boss);
        }

        smallBossWave++;
    }

    public void spawnBoss3() {
        clearAll();
        boss3 = new Boss3(boss3Sprite, boss3WeaponSprite, boss3BulletSprite,
                1200, 300, 50);
        gamePane.getChildren().add(boss3);
    }

    public void update(long now) {
        if (wallBoss != null && wallBoss.isAlive()) wallBoss.update(now);
        if (javaBoss != null && javaBoss.isAlive()) javaBoss.update(now);
        for (SmallBoss boss : smallBosses) {
            if (boss.isAlive()) boss.update(now);
        }
        if (boss3 != null && boss3.isAlive()) boss3.update(now);
    }

    public void updateBossBullets(List<Explosion> explosions, Image explosionSprite, int groundY) {
        if (wallBoss != null) {
            updateWallBossBullets(wallBoss, explosions, explosionSprite, groundY);
        }

        if (javaBoss != null) {
            updateGenericBossBullets(javaBoss.getBullets(), explosions, explosionSprite, groundY);
        }

        for (SmallBoss boss : smallBosses) {
            updateGenericBossBullets(boss.getBullets(), explosions, explosionSprite, groundY);
        }

        if (boss3 != null) {
            updateGenericBossBullets(boss3.getBullets(), explosions, explosionSprite, groundY);
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

            if (hit1 || hit2 || bullet.checkGroundCollision(groundY) || !bullet.isActive()) {
                if (hit1 || hit2) {
                    createExplosion(bx, by, explosions, explosionSprite);
                }
                gamePane.getChildren().remove(bullet);
                it.remove();
            }
        }
    }

    private void updateGenericBossBullets(List<Bullet> bullets, List<Explosion> explosions, Image explosionSprite, int groundY) {
        // Stage 2 Platform collision area
        final double PLATFORM_X = 196;
        final double PLATFORM_Y = 503;
        final double PLATFORM_WIDTH = 1084;
        final double PLATFORM_HEIGHT = 217;

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

            if (!bullet.isActive() || bullet.checkGroundCollision(groundY) || hitPlatform) {
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
        if (wallBoss != null) gamePane.getChildren().remove(wallBoss);
        if (javaBoss != null) gamePane.getChildren().remove(javaBoss);
        for (SmallBoss boss : smallBosses) gamePane.getChildren().remove(boss);
        if (boss3 != null) gamePane.getChildren().remove(boss3);

        wallBoss = null;
        javaBoss = null;
        smallBosses.clear();
        boss3 = null;
        smallBossWave = 0;
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
        return smallBossWave < 3 && smallBosses.stream().allMatch(b -> !b.isAlive());
    }

    // Getters
    public WallBoss getWallBoss() { return wallBoss; }
    public JavaBoss getJavaBoss() { return javaBoss; }
    public List<SmallBoss> getSmallBosses() { return smallBosses; }
    public Boss3 getBoss3() { return boss3; }
    public int getSmallBossWave() { return smallBossWave; }
}