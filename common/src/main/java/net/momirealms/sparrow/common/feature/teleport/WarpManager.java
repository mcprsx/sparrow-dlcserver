package net.momirealms.sparrow.common.feature.teleport;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

public interface WarpManager {

    void setWarp(@NotNull String name, @NotNull TeleportLocation location);

    void deleteWarp(@NotNull String name);

    @Nullable
    TeleportLocation getWarp(@NotNull String name);

    @NotNull
    Set<String> getWarpNames();

    @NotNull
    Map<String, TeleportLocation> getWarps();

    void load();

    void save();

    void unload();
}

