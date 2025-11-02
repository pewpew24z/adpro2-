package se233.project2.controller;

/**
 * Interface สำหรับ Stage ที่สามารถ update ได้
 * ใช้กับ GameLoop เพื่อให้รองรับหลาย Stage
 */
public interface Updatable {
    void update(long now);
}