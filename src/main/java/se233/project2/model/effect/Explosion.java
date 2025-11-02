package se233.project2.model.effect;

import javafx.geometry.Rectangle2D;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Explosion extends Pane {
    private ImageView imageView;
    private Circle fallbackCircle;
    private int duration = 40;
    private int currentFrame = 0;
    private boolean finished = false;

    // Animation
    private int animationTick = 0;
    private final int ANIMATION_SPEED = 6;  // ช้าลงเพื่อให้เห็นแต่ละ frame ชัดเจน
    private int currentSpriteFrame = 0;     // frame ปัจจุบัน (0, 1, 2)

    // ⭐ Boom.png frame definitions (แต่ละ frame กว้างไม่เท่ากัน)
    private static final int FRAME_HEIGHT = 34;
    private static final int TOTAL_FRAMES = 3;

    // Frame coordinates (x, y, width, height)
    private static final int[] FRAME_X = {0, 17, 50};           // x positions
    private static final int[] FRAME_WIDTH = {17, 33, 34};      // widths (frame 3 = 84-50 = 34)

    private static final int EXPLOSION_SIZE = 80;
    private Image explosionImage;

    public Explosion(Image explosionImage, double x, double y) {
        this.explosionImage = explosionImage;

        if (explosionImage != null) {
            // ⭐ ใช้ ImageView พร้อม viewport สำหรับ custom frames
            imageView = new ImageView(explosionImage);
            imageView.setFitWidth(EXPLOSION_SIZE);
            imageView.setFitHeight(EXPLOSION_SIZE);
            imageView.setPreserveRatio(false);  // ไม่ preserve เพราะแต่ละ frame กว้างไม่เท่ากัน
            imageView.setSmooth(false);

            // ตั้งค่า viewport เป็น frame แรก
            setFrameViewport(0);

            this.getChildren().add(imageView);
        } else {
            // Fallback: วงกลมสีส้ม
            fallbackCircle = new Circle(EXPLOSION_SIZE / 2, Color.ORANGE);
            this.getChildren().add(fallbackCircle);
        }

        this.setTranslateX(x - EXPLOSION_SIZE / 2);
        this.setTranslateY(y - EXPLOSION_SIZE / 2);
    }

    private void setFrameViewport(int frameIndex) {
        if (imageView != null && frameIndex >= 0 && frameIndex < TOTAL_FRAMES) {
            // ⭐ กำหนด viewport สำหรับแต่ละ frame
            int x = FRAME_X[frameIndex];
            int width = FRAME_WIDTH[frameIndex];

            Rectangle2D viewport = new Rectangle2D(x, 0, width, FRAME_HEIGHT);
            imageView.setViewport(viewport);
            currentSpriteFrame = frameIndex;

            System.out.println("💥 Explosion frame " + frameIndex + ": x=" + x + ", width=" + width);
        }
    }

    public void update() {
        currentFrame++;

        // ถ้ามีรูป → animate
        if (imageView != null && currentSpriteFrame < TOTAL_FRAMES) {
            animationTick++;
            if (animationTick >= ANIMATION_SPEED) {
                animationTick = 0;

                // เปลี่ยนไปยัง frame ถัดไป
                if (currentSpriteFrame < TOTAL_FRAMES - 1) {
                    setFrameViewport(currentSpriteFrame + 1);
                }
            }
        }

        // ⭐ Fade out หลังจาก animation เล่นจบแล้วเท่านั้น
        int animationEndFrame = TOTAL_FRAMES * ANIMATION_SPEED;  // 3 * 6 = 18 frames

        if (currentFrame >= animationEndFrame) {
            // เริ่ม fade out หลังจาก animation จบ
            int fadeFrames = currentFrame - animationEndFrame;
            double opacity = 1.0 - ((double) fadeFrames / 12);  // Fade out ภายใน 12 frames
            if (opacity < 0) opacity = 0;
            this.setOpacity(opacity);
        } else {
            // ⭐ ขณะเล่น animation ให้ opacity เต็ม
            this.setOpacity(1.0);
        }

        if (currentFrame >= duration) {
            finished = true;
        }
    }

    public boolean isFinished() {
        return finished;
    }
}