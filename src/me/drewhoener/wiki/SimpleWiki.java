package me.drewhoener.wiki;

import me.drewhoener.wiki.data.DataHolder;
import me.drewhoener.wiki.pages.PluginWiki;
import me.drewhoener.wiki.util.Util;
import org.apache.commons.io.FilenameUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.regex.Pattern;

public class SimpleWiki extends JavaPlugin {

	public DataHolder dataHolder;
	public static final File dataFolder = new File("plugins", "SimpleWiki");

	public void onEnable(){

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

		this.dataHolder = new DataHolder(this);

		if(Util.WIKI_DIR.exists()){
			File[] files = Util.WIKI_DIR.listFiles();
			if(files != null)
				for(File file : files)
					try {
						this.getLogger().info("Debug: File name is " + file.getName());
						if (FilenameUtils.getExtension(file.getName()).endsWith("yml")) {
							YamlConfiguration wiki = new YamlConfiguration();
							wiki.load(file);
							this.dataHolder.addWiki(new PluginWiki(file.getName().replaceAll(Pattern.quote(".yml"), ""), wiki));
						}
					}catch(Exception e){
						e.printStackTrace();
					}
		}else{
			this.getLogger().severe("No Category File Detected!");
		}

		this.getCommand("swiki").setExecutor(new CommandWiki(this));

	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		switch(command.getName()){

			case "testing":

				if(sender.isOp())
					if(!this.dataHolder.getWikiList().isEmpty())
						for(PluginWiki category : this.dataHolder.getWikiList())
							Bukkit.broadcastMessage(category.toString());
				break;

		}

		return true;
	}
}
