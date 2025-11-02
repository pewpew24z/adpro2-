package se233.project2.view;

import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import se233.project2.model.enemy.RegularEnemy;
import se233.project2.model.enemy.SecondTierEnemy;
import se233.project2.model.item.Bullet;
import se233.project2.model.effect.Explosion;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * EnemyHandler - จัดการศัตรูทั้งหมด
 */
public class EnemyHandler {
    private List<RegularEnemy> regularEnemies;
    private List<SecondTierEnemy> secondTierEnemies;
    private Pane gamePane;

    private Image regularEnemySprite;
    private Image regularEnemyBulletSprite;
    private Image secondTierEnemySprite;
    private Image secondTierEnemyBulletSprite;

    public EnemyHandler(Pane gamePane, Image regularSprite, Image regularBullet,
                        Image secondTierSprite, Image secondTierBullet) {
        this.gamePane = gamePane;
        this.regularEnemySprite = regularSprite;
        this.regularEnemyBulletSprite = regularBullet;
        this.secondTierEnemySprite = secondTierSprite;
        this.secondTierEnemyBulletSprite = secondTierBullet;
        this.regularEnemies = new ArrayList<>();
        this.secondTierEnemies = new ArrayList<>();
    }

    public void spawnStage1Enemies() {
        clearAll();
        regularEnemies.add(new RegularEnemy(regularEnemySprite, regularEnemyBulletSprite,
                900, 200, 700, 1230, 100, 400, 3));
        regularEnemies.add(new RegularEnemy(regularEnemySprite, regularEnemyBulletSprite,
                1000, 300, 700, 1230, 100, 500, 3));
        regularEnemies.add(new RegularEnemy(regularEnemySprite, regularEnemyBulletSprite,
                850, 400, 700, 1230, 200, 500, 3));

        for (RegularEnemy enemy : regularEnemies) {
            gamePane.getChildren().add(enemy);
        }
    }

    public void spawnStage2Enemies() {
        clearAll();
        secondTierEnemies.add(new SecondTierEnemy(secondTierEnemySprite, secondTierEnemyBulletSprite,
                900, 200, 700, 1230, 100, 400, 5));
        secondTierEnemies.add(new SecondTierEnemy(secondTierEnemySprite, secondTierEnemyBulletSprite,
                1000, 250, 700, 1230, 100, 450, 5));
        secondTierEnemies.add(new SecondTierEnemy(secondTierEnemySprite, secondTierEnemyBulletSprite,
                1100, 300, 700, 1230, 150, 500, 5));
        secondTierEnemies.add(new SecondTierEnemy(secondTierEnemySprite, secondTierEnemyBulletSprite,
                950, 350, 700, 1230, 200, 500, 5));
        secondTierEnemies.add(new SecondTierEnemy(secondTierEnemySprite, secondTierEnemyBulletSprite,
                1050, 150, 700, 1230, 100, 400, 5));

        for (SecondTierEnemy enemy : secondTierEnemies) {
            gamePane.getChildren().add(enemy);
        }
    }

    public void update(long now, double playerX, double playerY) {
        for (RegularEnemy enemy : regularEnemies) {
            if (enemy.isAlive()) {
                enemy.setPlayerPosition(playerX, playerY);
                enemy.update(now);
            }
        }

        for (SecondTierEnemy enemy : secondTierEnemies) {
            if (enemy.isAlive()) {
                enemy.setPlayerPosition(playerX, playerY);
                enemy.update(now);
            }
        }
    }

    public void updateEnemyBullets(List<Explosion> explosions, Image explosionSprite, int groundY) {
        // Regular enemies
        for (RegularEnemy enemy : regularEnemies) {
            Iterator<Bullet> it = enemy.getBullets().iterator();
            while (it.hasNext()) {
                Bullet bullet = it.next();

                if (!gamePane.getChildren().contains(bullet)) {
                    gamePane.getChildren().add(bullet);
                }

                if (!bullet.isActive() || bullet.checkGroundCollision(groundY)) {
                    if (bullet.checkGroundCollision(groundY)) {
                        createExplosion(bullet.getX(), bullet.getY(), explosions, explosionSprite);
                    }
                    gamePane.getChildren().remove(bullet);
                    it.remove();
                }
            }
        }

        // Second-tier enemies
        for (SecondTierEnemy enemy : secondTierEnemies) {
            Iterator<Bullet> it = enemy.getBullets().iterator();
            while (it.hasNext()) {
                Bullet bullet = it.next();

                if (!gamePane.getChildren().contains(bullet)) {
                    gamePane.getChildren().add(bullet);
                }

                if (!bullet.isActive() || bullet.checkGroundCollision(groundY)) {
                    if (bullet.checkGroundCollision(groundY)) {
                        createExplosion(bullet.getX(), bullet.getY(), explosions, explosionSprite);
                    }
                    gamePane.getChildren().remove(bullet);
                    it.remove();
                }
            }
        }
    }

    private void createExplosion(double x, double y, List<Explosion> explosions, Image sprite) {
        Explosion exp = new Explosion(sprite, x, y);
        explosions.add(exp);
        gamePane.getChildren().add(exp);
    }

    public void clearAll() {
        for (RegularEnemy enemy : regularEnemies) {
            gamePane.getChildren().remove(enemy);
        }
        for (SecondTierEnemy enemy : secondTierEnemies) {
            gamePane.getChildren().remove(enemy);
        }
        regularEnemies.clear();
        secondTierEnemies.clear();
    }

    public boolean allCleared() {
        return regularEnemies.stream().allMatch(e -> !e.isAlive()) &&
                secondTierEnemies.stream().allMatch(e -> !e.isAlive());
    }

    public List<RegularEnemy> getRegularEnemies() { return regularEnemies; }
    public List<SecondTierEnemy> getSecondTierEnemies() { return secondTierEnemies; }
}