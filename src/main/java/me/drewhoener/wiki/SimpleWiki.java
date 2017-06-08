package me.drewhoener.wiki;

import me.drewhoener.wiki.data.DataHolder;
import me.drewhoener.wiki.pages.PluginWiki;
import me.drewhoener.wiki.util.Util;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.io.FilenameUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.regex.Pattern;

public class SimpleWiki extends JavaPlugin implements Listener {

	public static final File dataFolder = new File("plugins", "SimpleWiki");
	DataHolder dataHolder;

	public void onEnable() {

		if (!this.getDataFolder().exists()) {

			if (this.getDataFolder().mkdirs()) {
				this.saveDefaultConfig();
				this.getLogger().info("Successfully created the Data Folder.");
			} else {
				this.getLogger().severe("Failed to create the data folder (" + this.getDataFolder().getAbsolutePath() + ")");
			}

		}

		if (!(new File(this.getDataFolder(), "config.yml").exists())) {
			this.saveDefaultConfig();
		}

		this.setConfig();

		this.dataHolder = new DataHolder();
		this.indexFiles();

		this.getCommand("wiki").setExecutor(new CommandWiki(this));
		this.getServer().getPluginManager().registerEvents(this, this);

	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		switch (command.getName()) {

			case "testing":

				if (sender.isOp() && sender instanceof Player)
					if (!this.dataHolder.getWikiList().isEmpty())
						for (PluginWiki category : this.dataHolder.getWikiList())
							Bukkit.broadcastMessage(category.toString());
				break;
			case "reindex":
				if (!sender.hasPermission(Util.Config.DEFAULT_PERMISSION))
					return true;
				sender.sendMessage(ChatColor.GREEN + "Reloading wiki files");
				this.getLogger().info("Reloaded Wiki");
				this.dataHolder.clearWikiList();
				this.indexFiles();
				break;

		}

		return true;
	}

	private void indexFiles() {
		if (Util.WIKI_DIR.exists()) {
			File[] files = Util.WIKI_DIR.listFiles();
			if (files != null)
				for (File file : files)
					try {
						if (Util.Config.DEBUG)
							this.getLogger().info("Debug: File name is " + file.getName());
						if (FilenameUtils.getExtension(file.getName()).endsWith("yml")) {
							YamlConfiguration wiki = new YamlConfiguration();
							wiki.load(file);
							this.dataHolder.addWiki(new PluginWiki(file.getName().replaceAll(Pattern.quote(".yml"), ""), wiki));
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
		} else {
			this.getLogger().severe("No Category File Detected!");
		}
	}

	private void setConfig() {
		Util.Config.DEBUG = this.getConfig().getBoolean("debug", false);
		Util.Config.DEFAULT_PERMISSION = this.getConfig().getString("permissionNode", "");
	}

	@EventHandler
	public void onInteract(InventoryClickEvent event) {
		if (this.dataHolder.noInteract.contains(event.getWhoClicked().getUniqueId()))
			event.setCancelled(true);
	}

	@EventHandler
	public void onCloseInventory(InventoryCloseEvent event) {
		this.dataHolder.noInteract.remove(event.getPlayer().getUniqueId());
	}
}
