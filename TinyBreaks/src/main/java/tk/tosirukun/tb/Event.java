package tk.tosirukun.tb;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import tk.tosirukun.tb.utils.Stat;

public class Event implements Listener {
	private Tinybreaks plugin;

	public Event(Tinybreaks tinybreaks) {
		this.plugin = tinybreaks;
	}

	protected ItemStack generateItem(final Material material, final String name, final String... lore) {
		final ItemStack item = new ItemStack(material, 1);
		final ItemMeta meta = item.getItemMeta();

		meta.setDisplayName(name);
		meta.setLore(Arrays.asList(lore));
		item.setItemMeta(meta);
		return item;
	}

	public void openShopInv(Player player) {
		Inventory inv = plugin.getServer().createInventory(null,18, ChatColor.BOLD + "" + ChatColor.GRAY + "整地SHOP");
		inv.setItem(0, generateItem(Material.IRON_PICKAXE, "ふつうの鉄つるはし", "200ポイント"));
		inv.setItem(1, generateItem(Material.DIAMOND_PICKAXE, "ふつうのダイヤつるはし", "1000ポイント"));
		inv.setItem(2, generateItem(Material.DIAMOND_PICKAXE, "やばいつるはし", "採掘速度Lv20", "5000ポイント"));
		player.openInventory(inv);
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

	@EventHandler
	public void onPlayerLeftEvent(PlayerQuitEvent e) {
		if (plugin.DevelopMode) plugin.getLogger().info("プレイヤーの情報を取得します...");
		Stat status = plugin.statusManager.getStat(e.getPlayer());
		if (plugin.DevelopMode) plugin.getLogger().info("成功しました");
		if(plugin.DevelopMode) plugin.getLogger().info("プレイヤーのアカウントに保存します...");
		plugin.database.UpdatePlayerAccount(e.getPlayer(), status);
		if (plugin.DevelopMode) plugin.getLogger().info("成功しました");
		if (plugin.DevelopMode) plugin.getLogger().info("ステータスマネージャーからプレイヤーの情報を削除します...");
		plugin.statusManager.removeStat(e.getPlayer());
		if (plugin.DevelopMode) plugin.getLogger().info("成功しました");
	}

	@EventHandler
	public void onInventoryClickEvent(InventoryClickEvent e) {
		if (!e.getInventory().getName().equalsIgnoreCase("整地SHOP")) {
			return;
		}
		e.setCancelled(true);

		int ClickedSlot = e.getRawSlot();
		Stat status = plugin.statusManager.getStat((Player)e.getWhoClicked());
		Player player = (Player)e.getWhoClicked();

		if (ClickedSlot == 0) {
			if (status.getPoint() >= 200) {
				status.setPoint(status.getPoint() - 200);
				plugin.statusManager.updateStat(player, status);
				ItemStack result = generateItem(Material.IRON_PICKAXE, ChatColor.WHITE + "ふつうの鉄つるはし");
				player.getInventory().addItem(result);
			} else {
				player.sendMessage(ChatColor.RED + "ポイントが足りません");
			}
		}

		if (ClickedSlot == 0) {
			if (status.getPoint() >= 1000) {
				status.setPoint(status.getPoint() - 1000);
				plugin.statusManager.updateStat(player, status);
				ItemStack result = generateItem(Material.DIAMOND_PICKAXE, ChatColor.WHITE + "ふつうのダイヤつるはし");
				player.getInventory().addItem(result);
			} else {
				player.sendMessage(ChatColor.RED + "ポイントが足りません");
			}
		}

		if (ClickedSlot == 0) {
			if (status.getPoint() >= 5000) {
				status.setPoint(status.getPoint() - 5000);
				plugin.statusManager.updateStat(player, status);
				ItemStack result = generateItem(Material.DIAMOND_PICKAXE, ChatColor.WHITE + "やばいつるはし");
				ItemMeta meta = result.getItemMeta();
				meta.addEnchant(Enchantment.DIG_SPEED, 20, false);
				result.setItemMeta(meta);
				player.getInventory().addItem(result);
			} else {
				player.sendMessage(ChatColor.RED + "ポイントが足りません");
			}
		}
	}

}
