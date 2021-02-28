package tk.tosirukun.tb;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import tk.tosirukun.tb.utils.Stat;

public class Event implements Listener {
	private Tinybreaks plugin;

	public Event(Tinybreaks tinybreaks) {
		this.plugin = tinybreaks;
	}

	@EventHandler
	public void onPlayerJoinEvent(PlayerJoinEvent e) {
		Player player = e.getPlayer();
		if (!plugin.database.DoPlayerHaveAccount(player)) {
			//アカウントを作成
			if (plugin.DevelopMode) plugin.getLogger().info("今回参加したプレイヤーはアカウントを持っていません\n新規作成します...");
			int id = plugin.database.CreateSyncAccount(player);
			plugin.database.CreatePlayerAccount(id, player);
			if (plugin.DevelopMode) plugin.getLogger().info("成功しました");
		}
		if (plugin.DevelopMode) plugin.getLogger().info("プレイヤーの情報を取得します");
		Stat status = plugin.database.getPlayerStatus(player);
		if (plugin.DevelopMode) plugin.getLogger().info("プレイヤーの情報をステータスマネージャーに登録します...");
		plugin.statusManager.setStat(player, status);
		if (plugin.DevelopMode) plugin.getLogger().info("成功しました");


	}

	@EventHandler
	public void onPlayerBreakBlockEvent(BlockBreakEvent e) {
		if (e.getPlayer() != null) {
			Stat status = plugin.statusManager.getStat(e.getPlayer());
			status.setBlock(status.getBlock() + 1);
			status.setPoint(status.getPoint() + 1);
			plugin.statusManager.updateStat(e.getPlayer(), status);
		}
	}

}
