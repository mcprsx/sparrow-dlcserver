package net.momirealms.sparrow.bukkit.command.feature.teleport;

import net.kyori.adventure.text.Component;
import net.momirealms.sparrow.bukkit.SparrowBukkitPlugin;
import net.momirealms.sparrow.bukkit.command.BukkitCommandFeature;
import net.momirealms.sparrow.bukkit.feature.teleport.TeleportUtils;
import net.momirealms.sparrow.common.command.SparrowCommandManager;
import net.momirealms.sparrow.common.feature.teleport.TeleportLocation;
import net.momirealms.sparrow.common.locale.MessageConstants;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;

public class SpawnPlayerCommand extends BukkitCommandFeature<CommandSender> {

    public SpawnPlayerCommand(SparrowCommandManager<CommandSender> sparrowCommandManager) {
        super(sparrowCommandManager);
    }

    @Override
    public String getFeatureID() {
        return "spawn_player";
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(CommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .senderType(Player.class)
                .handler(commandContext -> {
                    Player player = commandContext.sender();
                    TeleportLocation spawn = SparrowBukkitPlugin.getInstance().getSpawnManager().getSpawn();
                    
                    if (spawn == null) {
                        handleFeedback(commandContext, MessageConstants.COMMANDS_TELEPORT_SPAWN_NOT_SET);
                        return;
                    }
                    
                    if (TeleportUtils.teleport(player, spawn)) {
                        handleFeedback(commandContext, MessageConstants.COMMANDS_TELEPORT_SPAWN_SUCCESS);
                    } else {
                        handleFeedback(commandContext, MessageConstants.COMMANDS_TELEPORT_SPAWN_FAILED);
                    }
                });
    }
}

