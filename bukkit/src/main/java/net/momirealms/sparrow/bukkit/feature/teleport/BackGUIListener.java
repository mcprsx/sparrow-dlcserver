package net.momirealms.sparrow.bukkit.feature.teleport;

import net.kyori.adventure.text.Component;
import net.momirealms.sparrow.bukkit.SparrowBukkitPlugin;
import net.momirealms.sparrow.common.feature.teleport.TeleportHistory;
import net.momirealms.sparrow.common.feature.teleport.TeleportLocation;
import net.momirealms.sparrow.common.helper.AdventureHelper;
import net.momirealms.sparrow.common.locale.MessageConstants;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class BackGUIListener implements Listener {
    
    private final SparrowBukkitPlugin plugin;
    
    public BackGUIListener(SparrowBukkitPlugin plugin) {
        this.plugin = plugin;
    }
    
    public void openBackGUI(Player player) {
        var config = plugin.getConfigManager().loadConfig("config.yml");
        
        // 读取配置
        String title = config.getString("back-system.gui.title", "<dark_gray>傳送歷史記錄</dark_gray>");
        int size = config.getInt("back-system.gui.size", 36);
        long expireTime = config.getLong("back-system.expire-time", 86400000L); // 默认1天
        
        // 清理过期记录
        TeleportHistory history = plugin.getTeleportHistoryManager().getHistory(player.getUniqueId());
        if (expireTime > 0) {
            history.removeExpired(expireTime);
        }
        
        List<TeleportLocation> locations = history.getHistory();
        
        // 创建选单 - 转换 & 颜色代码
        String coloredTitle = org.bukkit.ChatColor.translateAlternateColorCodes('&', title);
        Inventory inv = Bukkit.createInventory(new BackInventoryHolder(), size, coloredTitle);
        
        // 填充传送点（第一列空，每行8个）
        for (int row = 0; row < 3; row++) {
            // 第一个格子留空
            
            // 填充传送点 (8个每行)
            for (int col = 1; col <= 8; col++) {
                int index = row * 8 + (col - 1);
                if (index < locations.size()) {
                    inv.setItem(row * 9 + col, createLocationItem(config, index + 1, locations.get(index)));
                }
            }
        }
        
        player.openInventory(inv);
    }
    
    private ItemStack createEmptySlot(dev.dejvokep.boostedyaml.YamlDocument config) {
        String materialName = config.getString("back-system.gui.empty-slot.material", "GRAY_STAINED_GLASS_PANE");
        String displayName = config.getString("back-system.gui.empty-slot.display-name", " ");
        int customModelData = config.getInt("back-system.gui.empty-slot.custom-model-data", 0);
        
        Material material = Material.getMaterial(materialName);
        if (material == null) material = Material.AIR;
        
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        
        // 使用传统 API
        meta.setDisplayName(org.bukkit.ChatColor.translateAlternateColorCodes('&', displayName));
        
        if (customModelData > 0) {
            meta.setCustomModelData(customModelData);
        }
        
        item.setItemMeta(meta);
        return item;
    }
    
    private ItemStack createLocationItem(dev.dejvokep.boostedyaml.YamlDocument config, int number, TeleportLocation location) {
        String materialName = config.getString("back-system.gui.history-item.material", "ENDER_PEARL");
        String displayNameTemplate = config.getString("back-system.gui.history-item.display-name", "<gold>傳送點 #<number></gold>");
        List<String> loreTemplate = config.getStringList("back-system.gui.history-item.lore");
        int customModelData = config.getInt("back-system.gui.history-item.custom-model-data", 0);
        
        Material material = Material.getMaterial(materialName);
        if (material == null) material = Material.ENDER_PEARL;
        
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        
        if (customModelData > 0) {
            meta.setCustomModelData(customModelData);
        }
        
        // 替换显示名称中的占位符
        String displayName = displayNameTemplate
                .replace("<number>", String.valueOf(number))
                .replace("<world>", location.getWorld())
                .replace("<x>", String.format("%.1f", location.getX()))
                .replace("<y>", String.format("%.1f", location.getY()))
                .replace("<z>", String.format("%.1f", location.getZ()));
        
        // 转换 & 颜色代码
        meta.setDisplayName(org.bukkit.ChatColor.translateAlternateColorCodes('&', displayName));
        
        // 替换占位符并转换颜色代码
        List<String> lore = new ArrayList<>();
        for (String line : loreTemplate) {
            String processedLine = line
                    .replace("<number>", String.valueOf(number))
                    .replace("<world>", location.getWorld())
                    .replace("<x>", String.format("%.1f", location.getX()))
                    .replace("<y>", String.format("%.1f", location.getY()))
                    .replace("<z>", String.format("%.1f", location.getZ()));
            // 转换 & 颜色代码
            lore.add(org.bukkit.ChatColor.translateAlternateColorCodes('&', processedLine));
        }
        
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof BackInventoryHolder)) {
            return;
        }
        
        event.setCancelled(true); // 取消所有点击
        
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        
        int slot = event.getSlot();
        int row = slot / 9;
        int col = slot % 9;
        
        // 第一列或第四行是空的
        if (col == 0 || row >= 3) {
            return;
        }
        
        // 计算索引 (每行8个点)
        int index = row * 8 + (col - 1);
        
        TeleportHistory history = plugin.getTeleportHistoryManager().getHistory(player.getUniqueId());
        TeleportLocation location = history.getLocation(index);
        
        if (location != null) {
            // 右键删除
            if (event.isRightClick()) {
                history.removeLocation(index);
                var sender = plugin.getSenderFactory().wrap(player);
                sender.sendMessage(net.momirealms.sparrow.common.locale.TranslationManager.render(
                        MessageConstants.COMMANDS_TELEPORT_BACK_DELETED.arguments(Component.text(index + 1)).build()));
                // 重新打开 GUI
                openBackGUI(player);
                return;
            }
            
            // 左键传送
            player.closeInventory();
            
            // 使用元数据标记这次传送不记录
            player.setMetadata("sparrow_skip_back_record", new org.bukkit.metadata.FixedMetadataValue(plugin.getLoader(), true));
            
            if (TeleportUtils.teleport(player, location)) {
                var sender = plugin.getSenderFactory().wrap(player);
                sender.sendMessage(net.momirealms.sparrow.common.locale.TranslationManager.render(
                        MessageConstants.COMMANDS_TELEPORT_BACK_SUCCESS.arguments(Component.text(index + 1)).build()));
            } else {
                var sender = plugin.getSenderFactory().wrap(player);
                sender.sendMessage(net.momirealms.sparrow.common.locale.TranslationManager.render(
                        MessageConstants.COMMANDS_TELEPORT_BACK_FAILED.build()));
            }
        }
    }
}

