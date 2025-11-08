package net.momirealms.sparrow.bukkit.feature.teleport;

import net.kyori.adventure.text.Component;
import net.momirealms.sparrow.bukkit.SparrowBukkitPlugin;
import net.momirealms.sparrow.common.feature.teleport.TeleportLocation;
import net.momirealms.sparrow.common.helper.AdventureHelper;
import net.momirealms.sparrow.common.locale.MessageConstants;
import net.momirealms.sparrow.common.locale.TranslationManager;
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
import java.util.Map;

public class HomeGUIListener implements Listener {
    
    private final SparrowBukkitPlugin plugin;
    private static final int ITEMS_PER_PAGE = 24; // 8x3 = 24 个家
    
    public HomeGUIListener(SparrowBukkitPlugin plugin) {
        this.plugin = plugin;
    }
    
    public void openHomeGUI(Player player, int page) {
        var config = plugin.getConfigManager().loadConfig("config.yml");
        
        // 读取配置
        String title = config.getString("home-system.gui.title", "<dark_gray>我的家</dark_gray>");
        int size = config.getInt("home-system.gui.size", 36);
        
        // 获取玩家的家列表
        Map<String, TeleportLocation> homes = plugin.getHomeManager().getHomes(player.getUniqueId());
        List<Map.Entry<String, TeleportLocation>> homeList = new ArrayList<>(homes.entrySet());
        
        // 计算总页数
        int totalPages = (int) Math.ceil((double) homeList.size() / ITEMS_PER_PAGE);
        if (totalPages == 0) totalPages = 1;
        
        // 确保页码有效
        if (page < 0) page = 0;
        if (page >= totalPages) page = totalPages - 1;
        
        // 创建标题（替换占位符并转换颜色代码）
        String finalTitle = title.replace("<page>", String.valueOf(page + 1))
                .replace("<total_pages>", String.valueOf(totalPages));
        
        // 只转换 & 颜色代码，保留所有 <> 标签
        String coloredTitle = org.bukkit.ChatColor.translateAlternateColorCodes('&', finalTitle);
        
        Inventory inv = Bukkit.createInventory(new HomeInventoryHolder(player.getUniqueId(), page), size, coloredTitle);
        
        // 填充家的物品（第一列空，每行8个）
        int startIndex = page * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, homeList.size());
        
        for (int row = 0; row < 3; row++) {
            // 第一个格子留空（不设置任何物品）
            
            // 填充家 (8个每行)
            for (int col = 1; col <= 8; col++) {
                int listIndex = startIndex + (row * 8 + (col - 1));
                if (listIndex < endIndex) {
                    var entry = homeList.get(listIndex);
                    inv.setItem(row * 9 + col, createHomeItem(config, entry.getKey(), entry.getValue()));
                }
            }
        }
        
        // 只有在有多页的情况下才显示分页按钮
        if (totalPages > 1) {
            // 位置 33 (索引33) - 上一页
            if (page > 0) {
                inv.setItem(33, createPreviousPageButton(config));
            }
            
            // 位置 34 (索引34) - 当前页码
            inv.setItem(34, createCurrentPageItem(config, page + 1, totalPages));
            
            // 位置 35 (索引35) - 下一页
            if (page < totalPages - 1) {
                inv.setItem(35, createNextPageButton(config));
            }
        }
        
        player.openInventory(inv);
    }
    
    private ItemStack createEmptySlot(dev.dejvokep.boostedyaml.YamlDocument config) {
        String materialName = config.getString("home-system.gui.empty-slot.material", "GRAY_STAINED_GLASS_PANE");
        String displayName = config.getString("home-system.gui.empty-slot.display-name", " ");
        int customModelData = config.getInt("home-system.gui.empty-slot.custom-model-data", 0);
        
        Material material = Material.getMaterial(materialName);
        if (material == null) material = Material.AIR;
        
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(org.bukkit.ChatColor.translateAlternateColorCodes('&', displayName));
        if (customModelData > 0) {
            meta.setCustomModelData(customModelData);
        }
        item.setItemMeta(meta);
        return item;
    }
    
    private ItemStack createHomeItem(dev.dejvokep.boostedyaml.YamlDocument config, String homeName, TeleportLocation location) {
        String materialName = config.getString("home-system.gui.home-item.material", "RED_BED");
        String displayNameTemplate = config.getString("home-system.gui.home-item.display-name", "<gold><name></gold>");
        List<String> loreTemplate = config.getStringList("home-system.gui.home-item.lore");
        int customModelData = config.getInt("home-system.gui.home-item.custom-model-data", 0);
        
        Material material = Material.getMaterial(materialName);
        if (material == null) material = Material.RED_BED;
        
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        
        if (customModelData > 0) {
            meta.setCustomModelData(customModelData);
        }
        
        // 替换占位符
        String displayName = displayNameTemplate
                .replace("<name>", homeName)
                .replace("<world>", location.getWorld())
                .replace("<x>", String.format("%.1f", location.getX()))
                .replace("<y>", String.format("%.1f", location.getY()))
                .replace("<z>", String.format("%.1f", location.getZ()));
        
        // 只转换 & 颜色代码，保留所有 <> 标签
        meta.setDisplayName(org.bukkit.ChatColor.translateAlternateColorCodes('&', displayName));
        
        // 替换占位符并转换颜色代码
        List<String> lore = new ArrayList<>();
        for (String line : loreTemplate) {
            String processedLine = line
                    .replace("<name>", homeName)
                    .replace("<world>", location.getWorld())
                    .replace("<x>", String.format("%.1f", location.getX()))
                    .replace("<y>", String.format("%.1f", location.getY()))
                    .replace("<z>", String.format("%.1f", location.getZ()));
            // 只转换 & 颜色代码，保留所有 <> 标签
            lore.add(org.bukkit.ChatColor.translateAlternateColorCodes('&', processedLine));
        }
        
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
    
    private ItemStack createPreviousPageButton(dev.dejvokep.boostedyaml.YamlDocument config) {
        String materialName = config.getString("home-system.gui.previous-page.material", "ARROW");
        String displayName = config.getString("home-system.gui.previous-page.display-name", "&e上一頁");
        int customModelData = config.getInt("home-system.gui.previous-page.custom-model-data", 0);
        
        Material material = Material.getMaterial(materialName);
        if (material == null) material = Material.ARROW;
        
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        // 只转换 & 颜色代码
        meta.setDisplayName(org.bukkit.ChatColor.translateAlternateColorCodes('&', displayName));
        if (customModelData > 0) {
            meta.setCustomModelData(customModelData);
        }
        item.setItemMeta(meta);
        return item;
    }
    
    private ItemStack createNextPageButton(dev.dejvokep.boostedyaml.YamlDocument config) {
        String materialName = config.getString("home-system.gui.next-page.material", "ARROW");
        String displayName = config.getString("home-system.gui.next-page.display-name", "&e下一頁");
        int customModelData = config.getInt("home-system.gui.next-page.custom-model-data", 0);
        
        Material material = Material.getMaterial(materialName);
        if (material == null) material = Material.ARROW;
        
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        // 只转换 & 颜色代码
        meta.setDisplayName(org.bukkit.ChatColor.translateAlternateColorCodes('&', displayName));
        if (customModelData > 0) {
            meta.setCustomModelData(customModelData);
        }
        item.setItemMeta(meta);
        return item;
    }
    
    private ItemStack createCurrentPageItem(dev.dejvokep.boostedyaml.YamlDocument config, int currentPage, int totalPages) {
        String materialName = config.getString("home-system.gui.current-page.material", "PAPER");
        String displayNameTemplate = config.getString("home-system.gui.current-page.display-name", "&b第 <page> 頁 / 共 <total> 頁");
        int customModelData = config.getInt("home-system.gui.current-page.custom-model-data", 0);
        
        Material material = Material.getMaterial(materialName);
        if (material == null) material = Material.PAPER;
        
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        
        String displayName = displayNameTemplate
                .replace("<page>", String.valueOf(currentPage))
                .replace("<total>", String.valueOf(totalPages));
        
        // 保留 craft-engine 标签
        String processedName = displayName.replaceAll("<(?!/?shift:|/?image:)[^>]+>", "");
        meta.setDisplayName(org.bukkit.ChatColor.translateAlternateColorCodes('&', processedName));
        if (customModelData > 0) {
            meta.setCustomModelData(customModelData);
        }
        item.setItemMeta(meta);
        return item;
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof HomeInventoryHolder holder)) {
            return;
        }
        
        event.setCancelled(true);
        
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        
        int slot = event.getSlot();
        int row = slot / 9;
        int col = slot % 9;
        
        // 点击分页按钮
        if (slot == 33) {
            openHomeGUI(player, holder.getPage() - 1);
            return;
        } else if (slot == 34) {
            return;
        } else if (slot == 35) {
            openHomeGUI(player, holder.getPage() + 1);
            return;
        }
        
        // 第一列是空的
        if (col == 0 || row >= 3) {
            return;
        }
        
        // 计算点击的是第几个家（8个每行）
        int index = holder.getPage() * ITEMS_PER_PAGE + (row * 8 + (col - 1));
        
        Map<String, TeleportLocation> homes = plugin.getHomeManager().getHomes(player.getUniqueId());
        List<Map.Entry<String, TeleportLocation>> homeList = new ArrayList<>(homes.entrySet());
        
        if (index >= homeList.size()) {
            return;
        }
        
        var entry = homeList.get(index);
        String homeName = entry.getKey();
        TeleportLocation location = entry.getValue();
        
        // 点击传送
        player.closeInventory();
        if (TeleportUtils.teleport(player, location)) {
            var sender = plugin.getSenderFactory().wrap(player);
            sender.sendMessage(TranslationManager.render(
                    MessageConstants.COMMANDS_TELEPORT_HOME_SUCCESS.arguments(Component.text(homeName)).build()));
        } else {
            var sender = plugin.getSenderFactory().wrap(player);
            sender.sendMessage(TranslationManager.render(
                    MessageConstants.COMMANDS_TELEPORT_HOME_FAILED.build()));
        }
    }
}

