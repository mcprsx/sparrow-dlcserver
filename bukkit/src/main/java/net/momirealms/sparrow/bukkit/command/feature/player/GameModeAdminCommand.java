package net.momirealms.sparrow.bukkit.command.feature.player;

import net.kyori.adventure.text.Component;
import net.momirealms.sparrow.bukkit.command.BukkitCommandFeature;
import net.momirealms.sparrow.bukkit.command.key.SparrowBukkitArgumentKeys;
import net.momirealms.sparrow.common.command.SparrowCommandManager;
import net.momirealms.sparrow.common.command.key.SparrowFlagKeys;
import net.momirealms.sparrow.common.command.key.SparrowMetaKeys;
import net.momirealms.sparrow.common.locale.MessageConstants;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.bukkit.data.MultiplePlayerSelector;
import org.incendo.cloud.bukkit.parser.selector.MultiplePlayerSelectorParser;
import org.incendo.cloud.parser.standard.IntegerParser;

public class GameModeAdminCommand extends BukkitCommandFeature<CommandSender> {

    public GameModeAdminCommand(SparrowCommandManager<CommandSender> sparrowCommandManager) {
        super(sparrowCommandManager);
    }

    @Override
    public String getFeatureID() {
        return "gamemode_admin";
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(CommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .required(SparrowBukkitArgumentKeys.PLAYER_SELECTOR, MultiplePlayerSelectorParser.multiplePlayerSelectorParser())
                .meta(SparrowMetaKeys.ALLOW_EMPTY_PLAYER_SELECTOR, false)
                .required("mode", IntegerParser.integerParser(0, 3))
                .flag(SparrowFlagKeys.SILENT_FLAG)
                .handler(commandContext -> {
                    MultiplePlayerSelector selector = commandContext.get(SparrowBukkitArgumentKeys.PLAYER_SELECTOR);
                    int modeId = commandContext.get("mode");
                    GameMode mode = getGameModeFromId(modeId);
                    var players = selector.values();
                    
                    if (players.size() == 1) {
                        final Player player = players.iterator().next();
                        if (player.getGameMode() == mode) {
                            handleFeedback(commandContext, MessageConstants.COMMANDS_ADMIN_GAMEMODE_FAILED_SAME,
                                Component.text(player.getName()),
                                Component.text(getGameModeName(mode)));
                            return;
                        }
                        player.setGameMode(mode);
                        handleFeedback(commandContext, MessageConstants.COMMANDS_ADMIN_GAMEMODE_SUCCESS_SINGLE,
                            Component.text(player.getName()),
                            Component.text(getGameModeName(mode)));
                    } else {
                        int changed = 0;
                        for (Player player : players) {
                            if (player.getGameMode() != mode) {
                                player.setGameMode(mode);
                                changed++;
                            }
                        }
                        if (changed == 0) {
                            handleFeedback(commandContext, MessageConstants.COMMANDS_ADMIN_GAMEMODE_FAILED_NO_CHANGE,
                                Component.text(getGameModeName(mode)));
                            return;
                        }
                        handleFeedback(commandContext, MessageConstants.COMMANDS_ADMIN_GAMEMODE_SUCCESS_MULTIPLE,
                            Component.text(changed),
                            Component.text(getGameModeName(mode)));
                    }
                });
    }
    
    private GameMode getGameModeFromId(int id) {
        return switch (id) {
            case 0 -> GameMode.SURVIVAL;
            case 1 -> GameMode.CREATIVE;
            case 2 -> GameMode.ADVENTURE;
            case 3 -> GameMode.SPECTATOR;
            default -> GameMode.SURVIVAL;
        };
    }
    
    private String getGameModeName(GameMode mode) {
        return switch (mode) {
            case SURVIVAL -> "生存模式";
            case CREATIVE -> "創造模式";
            case ADVENTURE -> "冒險模式";
            case SPECTATOR -> "旁觀模式";
        };
    }
}

