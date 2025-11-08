package net.momirealms.sparrow.bukkit;

import net.momirealms.sparrow.bukkit.command.SparrowBukkitCommandManager;
import net.momirealms.sparrow.bukkit.feature.enchant.SparrowBukkitEnchantManager;
import net.momirealms.sparrow.bukkit.feature.item.SparrowBukkitItemFactory;
import net.momirealms.sparrow.bukkit.feature.proxy.SparrowBukkitBungeeManager;
import net.momirealms.sparrow.bukkit.feature.skull.SparrowBukkitSkullManager;
import net.momirealms.sparrow.bukkit.feature.phantom.PhantomDisableListener;
import net.momirealms.sparrow.bukkit.feature.teleport.*;
import net.momirealms.sparrow.common.command.SparrowCommandManager;
import net.momirealms.sparrow.common.dependency.Dependency;
import net.momirealms.sparrow.common.feature.teleport.HomeManager;
import net.momirealms.sparrow.common.feature.teleport.SpawnManager;
import net.momirealms.sparrow.common.feature.teleport.WarpManager;
import net.momirealms.sparrow.common.plugin.AbstractSparrowPlugin;
import net.momirealms.sparrow.common.sender.SenderFactory;
import org.bstats.bukkit.Metrics;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Set;

public final class SparrowBukkitPlugin extends AbstractSparrowPlugin {

    private static SparrowBukkitPlugin plugin;
    private final SparrowBukkitBootstrap bootstrap;
    private final SparrowBukkitBungeeManager bungeeManager;
    private SparrowBukkitSkullManager skullManager;
    private SparrowBukkitSenderFactory senderFactory;
    private SparrowBukkitCommandManager commandManager;
    private SparrowBukkitEnchantManager enchantManager;
    private SparrowBukkitItemFactory itemFactory;
    private SparrowBukkitHomeManager homeManager;
    private SparrowBukkitWarpManager warpManager;
    private SparrowBukkitSpawnManager spawnManager;
    private PhantomDisableListener phantomListener;
    private TeleportRequestManager teleportRequestManager;
    private TeleportHistoryManager teleportHistoryManager;
    private BackGUIListener backGUIListener;
    private HomeGUIListener homeGUIListener;
    private TeleportHistoryListener teleportHistoryListener;

    public SparrowBukkitPlugin(SparrowBukkitBootstrap bootstrap) {
        plugin = this;
        this.bootstrap = bootstrap;
        this.bungeeManager = new SparrowBukkitBungeeManager(this);
    }

    @Override
    protected Set<Dependency> getGlobalDependencies() {
        Set<Dependency> dependencies = super.getGlobalDependencies();
        dependencies.add(Dependency.BSTATS_BUKKIT);
        dependencies.add(Dependency.CLOUD_BUKKIT);
        dependencies.add(Dependency.CLOUD_PAPER);
        return dependencies;
    }

    @Override
    public void reload() {
        super.reload();
        this.enchantManager.reload();
    }

    @Override
    public void enable() {
        super.enable();
        this.skullManager = new SparrowBukkitSkullManager(this);
        this.enchantManager = new SparrowBukkitEnchantManager(this);
        this.itemFactory = SparrowBukkitItemFactory.create(this);
        
        // Initialize teleport managers
        this.homeManager = new SparrowBukkitHomeManager(this);
        this.warpManager = new SparrowBukkitWarpManager(this);
        this.spawnManager = new SparrowBukkitSpawnManager(this);
        this.teleportRequestManager = new TeleportRequestManager();
        this.teleportHistoryManager = new TeleportHistoryManager(this);
        
        // Load teleport data
        this.homeManager.load();
        this.warpManager.load();
        this.spawnManager.load();
        
        // Register respawn listener
        getLoader().getServer().getPluginManager().registerEvents(new SpawnRespawnListener(this), getLoader());
        
        // Register phantom disable listener
        this.phantomListener = new PhantomDisableListener(this);
        getLoader().getServer().getPluginManager().registerEvents(this.phantomListener, getLoader());
        
        // Register back GUI listener
        this.backGUIListener = new BackGUIListener(this);
        getLoader().getServer().getPluginManager().registerEvents(this.backGUIListener, getLoader());
        
        // Register home GUI listener
        this.homeGUIListener = new HomeGUIListener(this);
        getLoader().getServer().getPluginManager().registerEvents(this.homeGUIListener, getLoader());
        
        // Register teleport history listener
        this.teleportHistoryListener = new TeleportHistoryListener(this);
        getLoader().getServer().getPluginManager().registerEvents(this.teleportHistoryListener, getLoader());
        
        new Metrics(getLoader(), 21789);
    }

    @Override
    public void disable() {
        this.commandManager.unregisterFeatures();
        this.bungeeManager.disable();
        this.enchantManager.disable();
        this.skullManager.disable();
        
        // Unload teleport managers
        if (this.homeManager != null) this.homeManager.unload();
        if (this.warpManager != null) this.warpManager.unload();
        if (this.spawnManager != null) this.spawnManager.unload();
        
        super.disable();
    }

    @Override
    protected void setupSenderFactory() {
        this.senderFactory = new SparrowBukkitSenderFactory(this);
    }

    @Override
    protected void setupCommandManager() {
        this.commandManager = new SparrowBukkitCommandManager(this);
        this.commandManager.registerDefaultFeatures();
    }

    @Override
    public SparrowBukkitBootstrap getBootstrap() {
        return bootstrap;
    }

    public JavaPlugin getLoader() {
        return this.bootstrap.getLoader();
    }

    public static SparrowBukkitPlugin getInstance() {
        return plugin;
    }

    public SparrowBukkitBungeeManager getBungeeManager() {
        return bungeeManager;
    }

    public SenderFactory<SparrowBukkitPlugin, CommandSender> getSenderFactory() {
        return senderFactory;
    }

    public SparrowCommandManager<CommandSender> getCommandManager() {
        return commandManager;
    }

    public SparrowBukkitEnchantManager getEnchantManager() {
        return enchantManager;
    }

    public SparrowBukkitSkullManager getSkullManager() {
        return skullManager;
    }

    public SparrowBukkitItemFactory getItemFactory() {
        return itemFactory;
    }

    public HomeManager getHomeManager() {
        return homeManager;
    }

    public WarpManager getWarpManager() {
        return warpManager;
    }

    public SpawnManager getSpawnManager() {
        return spawnManager;
    }

    public TeleportRequestManager getTeleportRequestManager() {
        return teleportRequestManager;
    }

    public TeleportHistoryManager getTeleportHistoryManager() {
        return teleportHistoryManager;
    }

    public BackGUIListener getBackGUIListener() {
        return backGUIListener;
    }

    public HomeGUIListener getHomeGUIListener() {
        return homeGUIListener;
    }

    public TeleportHistoryListener getTeleportHistoryListener() {
        return teleportHistoryListener;
    }
}
