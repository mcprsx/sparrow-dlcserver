package net.momirealms.sparrow.bukkit.command.feature.teleport;

import net.kyori.adventure.text.Component;
import net.momirealms.sparrow.bukkit.command.BukkitCommandFeature;
import net.momirealms.sparrow.bukkit.command.key.SparrowBukkitArgumentKeys;
import net.momirealms.sparrow.common.command.SparrowCommandManager;
import net.momirealms.sparrow.common.command.key.SparrowMetaKeys;
import net.momirealms.sparrow.common.locale.MessageConstants;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.bukkit.data.MultiplePlayerSelector;
import org.incendo.cloud.bukkit.parser.PlayerParser;
import org.incendo.cloud.bukkit.parser.selector.MultiplePlayerSelectorParser;

public class TpAdminCommand extends BukkitCommandFeature<CommandSender> {

    public TpAdminCommand(SparrowCommandManager<CommandSender> sparrowCommandManager) {
        super(sparrowCommandManager);
    }

    @Override
    public String getFeatureID() {
        return "tp_admin";
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(CommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        // /tp <player_selector> <target> - 传送玩家到目标（管理员）
        return builder
                .required(SparrowBukkitArgumentKeys.PLAYER_SELECTOR, MultiplePlayerSelectorParser.multiplePlayerSelectorParser())
                .meta(SparrowMetaKeys.ALLOW_EMPTY_PLAYER_SELECTOR, false)
                .required("target", PlayerParser.playerParser())
                .handler(commandContext -> {
                    MultiplePlayerSelector selector = commandContext.get(SparrowBukkitArgumentKeys.PLAYER_SELECTOR);
                    Player target = commandContext.get("target");
                    
                    var players = selector.values();
                    for (Player player : players) {
                        player.teleportAsync(target.getLocation());
                    }
                    
                    var pair = resolveSelector(selector, MessageConstants.COMMANDS_TELEPORT_TP_SUCCESS_SINGLE, MessageConstants.COMMANDS_TELEPORT_TP_SUCCESS_MULTIPLE);
                    handleFeedback(commandContext, pair.left(), pair.right(), Component.text(target.getName()));
                });
    }
}

