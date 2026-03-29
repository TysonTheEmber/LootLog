package dev.tysontheember.lootlog.fabric;

import dev.tysontheember.lootlog.SoundBridge;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public class FabricSoundBridge implements SoundBridge {

    @Override
    public void playSound(String soundId, float volume, float pitch) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        String[] parts = soundId.split(":", 2);
        ResourceLocation loc = parts.length == 2
                ? ResourceLocation.fromNamespaceAndPath(parts[0], parts[1])
                : ResourceLocation.withDefaultNamespace(soundId);

        mc.getSoundManager().play(SimpleSoundInstance.forUI(
                SoundEvent.createVariableRangeEvent(loc), pitch, volume));
    }
}
