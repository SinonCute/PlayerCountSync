package me.hiencao.playercountsync.placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.hiencao.playercountsync.PlayerCountSync;
import me.hiencao.playercountsync.database.MySQL;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class Placeholder extends PlaceholderExpansion {

    private final MySQL mySQL = PlayerCountSync.getMySQL();

    @Override
    public @NotNull String getIdentifier() {
        return "PlayerCountSync";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Sinon";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if (params.equalsIgnoreCase("all")) {
            return mySQL.getAllPlayerCount() + "";
        }

        if (params.contains("_")) {
            String[] split = params.split("_");
            String uniqueId = split[1];
            if (mySQL.existsServerUniqueId(uniqueId)) {
                return mySQL.getPlayerCount(uniqueId) + "";
            } else {
                return "N/A";
            }
        }
        return null;
    }
}
