package dev.tysontheember.lootlog.fabric;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class ModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            try {
                Class.forName("dev.isxander.yacl3.api.YetAnotherConfigLib");
                return dev.tysontheember.lootlog.config.LootLogConfigScreen.create(
                        parent, FabricConfig::saveFromPojo);
            } catch (ClassNotFoundException e) {
                return null;
            }
        };
    }
}
