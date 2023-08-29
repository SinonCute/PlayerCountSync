package me.hiencao.playercountsync.listener;

import me.hiencao.playercountsync.PlayerCountSync;
import me.hiencao.playercountsync.config.ConfigManager;
import me.hiencao.playercountsync.database.MySQL;
import org.apache.commons.lang3.time.StopWatch;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {

    private final ConfigManager configManager = PlayerCountSync.getConfigManager();
    private final MySQL mySQL = PlayerCountSync.getMySQL();


    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        int playerCount = e.getPlayer().getServer().getOnlinePlayers().size();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        if (mySQL.existsServerUniqueId(configManager.getServerUniqueId())) {
            mySQL.updatePlayerCount(configManager.getServerUniqueId(), playerCount);
        } else {
            mySQL.addPlayerCount(configManager.getServerUniqueId(), playerCount);
        }
        stopWatch.stop();
        PlayerCountSync.getInstance().getLogger().info("Update player count to database in " + stopWatch.getTime() + "ms");
    }
}
