package me.hiencao.playercountsync.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.hiencao.playercountsync.PlayerCountSync;
import me.hiencao.playercountsync.config.ConfigManager;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Properties;

public class MySQL {
    private final ConfigManager config = PlayerCountSync.getConfigManager();
    private final PlayerCountSync playerCountSync;
    private DataSource dataSource;

    private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS player_count (server_unique_id VARCHAR(36) NOT NULL, player_count INT NOT NULL, PRIMARY KEY (server_unique_id))";
    private static final String INSERT_PLAYER_COUNT = "INSERT INTO player_count (server_unique_id, player_count) VALUES (?, ?)";
    private static final String SELECT_PLAYER_COUNT = "SELECT * FROM player_count WHERE server_unique_id = ?";
    private static final String SELECT_ALL_PLAYER_COUNT = "SELECT * FROM player_count";
    private static final String UPDATE_PLAYER_COUNT = "UPDATE player_count SET player_count = ? WHERE server_unique_id = ?";
    private static final String EXISTS_SERVER_UNIQUE_ID = "SELECT EXISTS(SELECT * FROM player_count WHERE server_unique_id = ?)";


    public MySQL(PlayerCountSync playerCountSync) {
        this.playerCountSync = playerCountSync;
    }

    public void init() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl("jdbc:mysql://{host}:{port}/{table}?autoReconnect=true"
                .replace("{host}", config.getDatabaseHost())
                .replace("{port}", config.getDatabasePort())
                .replace("{table}", config.getDatabaseName()));
        hikariConfig.setUsername(config.getDatabaseUser());
        hikariConfig.setPassword(config.getDatabasePassword());
        hikariConfig.setMaximumPoolSize(12);
        hikariConfig.setMinimumIdle(12);
        hikariConfig.setMaxLifetime(1800000);
        hikariConfig.setKeepaliveTime(30000);
        hikariConfig.setDataSourceProperties(new Properties() {{
            put("cachePrepStmts", "true");
            put("prepStmtCacheSize", "250");
            put("prepStmtCacheSqlLimit", "2048");
            put("useServerPrepStmts", "true");
            put("useLocalSessionState", "true");
            put("useLocalTransactionState", "true");
            put("rewriteBatchedStatements", "true");
            put("cacheResultSetMetadata", "true");
            put("cacheServerConfiguration", "true");
            put("elideSetAutoCommits", "true");
            put("maintainTimeStats", "false");
        }});
        dataSource = new HikariDataSource(hikariConfig);
        playerCountSync.getLogger().info("MariaDB connection established");
        checkTable();
    }
    private void checkTable() {
        try (Connection sql = dataSource.getConnection()) {
            DatabaseMetaData dbm = sql.getMetaData();
            ResultSet tables = dbm.getTables(null, null, "player_count", new String[]{"TABLE"});
            if (!tables.next()) {
                createTable();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            playerCountSync.getLogger().severe("SQLException on checkTable");
        }
    }

    public void createTable() {
        try (Connection sql = dataSource.getConnection()) {
            PreparedStatement statement = sql.prepareStatement(CREATE_TABLE);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            playerCountSync.getLogger().severe("SQLException on createTable");
        }
    }

    public void addPlayerCount(String serverUniqueId, int playerCount) {
        PreparedStatement statement;
        try (Connection sql = dataSource.getConnection()) {
            statement = sql.prepareStatement(INSERT_PLAYER_COUNT);
            statement.setString(1, serverUniqueId);
            statement.setInt(2, playerCount);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            playerCountSync.getLogger().severe("SQLException on addPlayerCount");
        }
    }

    public int getPlayerCount(String serverUniqueId) {
        int playerCount = 0;
        try (Connection sql = dataSource.getConnection()) {
            PreparedStatement statement = sql.prepareStatement(SELECT_PLAYER_COUNT);
            statement.setString(1, serverUniqueId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                playerCount = resultSet.getInt("player_count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            playerCountSync.getLogger().severe("SQLException on getPlayerCount");
        }
        return playerCount;
    }

    public int getAllPlayerCount() {
        int playerCount = 0;
        try (Connection sql = dataSource.getConnection()) {
            PreparedStatement statement = sql.prepareStatement(SELECT_ALL_PLAYER_COUNT);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                playerCount += resultSet.getInt("player_count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            playerCountSync.getLogger().severe("SQLException on getAllPlayerCount");
        }
        return playerCount;
    }

    public void updatePlayerCount(String serverUniqueId, int playerCount) {
        PreparedStatement statement;
        try (Connection sql = dataSource.getConnection()) {
            statement = sql.prepareStatement(UPDATE_PLAYER_COUNT);
            statement.setInt(1, playerCount);
            statement.setString(2, serverUniqueId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            playerCountSync.getLogger().severe("SQLException on updatePlayerCount");
        }
    }

    public boolean existsServerUniqueId(String serverUniqueId) {
        boolean exists = false;
        try (Connection sql = dataSource.getConnection()) {
            PreparedStatement statement = sql.prepareStatement(EXISTS_SERVER_UNIQUE_ID);
            statement.setString(1, serverUniqueId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                exists = resultSet.getBoolean(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            playerCountSync.getLogger().severe("SQLException on existsServerUniqueId");
        }
        return exists;
    }
}
