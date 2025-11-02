package se233.project2.model;

import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import se233.project2.view.GameStage;

import java.util.List;

public class GameCharacter extends Pane {
    private AnimatedSprite currentSprite;

    // All sprite sheets
    private Image walkSheet;
    private Image jumpSheet;
    private Image proneSheet;
    private Image shootSheet;
    private Image shootUpSheet;
    private Image shootDownSheet;
    private Image walkShootSheet;
    private Image walkShootUpSheet;
    private Image walkShootDownSheet;
    private Image dieSheet;

    // All animated sprites
    private AnimatedSprite walkSprite;
    private AnimatedSprite jumpSprite;
    private AnimatedSprite proneSprite;
    private AnimatedSprite shootSprite;
    private AnimatedSprite shootUpSprite;
    private AnimatedSprite shootDownSprite;
    private AnimatedSprite walkShootSprite;
    private AnimatedSprite walkShootUpSprite;
    private AnimatedSprite walkShootDownSprite;
    private AnimatedSprite dieSprite;

    private int x;
    private int y;
    private double velocityX = 0;
    private double velocityY = 0;

    private boolean onGround = false;
    private boolean facingRight = true;
    private boolean isProne = false;
    private boolean isShooting = false;
    private boolean isShootingUp = false;
    private boolean isShootingDown = false;
    private boolean isDead = false;

    private final double GRAVITY = 0.5;
    private final double JUMP_FORCE = -12;
    private final double MOVE_SPEED = 4;
    private final double MAX_FALL_SPEED = 10;

    private int animationTick = 0;
    private final int ANIMATION_SPEED = 5;

    // Sprite dimensions
    private static final int SPRITE_WIDTH = 47;
    private static final int SPRITE_HEIGHT = 36;

    private static final int OFFSET_X = 0;
    private static final int OFFSET_Y = 0;
    private static final int SPACING_X = 47;
    private static final int SPACING_Y = 36;

    public GameCharacter(int x, int y) {
        this.x = x;
        this.y = y;

        // Load all sprites
        loadAllSprites();

        // Create all animated sprites
        createAnimatedSprites();

        // Set initial sprite
        if (walkSprite != null) {
            currentSprite = walkSprite;
            this.getChildren().add(currentSprite);
        } else {
            Rectangle placeholder = new Rectangle(SPRITE_WIDTH, SPRITE_HEIGHT, Color.HOTPINK);
            this.getChildren().add(placeholder);
        }

        this.setTranslateX(x);
        this.setTranslateY(y);
    }

    private void loadAllSprites() {
        walkSheet = loadSprite("/se233/project2/assets/player/kitty_walk.png");
        jumpSheet = loadSprite("/se233/project2/assets/player/kitty_jump.png");
        proneSheet = loadSprite("/se233/project2/assets/player/kitty_prone.png");
        shootSheet = loadSprite("/se233/project2/assets/player/kitty_shoot.png");
        shootUpSheet = loadSprite("/se233/project2/assets/player/kitty_shoot-up.png");
        shootDownSheet = loadSprite("/se233/project2/assets/player/kitty_shoot-down.png");
        walkShootSheet = loadSprite("/se233/project2/assets/player/kitty_walk_shoot.png");
        walkShootUpSheet = loadSprite("/se233/project2/assets/player/kitty_walk_shoot-up.png");
        walkShootDownSheet = loadSprite("/se233/project2/assets/player/kitty_walk_shoot-down.png");
        dieSheet = loadSprite("/se233/project2/assets/player/kitty_die.png");
    }

    private Image loadSprite(String path) {
        try {
            var stream = getClass().getResourceAsStream(path);
            if (stream != null) {
                return new Image(stream);
            }
        } catch (Exception e) {
            System.err.println("Error loading sprite: " + path + " - " + e.getMessage());
        }
        return null;
    }

    private void createAnimatedSprites() {
        // Walk animation (3 frames)
        if (walkSheet != null) {
            walkSprite = new AnimatedSprite(walkSheet, 3, 3,
                    OFFSET_X, OFFSET_Y, SPRITE_WIDTH, SPRITE_HEIGHT, SPACING_X, SPACING_Y);
            setupSprite(walkSprite);
        }

        // Jump animation (1 frame)
        if (jumpSheet != null) {
            jumpSprite = new AnimatedSprite(jumpSheet, 1, 1,
                    OFFSET_X, OFFSET_Y, SPRITE_WIDTH, SPRITE_HEIGHT, SPACING_X, SPACING_Y);
            setupSprite(jumpSprite);
        }

        // Prone animation (1 frame)
        if (proneSheet != null) {
            proneSprite = new AnimatedSprite(proneSheet, 1, 1,
                    OFFSET_X, OFFSET_Y, SPRITE_WIDTH, SPRITE_HEIGHT, SPACING_X, SPACING_Y);
            setupSprite(proneSprite);
        }

        // Shoot animation (1 frame)
        if (shootSheet != null) {
            shootSprite = new AnimatedSprite(shootSheet, 1, 1,
                    OFFSET_X, OFFSET_Y, SPRITE_WIDTH, SPRITE_HEIGHT, SPACING_X, SPACING_Y);
            setupSprite(shootSprite);
        }

        // Shoot up animation (1 frame)
        if (shootUpSheet != null) {
            shootUpSprite = new AnimatedSprite(shootUpSheet, 1, 1,
                    OFFSET_X, OFFSET_Y, SPRITE_WIDTH, SPRITE_HEIGHT, SPACING_X, SPACING_Y);
            setupSprite(shootUpSprite);
        }

        // Shoot down animation (1 frame)
        if (shootDownSheet != null) {
            shootDownSprite = new AnimatedSprite(shootDownSheet, 1, 1,
                    OFFSET_X, OFFSET_Y, SPRITE_WIDTH, SPRITE_HEIGHT, SPACING_X, SPACING_Y);
            setupSprite(shootDownSprite);
        }

        // Walk shoot animation (3 frames)
        if (walkShootSheet != null) {
            walkShootSprite = new AnimatedSprite(walkShootSheet, 3, 3,
                    OFFSET_X, OFFSET_Y, SPRITE_WIDTH, SPRITE_HEIGHT, SPACING_X, SPACING_Y);
            setupSprite(walkShootSprite);
        }

        // Walk shoot up animation (3 frames)
        if (walkShootUpSheet != null) {
            walkShootUpSprite = new AnimatedSprite(walkShootUpSheet, 3, 3,
                    OFFSET_X, OFFSET_Y, SPRITE_WIDTH, SPRITE_HEIGHT, SPACING_X, SPACING_Y);
            setupSprite(walkShootUpSprite);
        }

        // Walk shoot down animation (3 frames)
        if (walkShootDownSheet != null) {
            walkShootDownSprite = new AnimatedSprite(walkShootDownSheet, 3, 3,
                    OFFSET_X, OFFSET_Y, SPRITE_WIDTH, SPRITE_HEIGHT, SPACING_X, SPACING_Y);
            setupSprite(walkShootDownSprite);
        }

        // Die animation (2 frames)
        if (dieSheet != null) {
            dieSprite = new AnimatedSprite(dieSheet, 2, 2,
                    OFFSET_X, OFFSET_Y, SPRITE_WIDTH, SPRITE_HEIGHT, SPACING_X, SPACING_Y);
            setupSprite(dieSprite);
        }
    }

    private void setupSprite(AnimatedSprite sprite) {
        sprite.setFitWidth(SPRITE_WIDTH);
        sprite.setFitHeight(SPRITE_HEIGHT);
        sprite.setPreserveRatio(true);
        sprite.setSmooth(false);
    }

    private void switchSprite(AnimatedSprite newSprite) {
        if (newSprite == null || newSprite == currentSprite) return;

        this.getChildren().remove(currentSprite);
        currentSprite = newSprite;
        currentSprite.setScaleX(facingRight ? 1 : -1);
        this.getChildren().add(currentSprite);
    }

    public void update(Keys keys, List<Platform> platforms) {
        if (isDead) {
            updateDeathAnimation();
            return;
        }

        // Horizontal movement (WASD controls)
        velocityX = 0;

        if (!isProne) {
            if (keys.isPressed(KeyCode.A)) {
                velocityX = -MOVE_SPEED;
                facingRight = false;
            } else if (keys.isPressed(KeyCode.D)) {
                velocityX = MOVE_SPEED;
                facingRight = true;
            }
        }

        // Jump (W key)
        if (keys.isPressed(KeyCode.W) && onGround) {
            velocityY = JUMP_FORCE;
            onGround = false;
        }

        // Prone (S key)
        isProne = keys.isPressed(KeyCode.S) && onGround;

        // Shooting
        isShooting = keys.isPressed(KeyCode.SPACE);
        isShootingUp = keys.isPressed(KeyCode.UP);
        isShootingDown = keys.isPressed(KeyCode.DOWN);

        // Gravity
        if (!onGround) {
            velocityY += GRAVITY;
            if (velocityY > MAX_FALL_SPEED) {
                velocityY = MAX_FALL_SPEED;
            }
        }

        // Update position
        x += velocityX;
        y += velocityY;

        // Boundaries
        if (x < 0) x = 0;
        if (x > GameStage.WIDTH - SPRITE_WIDTH) x = GameStage.WIDTH - SPRITE_WIDTH;

        // Platform collision
        onGround = false;
        for (Platform platform : platforms) {
            if (platform.isPlayerOnTop(x, y, SPRITE_WIDTH, SPRITE_HEIGHT, velocityY)) {
                y = platform.getTop() - SPRITE_HEIGHT;
                velocityY = 0;
                onGround = true;
                break;
            }
        }

        // Ground collision (fallback)
        if (y >= GameStage.HEIGHT - SPRITE_HEIGHT) {
            y = GameStage.HEIGHT - SPRITE_HEIGHT;
            velocityY = 0;
            onGround = true;
        }

        // Update animation
        updateAnimation();

        // Update visual position
        this.setTranslateX(x);
        this.setTranslateY(y);

        // Flip sprite
        if (currentSprite != null) {
            currentSprite.setScaleX(facingRight ? 1 : -1);
        }
    }

    private void updateAnimation() {
        if (currentSprite == null) return;

        boolean isMoving = (velocityX != 0);
        AnimatedSprite targetSprite = null;

        // Priority: Death > Prone > Jump > Shooting + Walking > Shooting > Walking

        if (isDead) {
            targetSprite = dieSprite;
        } else if (isProne) {
            targetSprite = proneSprite;
        } else if (!onGround) {
            // In air (jumping/falling)
            targetSprite = jumpSprite;
        } else if (isMoving && isShootingUp) {
            targetSprite = walkShootUpSprite;
        } else if (isMoving && isShootingDown) {
            targetSprite = walkShootDownSprite;
        } else if (isMoving && isShooting) {
            targetSprite = walkShootSprite;
        } else if (isShootingUp) {
            targetSprite = shootUpSprite;
        } else if (isShootingDown) {
            targetSprite = shootDownSprite;
        } else if (isShooting) {
            targetSprite = shootSprite;
        } else if (isMoving) {
            targetSprite = walkSprite;
        } else {
            // Idle - use first frame of walk
            targetSprite = walkSprite;
            if (currentSprite != walkSprite) {
                switchSprite(targetSprite);
                currentSprite.reset();
            }
            return;
        }

        // Switch sprite if different
        if (targetSprite != null && targetSprite != currentSprite) {
            switchSprite(targetSprite);
            animationTick = 0;
        }

        // Animate multi-frame sprites
        if (isMoving || isDead) {
            animationTick++;
            if (animationTick >= ANIMATION_SPEED) {
                animationTick = 0;
                currentSprite.tick();
            }
        }
    }

    private void updateDeathAnimation() {
        if (dieSprite != null && currentSprite != dieSprite) {
            switchSprite(dieSprite);
            animationTick = 0;
        }

        // Animate death
        animationTick++;
        if (animationTick >= ANIMATION_SPEED * 2) { // Slower death animation
            animationTick = 0;
            currentSprite.tick();
        }
    }

    public void die() {
        isDead = true;
        velocityX = 0;
        velocityY = 0;
    }

    public double getCenterX() {
        return x + (SPRITE_WIDTH / 2);
    }

    public double getCenterY() {
        return y + (SPRITE_HEIGHT / 2);
    }

    public boolean isFacingRight() {
        return facingRight;
    }

    public boolean isShooting() {
        return isShooting || isShootingUp || isShootingDown;
    }

    public boolean isShootingUp() {
        return isShootingUp;
    }

    public boolean isShootingDown() {
        return isShootingDown;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isDead() {
        return isDead;
    }
}