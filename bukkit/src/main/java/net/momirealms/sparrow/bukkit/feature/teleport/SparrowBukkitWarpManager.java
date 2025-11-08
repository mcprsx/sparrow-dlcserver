package net.momirealms.sparrow.bukkit.feature.teleport;

import net.momirealms.sparrow.bukkit.SparrowBukkitPlugin;
import net.momirealms.sparrow.common.feature.teleport.TeleportLocation;
import net.momirealms.sparrow.common.feature.teleport.WarpManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class SparrowBukkitWarpManager implements WarpManager {

    private final SparrowBukkitPlugin plugin;
    private final Map<String, TeleportLocation> warps = new ConcurrentHashMap<>();
    private final File dataFile;

    public SparrowBukkitWarpManager(SparrowBukkitPlugin plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getLoader().getDataFolder(), "warps.yml");
    }

    @Override
    public void setWarp(@NotNull String name, @NotNull TeleportLocation location) {
        warps.put(name.toLowerCase(), location);
        save();
    }

    @Override
    public void deleteWarp(@NotNull String name) {
        warps.remove(name.toLowerCase());
        save();
    }

    @Override
    @Nullable
    public TeleportLocation getWarp(@NotNull String name) {
        return warps.get(name.toLowerCase());
    }

    @Override
    @NotNull
    public Set<String> getWarpNames() {
        return warps.keySet();
    }

    @Override
    @NotNull
    public Map<String, TeleportLocation> getWarps() {
        return new HashMap<>(warps);
    }

    @Override
    public void load() {
        if (!dataFile.exists()) {
            return;
        }

        try {
            dev.dejvokep.boostedyaml.YamlDocument document = dev.dejvokep.boostedyaml.YamlDocument.create(dataFile);
            for (Object key : document.getRoutesAsStrings(false)) {
                String warpName = key.toString();
                String locationStr = document.getString(warpName);
                if (locationStr != null) {
                    try {
                        warps.put(warpName.toLowerCase(), TeleportLocation.fromString(locationStr));
                    } catch (Exception e) {
                        plugin.getBootstrap().getPluginLogger().warn("Failed to load warp " + warpName, e);
                    }
                }
            }
            plugin.getBootstrap().getPluginLogger().info("Loaded " + warps.size() + " warps");
        } catch (Exception e) {
            plugin.getBootstrap().getPluginLogger().severe("Failed to load warps", e);
        }
    }

    @Override
    public void save() {
        try {
            dev.dejvokep.boostedyaml.YamlDocument document = dev.dejvokep.boostedyaml.YamlDocument.create(dataFile);
            document.clear();
            
            for (Map.Entry<String, TeleportLocation> entry : warps.entrySet()) {
                document.set(entry.getKey(), entry.getValue().toString());
            }
            
            document.save();
        } catch (Exception e) {
            plugin.getBootstrap().getPluginLogger().severe("Failed to save warps", e);
        }
    }

    @Override
    public void unload() {
        save();
        warps.clear();
    }
}

