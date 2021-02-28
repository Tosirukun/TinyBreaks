package tk.tosirukun.tb;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import tk.tosirukun.tb.db.Database;
import tk.tosirukun.tb.utils.StatManager;

public class Tinybreaks extends JavaPlugin {

	StatManager statusManager;
	Database database;
	boolean DevelopMode = true;

	@Override
	public void onEnable() {
		saveDefaultConfig();
		if (DevelopMode) getLogger().info("TinyBreaksを有効化します...");
		if (DevelopMode) getLogger().info("Databaseに接続します");
		database = new Database();
		database.connect(getConfig());
		if (DevelopMode) getLogger().info("成功しました");
		if (DevelopMode) getLogger().info("ステータスマネージャーを登録します");
		statusManager = new StatManager();
		if (DevelopMode) getLogger().info("成功しました");
		if (DevelopMode) getLogger().info("イベントリスナーを登録します");
		getServer().getPluginManager().registerEvents(new Event(this), this);
		if (DevelopMode) getLogger().info("成功しました");

		Tinybreaks plugin = this;

		getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			@Override
			public void run() {
				for (Player player: plugin.getServer().getOnlinePlayers()) {
					ScoreboardManager manager = plugin.getServer().getScoreboardManager();
					Scoreboard board = player.getScoreboard();
					Objective obj = board.getObjective(DisplaySlot.SIDEBAR);
					if (obj == null) {
						obj = manager.getNewScoreboard().registerNewObjective("MainScoreboard", "dummy");
					}
					obj.setDisplaySlot(DisplaySlot.SIDEBAR);
					obj.setDisplayName("とじるくん整地鯖");
					Score block = obj.getScore("破壊ブロック数");
					block.setScore((int)plugin.statusManager.getStat(player).getBlock());
					player.setScoreboard(board);
				}
			}
		}, 0L, 100L);
	}

}
