package me.gabriel.lobbylogin;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor {

	private final Main plugin;
	Map<UUID, Boolean> logado = Events.getLogadoMap();

	public Commands(Main plugin) {
		this.plugin = plugin;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		Player player = (Player) sender;

		File loginFile = new File(plugin.getDataFolder(), "logins.yml");
		FileConfiguration loginConfig = YamlConfiguration.loadConfiguration(loginFile);

		if (!(sender instanceof Player)) {
			sender.sendMessage("Apenas jogadores podem executar este comando.");
			return true;
		}

		if (command.getName().equalsIgnoreCase("setlobby")) {

			Location loc = player.getLocation();

			plugin.getConfig().set("lobby.location", loc);

			plugin.saveConfig();

			player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eLobby definida com sucesso!"));
		}

		if (command.getName().equalsIgnoreCase("lobby")) {

			Location lobbyloc = (Location) plugin.getConfig().getLocation("lobby.location");

			if (lobbyloc != null) {
				player.teleport(lobbyloc);
			} else {
				player.sendMessage("Lobby não definido na config.");
			}

			player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eVocê foi para o lobby!"));

		}

		if (command.getName().equalsIgnoreCase("register")) {
			if (args.length >= 2) {
				String senha = args[0];
				String confirmar = args[1];
				String playerKey = "login." + player.getUniqueId();

				if (loginConfig.contains(playerKey)) {
					if (logado.getOrDefault(player.getUniqueId(), false)) {
						player.sendMessage("§eVocê já está logado!");
						return true;
					}
					player.sendMessage("§cUse /login (senha).");
					return true;
				}

				if (senha.equals(confirmar)) {
					player.sendTitle("§e§lBem vindo!", "§aRegistrado com sucesso!", 10, 20, 10);
					player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
					player.sendMessage("§aRegistrado com sucesso!");

					String senhaHash = Integer.toString(senha.hashCode());

					loginConfig.set("login." + player.getUniqueId(), senhaHash);
					logado.put(player.getUniqueId(), true);

					try {
						loginConfig.save(loginFile);
					} catch (IOException e) {
						e.printStackTrace();
						player.sendMessage("§cErro ao salvar registro.");
					}

				} else {
					player.sendMessage("§cAs senhas não coincidem!");
				}

			} else {
				player.sendMessage("§cUse /register (senha) (senha).");
			}

			return true;
		}

		if (command.getName().equalsIgnoreCase("login")) {
			if (args.length >= 1) {
				String senha = args[0];
				String playerKey = "login." + player.getUniqueId();

				if (!loginConfig.contains(playerKey)) {
					player.sendMessage("§cUse /register (senha) (senha).");
					return true;
				}

				if (logado.getOrDefault(player.getUniqueId(), false)) {
					player.sendMessage("§eVocê já está logado!");
					return true;
				}

				String senhaHash = Integer.toString(senha.hashCode());
				String senhaSalva = loginConfig.getString(playerKey);

				if (senhaHash.equals(senhaSalva)) {
					logado.put(player.getUniqueId(), true);

					player.sendTitle("§e§lBem vindo!", "§aLogado com sucesso!", 10, 40, 10);
					player.sendMessage("§aLogin realizado com sucesso!");

					player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
				} else {
					player.sendMessage("§cSenha incorreta!");
					player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 1.0f);
				}
			} else {
				player.sendMessage("§cUse /login (senha).");
			}

			return true;
		}

		if (command.getName().equalsIgnoreCase("changepass")) {
			if (args.length >= 2) {
				String senhaAntiga = args[0];
				String senhaNova = args[1];

				String playerKey = "login." + player.getUniqueId();

				if (!loginConfig.contains(playerKey)) {
					player.sendMessage("§cVocê ainda não está registrado!");
					return true;
				}

				if (senhaAntiga == senhaNova) {
					return true;
				}

				String senhaSalva = loginConfig.getString(playerKey);
				String senhaAntigaHash = Integer.toString(senhaAntiga.hashCode());

				if (!senhaSalva.equals(senhaAntigaHash)) {
					player.sendMessage("§cSenha antiga incorreta!");
					return true;
				}

				String novaHash = Integer.toString(senhaNova.hashCode());
				loginConfig.set(playerKey, novaHash);

				try {
					loginConfig.save(loginFile);
					player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
					player.sendMessage("§aSenha alterada com sucesso!");
				} catch (IOException e) {
					e.printStackTrace();
					player.sendMessage("§cErro ao salvar nova senha.");
				}

			} else {
				player.sendMessage("§cUse /changepass (senha antiga) (senha nova).");
			}

			return true;
		}

		return true;
	}

}
