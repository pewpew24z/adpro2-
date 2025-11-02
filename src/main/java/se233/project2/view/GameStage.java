package se233.project2.view;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import se233.project2.controller.Updatable;
import se233.project2.model.GameCharacter;
import se233.project2.model.Keys;
import se233.project2.model.Platform;
import se233.project2.model.effect.Explosion;
import se233.project2.model.item.Bullet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameStage extends Pane implements Updatable {
    public static final int WIDTH = 1280;
    public static final int HEIGHT = 720;
    public static final int GROUND_Y = 668;

    private ImageView backgroundImageView;
    private GameCharacter player;
    private Keys keys;

    // Handlers
    private GameUIHandler uiHandler;
    private EnemyHandler enemyHandler;
    private BossHandler bossHandler;

    private List<Bullet> playerBullets;
    private List<Platform> platforms;
    private List<Explosion> explosions;

    // Sprites
    private Image playerBulletSprite;
    private Image explosionSprite;
    private Image liveIconSprite;

    private long lastShoot = 0;
    private final long SHOOT_DELAY = 200_000_000;

    // Game state
    private int playerLives = 3;
    private int currentStage = 1;
    private boolean minionsCleared = false;
    private boolean bossSpawned = false;
    private boolean bossDefeated = false;
    private boolean stageCleared = false;
    private boolean gameOver = false;
    private int stageClearDelay = 0;
    private int bossSpawnDelay = 0;
    private final int STAGE_CLEAR_WAIT = 180;
    private final int BOSS_SPAWN_WAIT = 120;

    // Stage 3
    private boolean waitingForNextWave = false;
    private int waveDelay = 0;
    private final int WAVE_WAIT = 90;

    public GameStage() {
        this.setPrefWidth(WIDTH);
        this.setPrefHeight(HEIGHT);

        loadAllSprites();
        keys = new Keys();
        playerBullets = new ArrayList<>();
        platforms = new ArrayList<>();
        explosions = new ArrayList<>();

        setupHandlers();
        loadStage(1);

        this.setOnKeyPressed(event -> keys.update(event.getCode(), true));
        this.setOnKeyReleased(event -> keys.update(event.getCode(), false));
        this.setFocusTraversable(true);
    }

    private void loadAllSprites() {
        playerBulletSprite = loadImage("item/bullet-player.png");
        explosionSprite = loadImage("effect/Boom.png");
        liveIconSprite = loadImage("effect/live.png");
    }

    private void setupHandlers() {
        uiHandler = new GameUIHandler(this, liveIconSprite);

        enemyHandler = new EnemyHandler(this,
                loadImage("enemy/regular_enemy.png"),
                loadImage("enemy/bullet_regular-enemy.png"),
                loadImage("enemy/secound-tier_enemy.png"),
                loadImage("enemy/bullet_secound-tier-enemy.png")
        );

        bossHandler = new BossHandler(this,
                loadImage("boss/boss1/wall-boss-normal.png"),
                loadImage("effect/Boom_removebg.png"),
                loadImage("boss/boss1/bullet-wall.png"),
                loadImage("boss/boss2/Java-boss.png"),
                loadImage("effect/Boom_removebg.png"),
                loadImage("boss/boss2/weapon-boss2.png"),
                loadImage("boss/boss2/bullet-java-boss.png"),
                loadImage("enemy/smallboss/small-boss2.png"),
                loadImage("enemy/smallboss/weapon_small-boss.png"),
                loadImage("enemy/smallboss/bullet_small-boss.png"),
                loadImage("boss/boss3/boss3.png"),
                loadImage("boss/boss3/boss3_weapon.png"),
                loadImage("boss/boss3/boss3_bullet.png")
        );
    }

    private void loadStage(int stage) {
        this.getChildren().clear();
        playerBullets.clear();
        platforms.clear();
        explosions.clear();

        enemyHandler.clearAll();
        bossHandler.clearAll();

        minionsCleared = false;
        bossSpawned = false;
        bossDefeated = false;
        stageCleared = false;
        bossSpawnDelay = 0;
        stageClearDelay = 0;
        waitingForNextWave = false;

        currentStage = stage;

        // Background
        loadBackground(stage);
        createPlatforms(stage);

        if (player == null) {
            player = new GameCharacter(100, 100);
        } else {
            this.getChildren().remove(player);
            player = new GameCharacter(100, 100);
        }

        spawnMinions(stage);
        uiHandler.initialize(stage);
        uiHandler.createLiveIcons(playerLives);
        this.getChildren().add(player);
    }

    private void loadBackground(int stage) {
        Image bg = loadImage("stage/Stage" + stage + ".png");
        if (bg != null) {
            backgroundImageView = new ImageView(bg);
            backgroundImageView.setFitWidth(WIDTH);
            backgroundImageView.setFitHeight(HEIGHT);
            backgroundImageView.setPreserveRatio(false);
            this.getChildren().add(backgroundImageView);
        } else {
            this.getChildren().add(new Rectangle(WIDTH, HEIGHT, Color.SKYBLUE));
        }
    }

    private void createPlatforms(int stage) {
        if (stage == 1) {
            platforms.add(new Platform(0, 260, 352, 96));
            platforms.add(new Platform(364, 408, 176, 60));
            platforms.add(new Platform(0, 488, 360, 96));
            platforms.add(new Platform(544, 516, 168, 96));
            platforms.add(new Platform(712, 552, 568, 116));
            platforms.add(new Platform(360, 556, 184, 104));
            platforms.add(new Platform(-100, 670, WIDTH + 200, 100));
        } else if (stage == 2) {
            platforms.add(new Platform(0, 390, 193, 329));
            platforms.add(new Platform(195, 504, 1085, 216));
        } else if (stage == 3) {
            platforms.add(new Platform(50, 580, 280, 60));
            platforms.add(new Platform(480, 580, 280, 60));
            platforms.add(new Platform(900, 580, 280, 60));
            platforms.add(new Platform(0, GROUND_Y, WIDTH, 100));
        }
    }

    private void spawnMinions(int stage) {
        if (stage == 1) {
            enemyHandler.spawnStage1Enemies();
        } else if (stage == 2) {
            enemyHandler.spawnStage2Enemies();
        } else if (stage == 3) {
            minionsCleared = true;
            bossSpawnDelay = BOSS_SPAWN_WAIT;
            uiHandler.updateWaveLabel("Small Bosses Incoming...", Color.YELLOW);
        }
    }

    public void update(long now) {
        if (gameOver) return;

        if (stageCleared) {
            handleStageTransition();
            return;
        }

        checkMinionsCleared();
        handleBossSpawn();
        handleStage3Waves();
        checkBossDefeated();

        // Update game objects
        player.update(keys, platforms);
        if (player.isShooting() && now - lastShoot > SHOOT_DELAY) {
            shootPlayerBullet();
            lastShoot = now;
        }

        enemyHandler.update(now, player.getCenterX(), player.getCenterY());
        bossHandler.update(now);

        updatePlayerBullets();
        enemyHandler.updateEnemyBullets(explosions, explosionSprite, GROUND_Y);
        bossHandler.updateBossBullets(explosions, explosionSprite, GROUND_Y);

        checkEnemyBulletHits();
        checkBossBulletHits();
        updateExplosions();
    }

    private void handleStageTransition() {
        stageClearDelay--;
        if (stageClearDelay <= 0) {
            if (currentStage < 3) {
                loadStage(currentStage + 1);
            } else {
                showGameCompleted();
            }
        }
    }

    private void checkMinionsCleared() {
        if (!minionsCleared && !bossSpawned && enemyHandler.allCleared()) {
            minionsCleared = true;
            bossSpawnDelay = BOSS_SPAWN_WAIT;
            uiHandler.updateWaveLabel("Boss Incoming...", Color.YELLOW);
        }
    }

    private void handleBossSpawn() {
        if (minionsCleared && !bossSpawned) {
            bossSpawnDelay--;
            if (bossSpawnDelay <= 0) {
                spawnBoss(currentStage);
                bossSpawned = true;
                uiHandler.updateWaveLabel("BOSS FIGHT!", Color.RED);
            }
        }
    }

    private void handleStage3Waves() {
        if (currentStage != 3 || !bossSpawned) return;

        if (bossHandler.shouldSpawnNextSmallBossWave() && !waitingForNextWave) {
            waitingForNextWave = true;
            waveDelay = WAVE_WAIT;
        }

        if (waitingForNextWave) {
            waveDelay--;
            if (waveDelay <= 0) {
                bossHandler.spawnSmallBossWave();
                waitingForNextWave = false;
                uiHandler.updateWaveLabel("Wave " + bossHandler.getSmallBossWave() + "/3", Color.ORANGE);
            }
        }

        // Spawn Boss3
        if (bossHandler.getSmallBossWave() >= 3 &&
                bossHandler.getSmallBosses().stream().allMatch(b -> !b.isAlive()) &&
                bossHandler.getBoss3() == null) {
            bossHandler.spawnBoss3();
            uiHandler.updateWaveLabel("FINAL BOSS!", Color.RED);
        }
    }

    private void spawnBoss(int stage) {
        if (stage == 1) {
            bossHandler.spawnWallBoss();
        } else if (stage == 2) {
            bossHandler.spawnJavaBoss();
        } else if (stage == 3) {
            bossHandler.spawnSmallBossWave();
        }
    }

    private void checkBossDefeated() {
        if (bossSpawned && !bossDefeated && bossHandler.isCurrentBossDefeated(currentStage)) {
            onBossDefeated();
        }
    }

    private void shootPlayerBullet() {
        double bulletX = player.getCenterX() + (player.isFacingRight() ? 30 : -30);
        double bulletY = player.getCenterY();

        Bullet bullet = new Bullet(playerBulletSprite, bulletX, bulletY,
                player.isFacingRight(), true);
        playerBullets.add(bullet);
        this.getChildren().add(bullet);
    }

    private void updatePlayerBullets() {
        Iterator<Bullet> it = playerBullets.iterator();
        while (it.hasNext()) {
            Bullet b = it.next();
            b.update();

            if (!b.isActive()) {
                it.remove();
                this.getChildren().remove(b);
                continue;
            }

            if (checkEnemyHit(b) || checkBossHit(b)) {
                it.remove();
                this.getChildren().remove(b);
            }
        }
    }

    private boolean checkEnemyHit(Bullet bullet) {
        for (var enemy : enemyHandler.getRegularEnemies()) {
            if (enemy.checkBulletCollision(bullet)) {
                uiHandler.addScore(1);
                createExplosion(bullet.getX(), bullet.getY());
                return true;
            }
        }

        for (var enemy : enemyHandler.getSecondTierEnemies()) {
            if (enemy.checkBulletCollision(bullet)) {
                uiHandler.addScore(2);
                createExplosion(bullet.getX(), bullet.getY());
                return true;
            }
        }

        return false;
    }

    private boolean checkBossHit(Bullet bullet) {
        if (bossHandler.getWallBoss() != null && bossHandler.getWallBoss().checkBulletCollision(bullet)) {
            uiHandler.addScore(1);
            createExplosion(bullet.getX(), bullet.getY());
            return true;
        }

        if (bossHandler.getJavaBoss() != null && bossHandler.getJavaBoss().checkBulletCollision(bullet)) {
            uiHandler.addScore(1);
            createExplosion(bullet.getX(), bullet.getY());
            return true;
        }

        for (var boss : bossHandler.getSmallBosses()) {
            if (boss.checkBulletCollision(bullet)) {
                uiHandler.addScore(1);
                createExplosion(bullet.getX(), bullet.getY());
                return true;
            }
        }

        if (bossHandler.getBoss3() != null && bossHandler.getBoss3().checkBulletCollision(bullet)) {
            uiHandler.addScore(5);
            createExplosion(bullet.getX(), bullet.getY());
            return true;
        }

        return false;
    }

    private void checkEnemyBulletHits() {
        for (var enemy : enemyHandler.getRegularEnemies()) {
            checkBulletPlayerHit(enemy.getBullets());
        }
        for (var enemy : enemyHandler.getSecondTierEnemies()) {
            checkBulletPlayerHit(enemy.getBullets());
        }
    }

    private void checkBossBulletHits() {
        if (bossHandler.getWallBoss() != null) {
            checkBulletPlayerHit(bossHandler.getWallBoss().getBossBullets());
        }
        if (bossHandler.getJavaBoss() != null) {
            checkBulletPlayerHit(bossHandler.getJavaBoss().getBullets());
        }
        for (var boss : bossHandler.getSmallBosses()) {
            checkBulletPlayerHit(boss.getBullets());
        }
        if (bossHandler.getBoss3() != null) {
            checkBulletPlayerHit(bossHandler.getBoss3().getBullets());
        }
    }

    private void checkBulletPlayerHit(List<Bullet> bullets) {
        Iterator<Bullet> it = bullets.iterator();
        while (it.hasNext()) {
            Bullet b = it.next();
            if (checkPlayerHit(b)) {
                playerHit();
                createExplosion(b.getX(), b.getY());
                this.getChildren().remove(b);
                it.remove();
            }
        }
    }

    private boolean checkPlayerHit(Bullet bullet) {
        double bx = bullet.getCenterX();
        double by = bullet.getCenterY();
        return bx >= player.getX() && bx <= player.getX() + 47 &&
                by >= player.getY() && by <= player.getY() + 36;
    }

    private void playerHit() {
        playerLives--;
        uiHandler.createLiveIcons(playerLives);

        if (playerLives <= 0) {
            gameOver = true;
            showGameOver();
        } else {
            this.getChildren().remove(player);
            player = new GameCharacter(100, 100);
            this.getChildren().add(1, player);
        }
    }

    private void onBossDefeated() {
        bossDefeated = true;
        createBossExplosions();
        stageCleared = true;
        stageClearDelay = STAGE_CLEAR_WAIT;
    }

    private void createExplosion(double x, double y) {
        Explosion exp = new Explosion(explosionSprite, x, y);
        explosions.add(exp);
        this.getChildren().add(exp);
    }

    private void createBossExplosions() {
        double x = 0, y = 0, w = 0, h = 0;

        if (bossHandler.getWallBoss() != null) {
            var b = bossHandler.getWallBoss();
            x = b.getBossX(); y = b.getBossY(); w = b.getBossWidth(); h = b.getBossHeight();
        } else if (bossHandler.getJavaBoss() != null) {
            var b = bossHandler.getJavaBoss();
            x = b.getBossX(); y = b.getBossY(); w = b.getBossWidth(); h = b.getBossHeight();
        } else if (bossHandler.getBoss3() != null) {
            var b = bossHandler.getBoss3();
            x = b.getX(); y = b.getY(); w = b.getBossWidth(); h = b.getBossHeight();
        }

        for (int i = 0; i < 10; i++) {
            double randomX = x + Math.random() * w;
            double randomY = y + Math.random() * h;
            createExplosion(randomX, randomY);
        }
    }

    private void updateExplosions() {
        Iterator<Explosion> it = explosions.iterator();
        while (it.hasNext()) {
            Explosion exp = it.next();
            exp.update();
            if (exp.isFinished()) {
                it.remove();
                this.getChildren().remove(exp);
            }
        }
    }

    private void showGameOver() {
        this.getChildren().clear();

        Rectangle bg = new Rectangle(WIDTH, HEIGHT, Color.BLACK);

        Text gameOverText = new Text("GAME OVER!");
        gameOverText.setFont(Font.font("Arial", 48));
        gameOverText.setFill(Color.RED);
        gameOverText.setTranslateX(WIDTH / 2 - 150);
        gameOverText.setTranslateY(HEIGHT / 2 - 60);

        Text scoreText = new Text("Final Score: " + uiHandler.getScore());
        scoreText.setFont(Font.font("Arial", 36));
        scoreText.setFill(Color.WHITE);
        scoreText.setTranslateX(WIDTH / 2 - 120);
        scoreText.setTranslateY(HEIGHT / 2 + 20);

        Button restartButton = new Button("RESTART");
        restartButton.setFont(Font.font("Arial", 24));
        restartButton.setTranslateX(WIDTH / 2 - 60);
        restartButton.setTranslateY(HEIGHT / 2 + 80);
        restartButton.setOnAction(e -> restartGame());

        this.getChildren().addAll(bg, gameOverText, scoreText, restartButton);
    }

    private void showGameCompleted() {
        this.getChildren().clear();

        Rectangle bg = new Rectangle(WIDTH, HEIGHT, Color.DARKBLUE);

        Text completedText = new Text("ALL STAGES COMPLETED!");
        completedText.setFont(Font.font("Arial", 48));
        completedText.setFill(Color.GOLD);
        completedText.setTranslateX(WIDTH / 2 - 300);
        completedText.setTranslateY(HEIGHT / 2 - 60);

        Text scoreText = new Text("Final Score: " + uiHandler.getScore());
        scoreText.setFont(Font.font("Arial", 36));
        scoreText.setFill(Color.WHITE);
        scoreText.setTranslateX(WIDTH / 2 - 150);
        scoreText.setTranslateY(HEIGHT / 2 + 20);

        Button restartButton = new Button("PLAY AGAIN");
        restartButton.setFont(Font.font("Arial", 24));
        restartButton.setTranslateX(WIDTH / 2 - 75);
        restartButton.setTranslateY(HEIGHT / 2 + 80);
        restartButton.setOnAction(e -> restartGame());

        this.getChildren().addAll(bg, completedText, scoreText, restartButton);
    }

    private void restartGame() {
        gameOver = false;
        playerLives = 3;
        loadStage(1);
    }

    private Image loadImage(String filename) {
        try {
            String path = "/se233/project2/assets/" + filename;
            var stream = getClass().getResourceAsStream(path);
            if (stream != null) return new Image(stream);
        } catch (Exception e) {
            System.err.println("Error loading " + filename);
        }
        return null;
    }

    public Keys getKeys() { return keys; }
}