package me.hiencao.playercountsync.config;

import me.hiencao.playercountsync.PlayerCountSync;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {
	private final PlayerCountSync playerCountSync;
	private String serverUniqueId;

	private String databaseHost;
	private String databasePort;
	private String databaseName;
	private String databaseUser;
	private String databasePassword;

	private int updateInterval;

	public ConfigManager(PlayerCountSync playerCountSync) {
		this.playerCountSync = playerCountSync;
	}

	public void init() {
		playerCountSync.saveDefaultConfig();
		FileConfiguration yaml = playerCountSync.getConfig();

		serverUniqueId = yaml.getString("serverUniqueId");

		databaseHost = yaml.getString("database.host");
		databasePort = yaml.getString("database.port");
		databaseName = yaml.getString("database.name");
		databaseUser = yaml.getString("database.user");
		databasePassword = yaml.getString("database.password");

		updateInterval = yaml.getInt("updateInterval");
	}

	public String getServerUniqueId() {
		return serverUniqueId;
	}

	public String getDatabaseHost() {
		return databaseHost;
	}

	public String getDatabasePort() {
		return databasePort;
	}

	public String getDatabaseName() {
		return databaseName;
	}

	public String getDatabaseUser() {
		return databaseUser;
	}

	public String getDatabasePassword() {
		return databasePassword;
	}

	public int getUpdateInterval() { return updateInterval; }
}
