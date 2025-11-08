package net.momirealms.sparrow.bukkit.feature.teleport;

import net.momirealms.sparrow.bukkit.SparrowBukkitPlugin;
import net.momirealms.sparrow.common.feature.teleport.HomeManager;
import net.momirealms.sparrow.common.feature.teleport.TeleportLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SparrowBukkitHomeManager implements HomeManager {

    private final SparrowBukkitPlugin plugin;
    private final Map<UUID, Map<String, TeleportLocation>> homes = new ConcurrentHashMap<>();
    private final File dataFile;

    public SparrowBukkitHomeManager(SparrowBukkitPlugin plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getLoader().getDataFolder(), "homes.yml");
    }

    @Override
    public void setHome(@NotNull UUID player, @NotNull String name, @NotNull TeleportLocation location) {
        homes.computeIfAbsent(player, k -> new ConcurrentHashMap<>()).put(name.toLowerCase(), location);
        save();
    }

    @Override
    public void deleteHome(@NotNull UUID player, @NotNull String name) {
        Map<String, TeleportLocation> playerHomes = homes.get(player);
        if (playerHomes != null) {
            playerHomes.remove(name.toLowerCase());
            if (playerHomes.isEmpty()) {
                homes.remove(player);
            }
            save();
        }
    }

    @Override
    @Nullable
    public TeleportLocation getHome(@NotNull UUID player, @NotNull String name) {
        Map<String, TeleportLocation> playerHomes = homes.get(player);
        return playerHomes != null ? playerHomes.get(name.toLowerCase()) : null;
    }

    @Override
    @NotNull
    public Map<String, TeleportLocation> getHomes(@NotNull UUID player) {
        return new HashMap<>(homes.getOrDefault(player, new HashMap<>()));
    }

    @Override
    public int getHomeCount(@NotNull UUID player) {
        Map<String, TeleportLocation> playerHomes = homes.get(player);
        return playerHomes != null ? playerHomes.size() : 0;
    }

    @Override
    public void load() {
        if (!dataFile.exists()) {
            return;
        }

        try {
            dev.dejvokep.boostedyaml.YamlDocument document = dev.dejvokep.boostedyaml.YamlDocument.create(dataFile);
            for (Object key : document.getRoutesAsStrings(false)) {
                UUID uuid = UUID.fromString(key.toString());
                dev.dejvokep.boostedyaml.block.implementation.Section section = document.getSection(key.toString());
                if (section != null) {
                    Map<String, TeleportLocation> playerHomes = new ConcurrentHashMap<>();
                    for (Object homeName : section.getRoutesAsStrings(false)) {
                        String locationStr = section.getString(homeName.toString());
                        if (locationStr != null) {
                            try {
                                playerHomes.put(homeName.toString().toLowerCase(), TeleportLocation.fromString(locationStr));
                            } catch (Exception e) {
                                plugin.getBootstrap().getPluginLogger().warn("Failed to load home " + homeName + " for player " + uuid, e);
                            }
                        }
                    }
                    homes.put(uuid, playerHomes);
                }
            }
            plugin.getBootstrap().getPluginLogger().info("Loaded " + homes.size() + " players' homes");
        } catch (Exception e) {
            plugin.getBootstrap().getPluginLogger().severe("Failed to load homes", e);
        }
    }

    @Override
    public void save() {
        try {
            dev.dejvokep.boostedyaml.YamlDocument document = dev.dejvokep.boostedyaml.YamlDocument.create(dataFile);
            document.clear();
            
            for (Map.Entry<UUID, Map<String, TeleportLocation>> entry : homes.entrySet()) {
                for (Map.Entry<String, TeleportLocation> homeEntry : entry.getValue().entrySet()) {
                    document.set(entry.getKey() + "." + homeEntry.getKey(), homeEntry.getValue().toString());
                }
            }
            
            document.save();
        } catch (Exception e) {
            plugin.getBootstrap().getPluginLogger().severe("Failed to save homes", e);
        }
    }

    @Override
    public void unload() {
        save();
        homes.clear();
    }
}

