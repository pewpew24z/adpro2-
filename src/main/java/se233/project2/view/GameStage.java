package se233.project2.view;

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
import se233.project2.model.item.Bullet;
import se233.project2.model.boss.WallBoss;
import se233.project2.model.boss.JavaBoss;
import se233.project2.model.boss.SmallBoss;
import se233.project2.model.boss.Boss3;
import se233.project2.model.enemy.RegularEnemy;
import se233.project2.model.enemy.SecondTierEnemy;
import se233.project2.model.effect.Explosion;

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
    private Score scoreLabel;
    private Text livesLabel;
    private Text stageLabel;
    private Text waveLabel;

    private List<Bullet> playerBullets;
    private List<Platform> platforms;
    private List<Explosion> explosions;

    // Enemies
    private List<RegularEnemy> regularEnemies;
    private List<SecondTierEnemy> secondTierEnemies;

    // Bosses
    private WallBoss wallBoss;
    private JavaBoss javaBoss;
    private List<SmallBoss> smallBosses;  // Stage 3: มีหลายตัว
    private Boss3 boss3;  // Last Boss ของ Stage 3

    // Sprites - แยกต่างหาก per entity
    private Image playerBulletSprite;
    private Image explosionSprite;

    // Enemy sprites
    private Image regularEnemySprite;
    private Image regularEnemyBulletSprite;  // แยกต่างหาก

    private Image secondTierEnemySprite;
    private Image secondTierEnemyBulletSprite;  // แยกต่างหาก

    // Boss sprites
    private Image wallBossNormalSprite;
    private Image wallBossDeadSprite;
    private Image wallBossBulletSprite;  // แยกต่างหาก

    private Image javaBossNormalSprite;
    private Image javaBossDeadSprite;
    private Image javaBossWeaponSprite;
    private Image javaBossBulletSprite;  // แยกต่างหาก (sprite sheet 4 frames)

    private Image smallBossSprite;
    private Image smallBossWeaponSprite;
    private Image smallBossBulletSprite;

    private Image boss3Sprite;
    private Image boss3WeaponSprite;
    private Image boss3BulletSprite;

    private long lastShoot = 0;
    private final long SHOOT_DELAY = 200_000_000;

    // Game state
    private int playerLives = 3;
    private int currentStage = 1;
    private boolean minionsCleared = false;
    private boolean bossSpawned = false;
    private boolean bossDefeated = false;
    private boolean boss3Spawned = false;  // เช็คว่า spawn Boss3 แล้วหรือยัง
    private boolean stageCleared = false;
    private boolean gameOver = false;
    private int stageClearDelay = 0;
    private int bossSpawnDelay = 0;
    private final int STAGE_CLEAR_WAIT = 180;
    private final int BOSS_SPAWN_WAIT = 120;

    public GameStage() {
        this.setPrefWidth(WIDTH);
        this.setPrefHeight(HEIGHT);

        // Load sprites - แยกตามตัว
        loadAllSprites();

        // Initialize
        keys = new Keys();
        playerBullets = new ArrayList<>();
        platforms = new ArrayList<>();
        explosions = new ArrayList<>();
        regularEnemies = new ArrayList<>();
        secondTierEnemies = new ArrayList<>();
        smallBosses = new ArrayList<>();

        loadStage(1);

        this.setOnKeyPressed(event -> keys.update(event.getCode(), true));
        this.setOnKeyReleased(event -> keys.update(event.getCode(), false));
        this.setFocusTraversable(true);
    }

    private void loadAllSprites() {
        // Player
        playerBulletSprite = loadImage("item/bullet-player.png");

        // Effects
        explosionSprite = loadImage("effect/Boom.png");

        // Regular Enemy (Stage 1 minions)
        regularEnemySprite = loadImage("enemy/regular_enemy.png");
        regularEnemyBulletSprite = loadImage("enemy/bullet_regular-enemy.png");  // ⭐ bullet แยก

        // Second-Tier Enemy (Stage 2 minions)
        secondTierEnemySprite = loadImage("enemy/secound-tier_enemy.png");
        secondTierEnemyBulletSprite = loadImage("enemy/bullet_secound-tier-enemy.png");  // ⭐ bullet แยก

        // Wall Boss (Stage 1)
        wallBossNormalSprite = loadImage("boss/boss1/wall-boss-normal.png");
        wallBossDeadSprite = loadImage("effect/Boom_removebg.png");
        wallBossBulletSprite = loadImage("boss/boss1/bullet-wall.png");  // ⭐ bullet แยก

        // Java Boss (Stage 2)
        javaBossNormalSprite = loadImage("boss/boss2/Java-boss.png");
        javaBossDeadSprite = loadImage("effect/Boom_removebg.png");
        javaBossWeaponSprite = loadImage("boss/boss2/weapon-boss2.png");
        javaBossBulletSprite = loadImage("boss/boss2/bullet-java-boss.png");  // ⭐ bullet แยก (sprite sheet 4 frames)

        // Small Boss (Stage 3)
        smallBossSprite = loadImage("enemy/smallboss/small-boss.png");  // sprite sheet 4 frames
        smallBossWeaponSprite = loadImage("enemy/smallboss/weapon_small-boss.png");  // sprite sheet 3 frames
        smallBossBulletSprite = loadImage("enemy/smallboss/bullet_small-boss.png");

        // Boss3 (Last Boss - Stage 3)
        boss3Sprite = loadImage("boss/boss3/boss3.png");  // ⭐ ถ้ามีรูป
        boss3WeaponSprite = loadImage("boss/boss3/boss3_weapon.png");
        boss3BulletSprite = loadImage("boss/boss3/boss3_bullet.png");
    }

    private void loadStage(int stage) {
        // Reset state
        this.getChildren().clear();
        playerBullets.clear();
        platforms.clear();
        explosions.clear();
        regularEnemies.clear();
        secondTierEnemies.clear();
        smallBosses.clear();

        wallBoss = null;
        javaBoss = null;
        boss3 = null;

        minionsCleared = false;
        bossSpawned = false;
        bossDefeated = false;
        boss3Spawned = false;
        stageCleared = false;
        bossSpawnDelay = 0;
        stageClearDelay = 0;

        currentStage = stage;

        // Load background
        String bgPath = "stage/Stage" + stage + ".png";
        Image backgroundImage = loadImage(bgPath);
        if (backgroundImage != null) {
            backgroundImageView = new ImageView(backgroundImage);
            backgroundImageView.setFitWidth(WIDTH);
            backgroundImageView.setFitHeight(HEIGHT);
            backgroundImageView.setPreserveRatio(false);
            this.getChildren().add(backgroundImageView);
        } else {
            Rectangle bg = new Rectangle(WIDTH, HEIGHT, Color.SKYBLUE);
            this.getChildren().add(bg);
        }

        // Create platforms
        createPlatformsForStage(stage);

        // Create player
        if (player == null) {
            player = new GameCharacter(100, 100);
            scoreLabel = new Score(20, 20);
        } else {
            this.getChildren().remove(player);
            player = new GameCharacter(100, 100);
        }

        // Spawn minions first (NOT boss yet)
        spawnMinionsForStage(stage);

        // UI
        livesLabel = new Text("Lives: " + playerLives);
        livesLabel.setFont(Font.font("Arial", 24));
        livesLabel.setFill(Color.WHITE);
        livesLabel.setTranslateX(WIDTH - 150);
        livesLabel.setTranslateY(40);

        stageLabel = new Text("STAGE " + currentStage);
        stageLabel.setFont(Font.font("Arial", 32));
        stageLabel.setFill(Color.YELLOW);
        stageLabel.setTranslateX(WIDTH / 2 - 80);
        stageLabel.setTranslateY(50);

        waveLabel = new Text("Clear Enemies!");
        waveLabel.setFont(Font.font("Arial", 24));
        waveLabel.setFill(Color.ORANGE);
        waveLabel.setTranslateX(WIDTH / 2 - 100);
        waveLabel.setTranslateY(100);

        this.getChildren().addAll(player, scoreLabel, livesLabel, stageLabel, waveLabel);
    }

    private void createPlatformsForStage(int stage) {
        if (stage == 1) {
            platforms.add(new Platform(0, 260, 352, 96));
            platforms.add(new Platform(364, 408, 176, 60));
            platforms.add(new Platform(0, 488, 360, 96));
            platforms.add(new Platform(544, 516, 168, 96));
            platforms.add(new Platform(712, 552, 568, 116));
            platforms.add(new Platform(360, 556, 184, 104));
            platforms.add(new Platform(-100, 670, WIDTH + 200, 100));
        } else if (stage == 2) {
            // Stage 2 platforms - หญ้าสีเหลือง
            platforms.add(new Platform(0, 390, 193, 329));        // หญ้าซ้าย (ก้อนแรก)
            platforms.add(new Platform(195, 504, 1085, 216));     // หญ้ากลาง-ขวา (ยาว)
        } else if (stage == 3) {
            // Stage 3 platforms - ก้อนเมฆ (y ตรงกันหมด)
            platforms.add(new Platform(50, 580, 280, 60));      // ซ้าย
            platforms.add(new Platform(480, 580, 280, 60));     // กลาง
            platforms.add(new Platform(900, 580, 280, 60));     // ขวา
            platforms.add(new Platform(0, GROUND_Y, WIDTH, 100));  // พื้นล่างสุด
        }
    }

    private void spawnMinionsForStage(int stage) {
        if (stage == 1) {
            // Stage 1: Regular Enemies (ใช้ bullet_regular-enemy.png)
            regularEnemies.add(new RegularEnemy(
                    regularEnemySprite, regularEnemyBulletSprite,  // ⭐ ใช้ bullet แยก
                    900, 200, 700, WIDTH - 50, 100, 400, 3
            ));
            regularEnemies.add(new RegularEnemy(
                    regularEnemySprite, regularEnemyBulletSprite,
                    1000, 300, 700, WIDTH - 50, 100, 500, 3
            ));
            regularEnemies.add(new RegularEnemy(
                    regularEnemySprite, regularEnemyBulletSprite,
                    850, 400, 700, WIDTH - 50, 200, 500, 3
            ));

            for (RegularEnemy enemy : regularEnemies) {
                this.getChildren().add(enemy);
            }

        } else if (stage == 2) {
            // Stage 2: Second-Tier Enemies (ใช้ bullet_secound-tier-enemy.png)
            secondTierEnemies.add(new SecondTierEnemy(
                    secondTierEnemySprite, secondTierEnemyBulletSprite,  // ⭐ ใช้ bullet แยก
                    900, 200, 700, WIDTH - 50, 100, 400, 5
            ));
            secondTierEnemies.add(new SecondTierEnemy(
                    secondTierEnemySprite, secondTierEnemyBulletSprite,
                    1000, 250, 700, WIDTH - 50, 100, 450, 5
            ));
            secondTierEnemies.add(new SecondTierEnemy(
                    secondTierEnemySprite, secondTierEnemyBulletSprite,
                    1100, 300, 700, WIDTH - 50, 150, 500, 5
            ));
            secondTierEnemies.add(new SecondTierEnemy(
                    secondTierEnemySprite, secondTierEnemyBulletSprite,
                    950, 350, 700, WIDTH - 50, 200, 500, 5
            ));
            secondTierEnemies.add(new SecondTierEnemy(
                    secondTierEnemySprite, secondTierEnemyBulletSprite,
                    1050, 150, 700, WIDTH - 50, 100, 400, 5
            ));

            for (SecondTierEnemy enemy : secondTierEnemies) {
                this.getChildren().add(enemy);
            }
        } else if (stage == 3) {
            // Stage 3: ไม่มี minions เลย - รอ spawn SmallBoss 6 ตัว
            minionsCleared = true;
            bossSpawnDelay = BOSS_SPAWN_WAIT;
            waveLabel.setText("Small Bosses Incoming...");
            waveLabel.setFill(Color.YELLOW);
        }
    }

    private void spawnBossForStage(int stage) {
        if (stage == 1) {
            // Wall Boss (ใช้ bullet-wall.png)
            System.out.println("=== SPAWNING WALLBOSS (Stage 1) ===");
            wallBoss = new WallBoss(1050, 200, 180, 400, 30,
                    wallBossNormalSprite, wallBossDeadSprite, wallBossBulletSprite);  // ⭐ bullet แยก
            this.getChildren().add(wallBoss);
            System.out.println("✅ WallBoss spawned at x=1050, y=200");


        } else if (stage == 2) {
            // Java Boss (ใช้ bullet-java-boss.png sprite sheet 4 frames)
            javaBoss = new JavaBoss(1000, 250, 200, 300, 50,
                    javaBossNormalSprite, javaBossDeadSprite, javaBossWeaponSprite,
                    javaBossBulletSprite);  // ⭐ bullet แยก
            this.getChildren().add(javaBoss);

        } else if (stage == 3) {
            // Small Bosses (6 ตัว) - เกิดบนแนวก้อนเมฆ
            // Platform positions: ซ้าย(50-330), กลาง(480-760), ขวา(900-1180)
            // SmallBoss height=120, Platform y=580 → Boss y=460

            double bossY = 460;  // ยืนพอดีบน platform (580-120)

            // x: เริ่มจากนอกจอขวา (เว้นระยะกัน)
            // minX: หยุดบน platform แต่ละก้อน
            double[] xPositions = {1350, 1450, 1550, 1650, 1750, 1850};
            double[] minXPositions = {
                    150,   // หยุดบน platform ซ้าย
                    230,   // หยุดบน platform ซ้าย
                    550,   // หยุดบน platform กลาง
                    630,   // หยุดบน platform กลาง
                    950,   // หยุดบน platform ขวา
                    1030   // หยุดบน platform ขวา
            };

            for (int i = 0; i < 6; i++) {
                SmallBoss boss = new SmallBoss(
                        smallBossSprite,
                        smallBossWeaponSprite,
                        smallBossBulletSprite,
                        xPositions[i],    // เริ่มจากขวา
                        bossY,            // ✨ y เดียวกันทั้งหมด = 460
                        minXPositions[i], // ✨ หยุดบน platform ต่างๆ
                        10  // HP ต่ำ เพราะมี 6 ตัว
                );
                smallBosses.add(boss);
                this.getChildren().add(boss);
            }
        }

        waveLabel.setText("BOSS FIGHT!");
        waveLabel.setFill(Color.RED);
    }

    private void spawnBoss3() {
        // Spawn Boss3 (Last Boss) หลังจาก SmallBoss ตายหมด
        boss3 = new Boss3(
                boss3Sprite,
                boss3WeaponSprite,
                boss3BulletSprite,
                1200,  // เริ่มจากขวา
                300,   // กลางจอ
                50     // HP สูงกว่า SmallBoss มาก
        );
        this.getChildren().add(boss3);

        waveLabel.setText("FINAL BOSS!");
        waveLabel.setFill(Color.RED);
        System.out.println("Boss3 (Last Boss) spawned!");
    }

    public void update(long now) {
        if (gameOver) {
            showGameOver();
            return;
        }

        if (stageCleared) {
            stageClearDelay--;
            if (stageClearDelay <= 0) {
                if (currentStage < 3) {  // ⭐ เปลี่ยนเป็น 3 stages
                    loadStage(currentStage + 1);
                } else {
                    showGameCompleted();
                }
            }
            return;
        }

        // Check if all minions cleared
        if (!minionsCleared && !bossSpawned) {
            boolean allCleared = regularEnemies.stream().allMatch(e -> !e.isAlive()) &&
                    secondTierEnemies.stream().allMatch(e -> !e.isAlive());

            if (allCleared) {
                System.out.println("✅ All enemies cleared! Boss incoming in " + BOSS_SPAWN_WAIT + " frames...");
                minionsCleared = true;
                bossSpawnDelay = BOSS_SPAWN_WAIT;
                waveLabel.setText("Boss Incoming...");
                waveLabel.setFill(Color.YELLOW);
            }
        }

        // Spawn boss after delay
        if (minionsCleared && !bossSpawned) {
            bossSpawnDelay--;
            if (bossSpawnDelay <= 0) {
                spawnBossForStage(currentStage);
                bossSpawned = true;
            }
        }

        // Update player
        player.update(keys, platforms);

        // Player shooting
        if (player.isShooting() && now - lastShoot > SHOOT_DELAY) {
            shootPlayerBullet();
            lastShoot = now;
        }

        // Update regular enemies
        for (RegularEnemy enemy : regularEnemies) {
            if (enemy.isAlive()) {
                enemy.setPlayerPosition(player.getCenterX(), player.getCenterY());
                enemy.update(now);
                updateEnemyBullets(enemy.getBullets(), enemy);
            }
        }

        // Update second-tier enemies
        for (SecondTierEnemy enemy : secondTierEnemies) {
            if (enemy.isAlive()) {
                enemy.setPlayerPosition(player.getCenterX(), player.getCenterY());
                enemy.update(now);
                updateEnemyBullets(enemy.getBullets(), enemy);
            }
        }

        // Update Small Bosses (Stage 3)
        for (SmallBoss boss : smallBosses) {
            if (boss.isAlive()) {
                boss.update(now);
                updateSmallBossBullets(boss.getBullets(), boss);
            }
        }

        // Update player bullets
        updatePlayerBullets();

        // Update bosses
        if (wallBoss != null && wallBoss.isAlive()) {
            wallBoss.update(now);
            updateWallBossBullets(wallBoss.getBossBullets(), wallBoss);  // ⭐ ใช้ method พิเศษสำหรับ WallBoss
        } else if (wallBoss != null && !wallBoss.isAlive() && !bossDefeated) {
            onBossDefeated();
        }

        if (javaBoss != null && javaBoss.isAlive()) {
            javaBoss.update(now);
            updateBossBullets(javaBoss.getBullets(), javaBoss);
        } else if (javaBoss != null && !javaBoss.isAlive() && !bossDefeated) {
            onBossDefeated();
        }

        // Stage 3: Check if all small bosses defeated → spawn Boss3
        if (currentStage == 3 && !smallBosses.isEmpty() &&
                smallBosses.stream().allMatch(b -> !b.isAlive()) && !boss3Spawned) {
            // Spawn Boss3 (Last Boss)
            spawnBoss3();
            boss3Spawned = true;
        }

        // Update Boss3 (Last Boss)
        if (boss3 != null && boss3.isAlive()) {
            boss3.update(now);
            updateBoss3Bullets(boss3.getBullets());
        } else if (boss3 != null && !boss3.isAlive() && !bossDefeated) {
            onBossDefeated();
        }

        // Update explosions
        Iterator<Explosion> expIt = explosions.iterator();
        while (expIt.hasNext()) {
            Explosion exp = expIt.next();
            exp.update();
            if (exp.isFinished()) {
                expIt.remove();
                this.getChildren().remove(exp);
            }
        }
    }

    private void updatePlayerBullets() {
        Iterator<Bullet> bulletIt = playerBullets.iterator();
        while (bulletIt.hasNext()) {
            Bullet b = bulletIt.next();
            b.update();

            // Check regular enemy collision
            for (RegularEnemy enemy : regularEnemies) {
                if (enemy.checkBulletCollision(b)) {
                    scoreLabel.addScore(1);
                    createExplosion(b.getX(), b.getY());
                    bulletIt.remove();
                    this.getChildren().remove(b);
                    break;
                }
            }

            if (!b.isActive()) {
                bulletIt.remove();
                this.getChildren().remove(b);
                continue;
            }

            // Check second-tier enemy collision
            for (SecondTierEnemy enemy : secondTierEnemies) {
                if (enemy.checkBulletCollision(b)) {
                    scoreLabel.addScore(2);
                    createExplosion(b.getX(), b.getY());
                    bulletIt.remove();
                    this.getChildren().remove(b);
                    break;
                }
            }

            if (!b.isActive()) continue;

            // Check small boss collision (Stage 3)
            for (SmallBoss boss : smallBosses) {
                if (boss.checkBulletCollision(b)) {
                    scoreLabel.addScore(2);
                    createExplosion(b.getX(), b.getY());
                    bulletIt.remove();
                    this.getChildren().remove(b);
                    break;
                }
            }

            if (!b.isActive()) continue;

            // Check boss collision
            if (wallBoss != null && wallBoss.isAlive()) {
                if (wallBoss.checkBulletCollision(b)) {
                    scoreLabel.addScore(1);
                    createExplosion(b.getX(), b.getY());
                    bulletIt.remove();
                    this.getChildren().remove(b);
                }
            } else if (javaBoss != null && javaBoss.isAlive()) {
                if (javaBoss.checkBulletCollision(b)) {
                    scoreLabel.addScore(1);
                    createExplosion(b.getX(), b.getY());
                    bulletIt.remove();
                    this.getChildren().remove(b);
                }
            } else if (boss3 != null && boss3.isAlive()) {
                if (boss3.checkBulletCollision(b)) {
                    scoreLabel.addScore(1);
                    createExplosion(b.getX(), b.getY());
                    bulletIt.remove();
                    this.getChildren().remove(b);
                }
            }
        }
    }

    private void updateEnemyBullets(List<Bullet> bullets, Object enemy) {
        for (Bullet bullet : bullets) {
            if (!this.getChildren().contains(bullet)) {
                this.getChildren().add(bullet);
            }

            if (bullet.checkGroundCollision(GROUND_Y)) {
                createExplosion(bullet.getX(), bullet.getY());
                bullet.deactivate();
                this.getChildren().remove(bullet);

                if (enemy instanceof RegularEnemy) {
                    ((RegularEnemy) enemy).removeBullet(bullet);
                } else if (enemy instanceof SecondTierEnemy) {
                    ((SecondTierEnemy) enemy).removeBullet(bullet);
                }
                continue;
            }

            if (checkPlayerHit(bullet)) {
                playerHit();
                createExplosion(bullet.getX(), bullet.getY());
                bullet.deactivate();
                this.getChildren().remove(bullet);

                if (enemy instanceof RegularEnemy) {
                    ((RegularEnemy) enemy).removeBullet(bullet);
                } else if (enemy instanceof SecondTierEnemy) {
                    ((SecondTierEnemy) enemy).removeBullet(bullet);
                }
            }
        }
    }

    private void updateSmallBossBullets(List<Bullet> bullets, SmallBoss boss) {
        for (Bullet bullet : bullets) {
            if (!this.getChildren().contains(bullet)) {
                this.getChildren().add(bullet);
            }

            if (bullet.checkGroundCollision(GROUND_Y)) {
                createExplosion(bullet.getX(), bullet.getY());
                bullet.deactivate();
                this.getChildren().remove(bullet);
                boss.removeBullet(bullet);
                continue;
            }

            if (checkPlayerHit(bullet)) {
                playerHit();
                createExplosion(bullet.getX(), bullet.getY());
                bullet.deactivate();
                this.getChildren().remove(bullet);
                boss.removeBullet(bullet);
            }
        }
    }

    private void updateBoss3Bullets(List<Bullet> bullets) {
        for (Bullet bullet : bullets) {
            if (!this.getChildren().contains(bullet)) {
                this.getChildren().add(bullet);
            }

            if (bullet.checkGroundCollision(GROUND_Y)) {
                createExplosion(bullet.getX(), bullet.getY());
                bullet.deactivate();
                this.getChildren().remove(bullet);
                boss3.removeBullet(bullet);
                continue;
            }

            if (checkPlayerHit(bullet)) {
                playerHit();
                createExplosion(bullet.getX(), bullet.getY());
                bullet.deactivate();
                this.getChildren().remove(bullet);
                boss3.removeBullet(bullet);
            }
        }
    }

    /**
     * Update WallBoss bullets (Stage 1)
     * แสดง explosion เฉพาะเมื่อ bullet ตกบน platform ที่กำหนดเท่านั้น
     */
    private void updateWallBossBullets(List<Bullet> bullets, WallBoss boss) {
        // Platform ที่อนุญาตให้แสดง explosion (Stage 1)
        final double PLATFORM_1_X = 543;  // Platform ซ้าย
        final double PLATFORM_1_Y = 516;
        final double PLATFORM_1_WIDTH = 168;

        final double PLATFORM_2_X = 716;  // Platform boss (ขวา)
        final double PLATFORM_2_Y = 552;
        final double PLATFORM_2_WIDTH = 568;

        for (Bullet bullet : bullets) {
            if (!this.getChildren().contains(bullet)) {
                this.getChildren().add(bullet);
            }

            // เช็คว่า bullet ตกถึง platform หรือยัง
            double bulletY = bullet.getY();
            double bulletX = bullet.getX();

            // เช็ค platform 1 (y=516)
            boolean hitPlatform1 = bulletY >= PLATFORM_1_Y &&
                    bulletX >= PLATFORM_1_X &&
                    bulletX <= PLATFORM_1_X + PLATFORM_1_WIDTH;

            // เช็ค platform 2 (y=552)
            boolean hitPlatform2 = bulletY >= PLATFORM_2_Y &&
                    bulletX >= PLATFORM_2_X &&
                    bulletX <= PLATFORM_2_X + PLATFORM_2_WIDTH;

            // ถ้าตกบน platform ที่กำหนด → แสดง explosion
            if (hitPlatform1 || hitPlatform2) {
                createExplosion(bulletX, bulletY);
                bullet.deactivate();
                this.getChildren().remove(bullet);
                boss.removeBullet(bullet);
                continue;
            }

            // ถ้าตกถึงพื้นล่างสุด → ลบโดยไม่แสดง explosion
            if (bullet.checkGroundCollision(GROUND_Y)) {
                bullet.deactivate();
                this.getChildren().remove(bullet);
                boss.removeBullet(bullet);
                continue;
            }

            // เช็คว่าชน player หรือไม่
            if (checkPlayerHit(bullet)) {
                playerHit();
                createExplosion(bulletX, bulletY);
                bullet.deactivate();
                this.getChildren().remove(bullet);
                boss.removeBullet(bullet);
            }
        }
    }

    private void updateBossBullets(List<Bullet> bullets, Object boss) {
        for (Bullet bullet : bullets) {
            if (!this.getChildren().contains(bullet)) {
                this.getChildren().add(bullet);
            }

            if (bullet.checkGroundCollision(GROUND_Y)) {
                createExplosion(bullet.getX(), bullet.getY());
                bullet.deactivate();
                this.getChildren().remove(bullet);

                if (boss instanceof WallBoss) {
                    ((WallBoss) boss).removeBullet(bullet);
                } else if (boss instanceof JavaBoss) {
                    ((JavaBoss) boss).removeBullet(bullet);
                }
                continue;
            }

            if (checkPlayerHit(bullet)) {
                playerHit();
                createExplosion(bullet.getX(), bullet.getY());
                bullet.deactivate();
                this.getChildren().remove(bullet);

                if (boss instanceof WallBoss) {
                    ((WallBoss) boss).removeBullet(bullet);
                } else if (boss instanceof JavaBoss) {
                    ((JavaBoss) boss).removeBullet(bullet);
                }
            }
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

    private void createExplosion(double x, double y) {
        Explosion exp = new Explosion(explosionSprite, x, y);
        explosions.add(exp);
        this.getChildren().add(exp);
    }

    private void createBossDeathExplosions(double bossX, double bossY, double bossW, double bossH) {
        for (int i = 0; i < 10; i++) {
            double randomX = bossX + Math.random() * bossW;
            double randomY = bossY + Math.random() * bossH;
            createExplosion(randomX, randomY);
        }
    }

    private boolean checkPlayerHit(Bullet bullet) {
        double bulletX = bullet.getCenterX();
        double bulletY = bullet.getCenterY();
        return bulletX >= player.getX() && bulletX <= player.getX() + 47 &&
                bulletY >= player.getY() && bulletY <= player.getY() + 36;
    }

    private void playerHit() {
        playerLives--;
        livesLabel.setText("Lives: " + playerLives);

        if (playerLives <= 0) {
            gameOver = true;
        } else {
            this.getChildren().remove(player);
            player = new GameCharacter(100, 100);
            this.getChildren().add(1, player);
        }
    }

    private void onBossDefeated() {
        bossDefeated = true;
        scoreLabel.addScore(100);

        if (wallBoss != null) {
            createBossDeathExplosions(wallBoss.getBossX(), wallBoss.getBossY(),
                    wallBoss.getBossWidth(), wallBoss.getBossHeight());
        } else if (javaBoss != null) {
            createBossDeathExplosions(javaBoss.getBossX(), javaBoss.getBossY(),
                    javaBoss.getBossWidth(), javaBoss.getBossHeight());
        } else if (boss3 != null) {
            // Boss3 (Last Boss) defeated - huge explosions!
            createBossDeathExplosions(boss3.getX(), boss3.getY(),
                    boss3.getBossWidth(), boss3.getBossHeight());
        } else if (!smallBosses.isEmpty()) {
            // Explosions for all small bosses
            for (SmallBoss boss : smallBosses) {
                createBossDeathExplosions(boss.getX(), boss.getY(),
                        boss.getBossWidth(), boss.getBossHeight());
            }
        }

        stageCleared = true;
        stageClearDelay = STAGE_CLEAR_WAIT;
    }

    private void showGameOver() {
        this.getChildren().clear();
        Rectangle bg = new Rectangle(WIDTH, HEIGHT, Color.BLACK);
        Text endText = new Text("GAME OVER!");
        endText.setFont(Font.font("Arial", 48));
        endText.setFill(Color.RED);
        endText.setTranslateX(WIDTH / 2 - 150);
        endText.setTranslateY(HEIGHT / 2);
        this.getChildren().addAll(bg, endText);
    }

    private void showGameCompleted() {
        this.getChildren().clear();
        Rectangle bg = new Rectangle(WIDTH, HEIGHT, Color.DARKBLUE);
        Text endText = new Text("ALL STAGES COMPLETED!");
        endText.setFont(Font.font("Arial", 48));
        endText.setFill(Color.GOLD);
        endText.setTranslateX(WIDTH / 2 - 300);
        endText.setTranslateY(HEIGHT / 2);

        Text scoreText = new Text("Final Score: " + scoreLabel.getScore());
        scoreText.setFont(Font.font("Arial", 36));
        scoreText.setFill(Color.WHITE);
        scoreText.setTranslateX(WIDTH / 2 - 150);
        scoreText.setTranslateY(HEIGHT / 2 + 60);

        this.getChildren().addAll(bg, endText, scoreText);
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