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
import org.incendo.cloud.parser.standard.StringParser;

public class WarpPlayerCommand extends BukkitCommandFeature<CommandSender> {

    public WarpPlayerCommand(SparrowCommandManager<CommandSender> sparrowCommandManager) {
        super(sparrowCommandManager);
    }

    @Override
    public String getFeatureID() {
        return "warp_player";
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(CommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .senderType(Player.class)
                .required("name", StringParser.stringParser())
                .handler(commandContext -> {
                    Player player = commandContext.sender();
                    String warpName = commandContext.get("name");
                    
                    TeleportLocation warp = SparrowBukkitPlugin.getInstance().getWarpManager().getWarp(warpName);
                    
                    if (warp == null) {
                        handleFeedback(commandContext, MessageConstants.COMMANDS_TELEPORT_WARP_NOT_FOUND, Component.text(warpName));
                        return;
                    }
                    
                    if (TeleportUtils.teleport(player, warp)) {
                        handleFeedback(commandContext, MessageConstants.COMMANDS_TELEPORT_WARP_SUCCESS, Component.text(warpName));
                    } else {
                        handleFeedback(commandContext, MessageConstants.COMMANDS_TELEPORT_WARP_FAILED);
                    }
                });
    }
}



