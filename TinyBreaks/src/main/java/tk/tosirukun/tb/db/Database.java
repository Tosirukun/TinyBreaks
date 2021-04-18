package tk.tosirukun.tb.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import tk.tosirukun.tb.utils.Stat;

public class Database {
	private HikariDataSource hikari;

	public void connect(FileConfiguration config) {
		HikariConfig hikariConfig = new HikariConfig();
		hikariConfig.setDriverClassName("com.mysql.jdbc.Driver");
		hikariConfig.setJdbcUrl("jdbc:mysql://" + config.getString("Database.Url") + "/" + config.getString("Database.DBName"));
		hikariConfig.addDataSourceProperty("user", config.getString("Database.Username"));
		hikariConfig.addDataSourceProperty("password", config.getString("Database.Password"));
		hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
		hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
		hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
		hikariConfig.addDataSourceProperty("useServerPrepStmts", "true");
		hikariConfig.setConnectionInitSql("SELECT 1");

		this.hikari = new HikariDataSource(hikariConfig);
		createTable();

	}

	public void createTable() {
		try (Connection con = hikari.getConnection();
				Statement statement = con.createStatement()) {
				statement.executeUpdate(
						"CREATE TABLE IF NOT EXISTS `account` (" +
							"`id` INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT," +
							"`uuid` VARCHAR(16) NOT NULL UNIQUE KEY" +
							")"
				);
				statement.executeUpdate(
						"CREATE TABLE IF NOT EXISTS `data` (" +
							"`id` INT UNSIGNED NOT NULL PRIMARY KEY," +
							"`block` BIGINT UNSIGNED NOT NULL DEFAULT 0," +
							"`level` INT UNSIGNED NOT NULL DEFAULT 0," +
							"`moon` INT UNSIGNED NOT NULL DEFAULT 0," +
							"`rank` VARCHAR(255) NOT NULL" +
							")"
				);

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public boolean DoPlayerHaveAccount(Player player) {
		try (Connection con = hikari.getConnection()) {
		try (PreparedStatement statement = con.prepareStatement(
				"SELECT `id` FROM `account` WHERE `uuid`=?"
				)) {
			statement.setString(1, player.getUniqueId().toString());
			try (ResultSet result = statement.executeQuery()) {
				if (result.next()) {
					return true;
				} else {
					return false;
				}
			}
		} catch (SQLException e) {
			throw e;
		}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public int CreateSyncAccount(Player player) {
		try (Connection con = hikari.getConnection();
				PreparedStatement statement = con.prepareStatement(
						"INSERT INTO `account`(`uuid`) VALUES(?)"
						)) {
			statement.setString(1, player.getUniqueId().toString());
			statement.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		try (Connection con = hikari.getConnection()) {
			try (PreparedStatement statement = con.prepareStatement(
					"SELECT `id` FROM `account` WHERE `uuid`=?"
					)) {
				statement.setString(1, player.getUniqueId().toString());
				try (ResultSet result = statement.executeQuery()) {
					if (result.next()) {
						return result.getInt(1);
					}
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		throw new RuntimeException("このUUIDはアカウントに登録されていません\n意図的にアカウントを削除した場合は新たに作り直してください");
	}

	public void CreatePlayerAccount(int id, Player player) {
		try (Connection con = hikari.getConnection();
				PreparedStatement statement = con.prepareStatement(
						"INSERT INTO `data` VALUES(?,?,?)"
						)) {
			statement.setInt(1, id);
			statement.setInt(2, 0);
			statement.setInt(3, 0);
			statement.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public Stat getPlayerStatus(Player player) {
		int id = 0;
		Stat status = new Stat();
		try (Connection con = hikari.getConnection()) {
			try (PreparedStatement statement = con.prepareStatement(
					"SELECT `id` FROM `account` WHERE `uuid`=?"
					)) {
				statement.setString(1, player.getUniqueId().toString());
				try (ResultSet result = statement.executeQuery()) {
					if (result.next()) {
						id = result.getInt(1);
					}
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		try (Connection con = hikari.getConnection()) {
			try (PreparedStatement statement = con.prepareStatement(
					"SELECT `block`,`point` FROM `data` WHERE `id`=?"
					)) {
				statement.setInt(1, id);
				try (ResultSet result = statement.executeQuery()) {
					if (result.next()) {
						status.setProperties(result.getInt(1), result.getInt(2));
					}
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return status;
	}

	public void UpdatePlayerAccount(Player player, Stat status) {
		try (Connection con = hikari.getConnection();
				PreparedStatement statement = con.prepareStatement(
						"UPDATE `data` SET `block`=?,`point`=? WHERE `id`=?"
						)) {
			statement.setInt(1, (int)status.getBlock());
			statement.setInt(2, (int)status.getPoint());
			statement.setInt(3, this.getPlayerId(player));
			statement.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public int getPlayerId(Player player) {
		try (Connection con = hikari.getConnection()) {
			try (PreparedStatement statement = con.prepareStatement(
					"SELECT `id` FROM `account` WHERE `uuid`=?"
					)) {
				statement.setString(1, player.getUniqueId().toString());
				try (ResultSet result = statement.executeQuery()) {
					if (result.next()) {
						return result.getInt(1);
					}
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return -1;
	}

}
