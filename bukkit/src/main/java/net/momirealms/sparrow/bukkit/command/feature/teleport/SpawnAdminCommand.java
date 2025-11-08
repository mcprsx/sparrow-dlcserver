package net.momirealms.sparrow.bukkit.command.feature.teleport;

import net.momirealms.sparrow.bukkit.SparrowBukkitPlugin;
import net.momirealms.sparrow.bukkit.command.BukkitCommandFeature;
import net.momirealms.sparrow.bukkit.command.key.SparrowBukkitArgumentKeys;
import net.momirealms.sparrow.bukkit.feature.teleport.TeleportUtils;
import net.momirealms.sparrow.common.command.SparrowCommandManager;
import net.momirealms.sparrow.common.command.key.SparrowMetaKeys;
import net.momirealms.sparrow.common.feature.teleport.TeleportLocation;
import net.momirealms.sparrow.common.locale.MessageConstants;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.bukkit.data.MultiplePlayerSelector;
import org.incendo.cloud.bukkit.parser.selector.MultiplePlayerSelectorParser;

public class SpawnAdminCommand extends BukkitCommandFeature<CommandSender> {

    public SpawnAdminCommand(SparrowCommandManager<CommandSender> sparrowCommandManager) {
        super(sparrowCommandManager);
    }

    @Override
    public String getFeatureID() {
        return "spawn_admin";
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(CommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .required(SparrowBukkitArgumentKeys.PLAYER_SELECTOR, MultiplePlayerSelectorParser.multiplePlayerSelectorParser())
                .meta(SparrowMetaKeys.ALLOW_EMPTY_PLAYER_SELECTOR, false)
                .handler(commandContext -> {
                    MultiplePlayerSelector selector = commandContext.get(SparrowBukkitArgumentKeys.PLAYER_SELECTOR);
                    TeleportLocation spawn = SparrowBukkitPlugin.getInstance().getSpawnManager().getSpawn();
                    
                    if (spawn == null) {
                        handleFeedback(commandContext, MessageConstants.COMMANDS_TELEPORT_SPAWN_NOT_SET);
                        return;
                    }
                    
                    var players = selector.values();
                    for (Player player : players) {
                        TeleportUtils.teleport(player, spawn);
                    }
                    var pair = resolveSelector(selector, MessageConstants.COMMANDS_TELEPORT_SPAWN_SUCCESS_ADMIN, MessageConstants.COMMANDS_TELEPORT_SPAWN_SUCCESS_ADMIN);
                    handleFeedback(commandContext, pair.left(), pair.right());
                });
    }
}



