package me.gabriel.lobbylogin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class Events implements Listener {

	private static final Map<UUID, Boolean> logado = new HashMap<>();

	public static Map<UUID, Boolean> getLogadoMap() {
		return logado;
	}

	private final Main plugin;

	public Events(Main plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	private void onJoin(PlayerJoinEvent e) {

		Player player = e.getPlayer();
		UUID playerUUID = player.getUniqueId();
		logado.put(playerUUID, false);

		Location lobbyloc = (Location) plugin.getConfig().getLocation("lobby.location");
		if (lobbyloc != null) {
			player.teleport(lobbyloc);
		}

		File loginFile = new File(plugin.getDataFolder(), "logins.yml");
		FileConfiguration loginConfig = YamlConfiguration.loadConfiguration(loginFile);

		String playerKey = "login." + player.getUniqueId();

		if (!logado.get(playerUUID)) {

			if (!loginConfig.contains(playerKey)) {
				player.sendTitle(ChatColor.translateAlternateColorCodes('&', "&e&lBem vindo!"),
						"Use /register (senha) (senha).", 10, 1980, 10);
			} else {
				player.sendTitle(ChatColor.translateAlternateColorCodes('&', "&e&lBem vindo!"), "Use /login (senha).",
						10, 1980, 10);
			}
		}

		new BukkitRunnable() {
			int tempo = 60;

			@Override
			public void run() {
				if (!player.isOnline()) {
					this.cancel();
					return;
				}

				if (logado.getOrDefault(playerUUID, false)) {
					this.cancel();
					return;
				}

				player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
						new TextComponent(ChatColor.translateAlternateColorCodes('&', "&c" + tempo + "s")));

				if (tempo <= 0) {
					player.kickPlayer("§cVocê demorou demais para logar.");
					this.cancel();
					return;
				}

				tempo--;
			}
		}.runTaskTimer(plugin, 0L, 20L);
	}

	@EventHandler
	private void onMove(PlayerMoveEvent e) {

		Player player = e.getPlayer();
		UUID playerUUID = player.getUniqueId();

		if (!logado.get(playerUUID)) {
			e.setCancelled(true);
		}

	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		Player player = e.getPlayer();
		UUID playerUUID = player.getUniqueId();

		if (!logado.get(playerUUID)) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		Player player = e.getPlayer();
		UUID playerUUID = player.getUniqueId();

		if (!logado.get(playerUUID)) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		if (!Events.getLogadoMap().getOrDefault(e.getPlayer().getUniqueId(), false)) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onEntityDamage(EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof Player) {
			Player player = (Player) e.getDamager();
			if (!Events.getLogadoMap().getOrDefault(player.getUniqueId(), false)) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent e) {
		Player player = e.getPlayer();
		UUID playerUUID = player.getUniqueId();

		if (!logado.get(playerUUID)) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerPickupItem(EntityPickupItemEvent e) {
		if (e.getEntity() instanceof Player) {
			Player player = (Player) e.getEntity();
			if (!Events.getLogadoMap().getOrDefault(player.getUniqueId(), false)) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent e) {
		if (!Events.getLogadoMap().getOrDefault(e.getPlayer().getUniqueId(), false)) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		if (e.getWhoClicked() instanceof Player) {
			Player player = (Player) e.getWhoClicked();
			if (!Events.getLogadoMap().getOrDefault(player.getUniqueId(), false)) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent e) {
		Player player = e.getPlayer();
		UUID playerUUID = player.getUniqueId();

		if (!logado.getOrDefault(playerUUID, false)) {
			String msg = e.getMessage().toLowerCase();

			if (!msg.startsWith("/login") && !msg.startsWith("/register")) {
				e.setCancelled(true);
			}
		}
	}

}
