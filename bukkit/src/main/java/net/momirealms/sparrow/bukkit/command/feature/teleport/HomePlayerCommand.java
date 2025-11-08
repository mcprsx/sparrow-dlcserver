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

public class HomePlayerCommand extends BukkitCommandFeature<CommandSender> {

    public HomePlayerCommand(SparrowCommandManager<CommandSender> sparrowCommandManager) {
        super(sparrowCommandManager);
    }

    @Override
    public String getFeatureID() {
        return "home_player";
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(CommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .senderType(Player.class)
                .handler(commandContext -> {
                    Player player = commandContext.sender();
                    
                    // 直接打开 Home GUI
                    int homeCount = SparrowBukkitPlugin.getInstance().getHomeManager().getHomeCount(player.getUniqueId());
                    
                    if (homeCount == 0) {
                        handleFeedback(commandContext, MessageConstants.COMMANDS_TELEPORT_HOMES_EMPTY);
                        return;
                    }
                    
                    SparrowBukkitPlugin.getInstance().getHomeGUIListener().openHomeGUI(player, 0);
                });
    }
}



