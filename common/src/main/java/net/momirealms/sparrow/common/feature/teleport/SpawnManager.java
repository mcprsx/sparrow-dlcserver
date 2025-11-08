package net.momirealms.sparrow.common.feature.teleport;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface SpawnManager {

    void setSpawn(@NotNull TeleportLocation location);

    @Nullable
    TeleportLocation getSpawn();

    boolean hasSpawn();

    void load();

    void save();

    void unload();
}

