package net.momirealms.sparrow.bukkit.feature.teleport;

import net.momirealms.sparrow.bukkit.SparrowBukkitPlugin;
import net.momirealms.sparrow.common.feature.teleport.SpawnManager;
import net.momirealms.sparrow.common.feature.teleport.TeleportLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class SparrowBukkitSpawnManager implements SpawnManager {

    private final SparrowBukkitPlugin plugin;
    private TeleportLocation spawn;
    private final File dataFile;

    public SparrowBukkitSpawnManager(SparrowBukkitPlugin plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getLoader().getDataFolder(), "spawn.yml");
    }

    @Override
    public void setSpawn(@NotNull TeleportLocation location) {
        this.spawn = location;
        save();
    }

    @Override
    @Nullable
    public TeleportLocation getSpawn() {
        return spawn;
    }

    @Override
    public boolean hasSpawn() {
        return spawn != null;
    }

    @Override
    public void load() {
        if (!dataFile.exists()) {
            return;
        }

        try {
            dev.dejvokep.boostedyaml.YamlDocument document = dev.dejvokep.boostedyaml.YamlDocument.create(dataFile);
            String locationStr = document.getString("spawn");
            if (locationStr != null) {
                spawn = TeleportLocation.fromString(locationStr);
                plugin.getBootstrap().getPluginLogger().info("Loaded spawn location");
            }
        } catch (Exception e) {
            plugin.getBootstrap().getPluginLogger().severe("Failed to load spawn", e);
        }
    }

    @Override
    public void save() {
        try {
            dev.dejvokep.boostedyaml.YamlDocument document = dev.dejvokep.boostedyaml.YamlDocument.create(dataFile);
            if (spawn != null) {
                document.set("spawn", spawn.toString());
            } else {
                document.set("spawn", null);
            }
            document.save();
        } catch (Exception e) {
            plugin.getBootstrap().getPluginLogger().severe("Failed to save spawn", e);
        }
    }

    @Override
    public void unload() {
        save();
        spawn = null;
    }
}

