package se233.project2.model;

import javafx.scene.shape.Rectangle;

public class Platform {
    private Rectangle bounds;
    private int x;
    private int y;
    private int width;
    private int height;

    public Platform(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.bounds = new Rectangle(x, y, width, height);
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getTop() {
        return y;
    }

    public int getBottom() {
        return y + height;
    }

    public int getLeft() {
        return x;
    }

    public int getRight() {
        return x + width;
    }

    // เช็คว่าตัวละครชนด้านบนของ platform หรือไม่
    public boolean isPlayerOnTop(int playerX, int playerY, int playerWidth, int playerHeight, double velocityY) {
        int playerBottom = playerY + playerHeight;
        int playerLeft = playerX;
        int playerRight = playerX + playerWidth;

        // เช็คว่า player overlap กับ platform แนวนอนจริงๆ
        boolean horizontalOverlap = playerRight > x && playerLeft < (x + width);

        // เช็คว่า player อยู่ใกล้ขอบบนของ platform และกำลังตกลงมา
        boolean verticalOverlap = playerBottom >= y && playerBottom <= (y + 10) && velocityY >= 0;

        // ต้อง overlap ทั้งแนวนอนและแนวตั้ง
        return horizontalOverlap && verticalOverlap;
    }
}