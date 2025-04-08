package me.gabriel.lobbylogin;

import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

	@Override
	public void onEnable() {

		getServer().getPluginManager().registerEvents(new Events(this), this);
		this.getCommand("login").setExecutor(new Commands(this));
		this.getCommand("register").setExecutor(new Commands(this));
		this.getCommand("lobby").setExecutor(new Commands(this));
		this.getCommand("setlobby").setExecutor(new Commands(this));
		this.getCommand("changepass").setExecutor(new Commands(this));

	}

	@Override
	public void onDisable() {

		HandlerList.unregisterAll(new Events(this));

	}

}
