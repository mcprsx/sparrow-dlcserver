package net.momirealms.sparrow.bukkit.command.feature.teleport;

import net.kyori.adventure.text.Component;
import net.momirealms.sparrow.bukkit.SparrowBukkitPlugin;
import net.momirealms.sparrow.bukkit.command.BukkitCommandFeature;
import net.momirealms.sparrow.common.command.SparrowCommandManager;
import net.momirealms.sparrow.common.locale.MessageConstants;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.parser.standard.StringParser;

public class DelHomePlayerCommand extends BukkitCommandFeature<CommandSender> {

    public DelHomePlayerCommand(SparrowCommandManager<CommandSender> sparrowCommandManager) {
        super(sparrowCommandManager);
    }

    @Override
    public String getFeatureID() {
        return "delhome_player";
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(CommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .senderType(Player.class)
                .required("name", StringParser.stringParser())
                .handler(commandContext -> {
                    Player player = commandContext.sender();
                    String homeName = commandContext.get("name");
                    
                    if (SparrowBukkitPlugin.getInstance().getHomeManager().getHome(player.getUniqueId(), homeName) == null) {
                        handleFeedback(commandContext, MessageConstants.COMMANDS_TELEPORT_HOME_NOT_FOUND, Component.text(homeName));
                        return;
                    }
                    
                    SparrowBukkitPlugin.getInstance().getHomeManager().deleteHome(player.getUniqueId(), homeName);
                    handleFeedback(commandContext, MessageConstants.COMMANDS_TELEPORT_DELHOME_SUCCESS, Component.text(homeName));
                });
    }
}



