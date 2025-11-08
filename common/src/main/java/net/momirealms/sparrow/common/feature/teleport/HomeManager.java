package net.momirealms.sparrow.common.feature.teleport;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;

public interface HomeManager {

    void setHome(@NotNull UUID player, @NotNull String name, @NotNull TeleportLocation location);

    void deleteHome(@NotNull UUID player, @NotNull String name);

    @Nullable
    TeleportLocation getHome(@NotNull UUID player, @NotNull String name);

    @NotNull
    Map<String, TeleportLocation> getHomes(@NotNull UUID player);

    int getHomeCount(@NotNull UUID player);

    void load();

    void save();

    void unload();
}

