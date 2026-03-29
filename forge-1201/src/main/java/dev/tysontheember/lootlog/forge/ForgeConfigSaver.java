package dev.tysontheember.lootlog.forge;

/**
 * Save callback for the YACL config screen on Forge.
 * Writes current POJO state back to ForgeConfigSpec and saves to TOML.
 */
public class ForgeConfigSaver {

    public static void save() {
        ForgeConfig.saveFromPojo();
    }
}
