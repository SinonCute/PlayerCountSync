package me.hiencao.playercountsync;

import me.hiencao.playercountsync.config.ConfigManager;
import me.hiencao.playercountsync.database.MySQL;
import me.hiencao.playercountsync.listener.PlayerListener;
import me.hiencao.playercountsync.placeholder.Placeholder;
import org.bukkit.plugin.java.JavaPlugin;

public final class PlayerCountSync extends JavaPlugin {

    private static PlayerCountSync instance;
    private static ConfigManager configManager;
    private static MySQL mySQL;

    @Override
    public void onEnable() {
        instance = this;

        configManager = new ConfigManager(this);
        configManager.init();

        mySQL = new MySQL(this);
        mySQL.init();

        getServer().getPluginManager().registerEvents(new PlayerListener(), this);

        new Placeholder().register();
        initTaskUpdatePlayerCount();

        getLogger().info("PlayerCountSync enabled");
        getLogger().info("This server unique id is " + configManager.getServerUniqueId());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void initTaskUpdatePlayerCount() {
        //create schedule task every 5 minutes
        getServer().getScheduler().runTaskTimerAsynchronously(this, () -> {
            int playerCount = getServer().getOnlinePlayers().size();
            if (mySQL.existsServerUniqueId(configManager.getServerUniqueId())) {
                mySQL.updatePlayerCount(configManager.getServerUniqueId(), playerCount);
            } else {
                mySQL.addPlayerCount(configManager.getServerUniqueId(), playerCount);
            }
        }, 0L, configManager.getUpdateInterval());
    }

    public static PlayerCountSync getInstance() {
        return instance;
    }

    public static ConfigManager getConfigManager() {
        return configManager;
    }

    public static MySQL getMySQL() {
        return mySQL;
    }
}
