package dev.tysontheember.lootlog;

/**
 * Abstraction over platform-specific sound playback.
 * Implementations use the loader's sound API.
 * Set by each platform's entrypoint at mod init time.
 */
public interface SoundBridge {

    /**
     * Play a pickup notification sound.
     *
     * @param soundId  namespaced sound ID (e.g., "minecraft:entity.item.pickup")
     * @param volume   sound volume (0.0 - 1.0)
     * @param pitch    sound pitch (0.5 - 2.0)
     */
    void playSound(String soundId, float volume, float pitch);
}
