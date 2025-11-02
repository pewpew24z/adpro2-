package se233.project2.controller;

import javafx.scene.media.AudioClip;

/**
 * SoundController - จัดการเสียงทั้งหมดในเกม (ใช้ AudioClip)
 */
public class SoundController {
    private static SoundController instance;

    private AudioClip startScreenMusic;
    private AudioClip bulletSound;
    private AudioClip deadSound;

    // Volume settings
    private double masterVolume = 1.0;
    private final double START_MUSIC_VOLUME = 0.4;
    private final double BULLET_VOLUME = 0.3;
    private final double DEAD_VOLUME = 0.6;

    private SoundController() {
        loadSounds();
    }

    public static SoundController getInstance() {
        if (instance == null) {
            instance = new SoundController();
        }
        return instance;
    }

    private void loadSounds() {
        try {
            // Start screen music (loops)
            String startPath = getClass().getResource("/se233/project2/assets/sounds/start-sound.mp3").toString();
            startScreenMusic = new AudioClip(startPath);
            startScreenMusic.setVolume(START_MUSIC_VOLUME * masterVolume);
            startScreenMusic.setCycleCount(AudioClip.INDEFINITE); // วนลูปไม่สิ้นสุด

            // Bullet sound
            String bulletPath = getClass().getResource("/se233/project2/assets/sounds/bullet-sound.mp3").toString();
            bulletSound = new AudioClip(bulletPath);
            bulletSound.setVolume(BULLET_VOLUME * masterVolume);

            // Dead sound
            String deadPath = getClass().getResource("/se233/project2/assets/sounds/dead-sound.mp3").toString();
            deadSound = new AudioClip(deadPath);
            deadSound.setVolume(DEAD_VOLUME * masterVolume);

            System.out.println("✅ All sounds loaded successfully (AudioClip)");

        } catch (Exception e) {
            System.err.println("⚠️ Error loading sounds: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * เล่นเพลงหน้า Start Screen (วนลูป)
     */
    public void playStartScreenMusic() {
        if (startScreenMusic != null) {
            startScreenMusic.play();
        }
    }

    /**
     * หยุดเพลงหน้า Start Screen
     */
    public void stopStartScreenMusic() {
        if (startScreenMusic != null) {
            startScreenMusic.stop();
        }
    }

    /**
     * เล่นเสียงยิงกระสุน
     * AudioClip สามารถเล่นซ้อนทับกันได้ (overlapping)
     */
    public void playBulletSound() {
        if (bulletSound != null) {
            bulletSound.play(); // เล่นทันที ไม่ต้อง seek
        }
    }

    /**
     * เล่นเสียงตาย
     */
    public void playDeadSound() {
        if (deadSound != null) {
            deadSound.play();
        }
    }

    /**
     * หยุดเสียงทั้งหมด
     */
    public void stopAllSounds() {
        if (startScreenMusic != null) startScreenMusic.stop();
        if (bulletSound != null) bulletSound.stop();
        if (deadSound != null) deadSound.stop();
    }

    /**
     * ตั้งระดับเสียงทั้งหมด (0.0 - 1.0)
     */
    public void setMasterVolume(double volume) {
        this.masterVolume = Math.max(0.0, Math.min(1.0, volume)); // clamp 0-1

        if (startScreenMusic != null) {
            startScreenMusic.setVolume(START_MUSIC_VOLUME * masterVolume);
        }
        if (bulletSound != null) {
            bulletSound.setVolume(BULLET_VOLUME * masterVolume);
        }
        if (deadSound != null) {
            deadSound.setVolume(DEAD_VOLUME * masterVolume);
        }
    }

    /**
     * ดึงระดับเสียงปัจจุบัน
     */
    public double getMasterVolume() {
        return masterVolume;
    }

    /**
     * เช็คว่าเพลง Start Screen กำลังเล่นอยู่หรือไม่
     */
    public boolean isStartMusicPlaying() {
        return startScreenMusic != null && startScreenMusic.isPlaying();
    }
}
