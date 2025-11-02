package se233.project2.model;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class AnimatedSprite extends ImageView {
    private Image spriteSheet;
    private int totalColumns;
    private int width;
    private int height;
    private int offsetX;
    private int offsetY;
    private int spacingX;  // distance between frames (width + border)
    private int spacingY;  // distance between (height + border)
    private int curColumnIndex = 0;
    private int curRowIndex = 0;

    // For custom animation ranges
    private int startColumn = 0;
    private int frameCount = 1;

    // Constructor สำหรับ sprite sheet ที่มีขอบ
    public AnimatedSprite(Image spriteSheet, int totalColumns, int frameCount,
                          int offsetX, int offsetY, int width, int height,
                          int spacingX, int spacingY) {
        this.spriteSheet = spriteSheet;
        this.totalColumns = totalColumns;
        this.width = width;
        this.height = height;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.spacingX = spacingX;
        this.spacingY = spacingY;
        this.frameCount = frameCount;
        this.setImage(spriteSheet);
        this.setViewport(new Rectangle2D(offsetX, offsetY, width, height));
    }

    public AnimatedSprite(Image spriteSheet, int columns, int count,
                          int offsetX, int offsetY, int width, int height) {
        this(spriteSheet, columns, count, offsetX, offsetY, width, height, width, height);
    }

    public void tick() {
        curColumnIndex = (curColumnIndex + 1) % frameCount;
        interpolate();
    }

    public void interpolate() {
        final int x = offsetX + (startColumn + curColumnIndex) * spacingX;
        final int y = offsetY + curRowIndex * spacingY;
        this.setViewport(new Rectangle2D(x, y, width, height));
    }

    public void setAnimation(int row, int startCol, int frames) {
        this.curRowIndex = row;
        this.startColumn = startCol;
        this.frameCount = frames;
        this.curColumnIndex = 0;
        interpolate();
    }

    public void setRowIndex(int rowIndex) {
        setAnimation(rowIndex, 0, 1);
    }

    public void setRow(int row) {
        setRowIndex(row);
    }

    public int getRowIndex() {
        return curRowIndex;
    }

    public void reset() {
        curColumnIndex = 0;
        interpolate();
    }
}