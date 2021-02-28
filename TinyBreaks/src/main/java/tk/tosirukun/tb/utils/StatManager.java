package tk.tosirukun.tb.utils;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

public class StatManager {
	private Map<Player, Stat> PlayerStats = new HashMap<>();

	public void setStat(Player player, Stat stat) {
		PlayerStats.put(player, stat);
	}

	public Stat getStat(Player player) {
		return PlayerStats.get(player);
	}

	public void removeStat(Player player) {
		PlayerStats.remove(player);
	}

	public boolean containStat(Player player) {
		return PlayerStats.containsKey(player);
	}

	public Map<Player, Stat> getOnlineStat() {
		return PlayerStats;
	}

	public void updateStat(Player player, Stat status) {
		PlayerStats.replace(player, status);
	}

}
