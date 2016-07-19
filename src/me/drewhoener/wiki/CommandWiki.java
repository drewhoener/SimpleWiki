package me.drewhoener.wiki;

import me.drewhoener.wiki.pages.Category;
import me.drewhoener.wiki.pages.PluginWiki;
import me.drewhoener.wiki.pages.SubCategory;
import me.drewhoener.wiki.util.Util;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static org.bukkit.ChatColor.RED;

public class CommandWiki implements CommandExecutor {

	private final SimpleWiki simpleWiki;

	public CommandWiki(SimpleWiki simpleWiki) {
		this.simpleWiki = simpleWiki;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {

		if(!(sender instanceof Player)){
			sender.sendMessage(RED + "Only players!");
			return true;
		}

		this.runWikiCommand(((Player) sender), args);


		return true;
	}

	public void runWikiCommand(Player player, String[] args){

		int length = args.length;
		PluginWiki wiki;
		Category category;
		SubCategory subCategory;

		switch(length){
			case 0:
				player.sendMessage(Util.getEnd());
				for(BaseComponent[] components : this.simpleWiki.dataHolder.formatWikiEntries())
					player.spigot().sendMessage(components);
				player.sendMessage(Util.getEnd());
				break;
			case 1:
				wiki = this.simpleWiki.dataHolder.getWikiByName(args[0]);
				if(wiki == null){
					player.sendMessage(RED + "Couldn't find the wiki requested!");
					return;
				}

				player.sendMessage(Util.getEnd());
				for(BaseComponent[] components : this.simpleWiki.dataHolder.formatCategoryEntries(wiki))
					player.spigot().sendMessage(components);
				player.sendMessage(Util.getEnd());
				break;
			case 2:
				wiki = this.simpleWiki.dataHolder.getWikiByName(args[0]);
				if(wiki == null){
					player.sendMessage(RED + "Couldn't find the wiki requested!");
					return;
				}
				category = this.simpleWiki.dataHolder.getCategory(wiki, args[1]);
				if(category == null){
					player.sendMessage(RED + "Couldn't find the category requested!");
					return;
				}

				player.sendMessage(Util.getEnd());
				for(BaseComponent[] components : this.simpleWiki.dataHolder.formatSubCategoryEntries(category))
					player.spigot().sendMessage(components);
				player.sendMessage(Util.getEnd());
				break;
			case 3:
				wiki = this.simpleWiki.dataHolder.getWikiByName(args[0]);
				if(wiki == null){
					player.sendMessage(RED + "Couldn't find the wiki requested!");
					return;
				}
				category = this.simpleWiki.dataHolder.getCategory(wiki, args[1]);
				if(category == null){
					player.sendMessage(RED + "Couldn't find the category requested!");
					return;
				}
				subCategory = this.simpleWiki.dataHolder.getSubCategory(category, args[2]);
				Bukkit.broadcastMessage(args[2]);
				if(subCategory == null){
					player.sendMessage(RED + "Couldn't find the sub-ctategory requested!");
					return;
				}
				player.sendMessage(Util.getEnd());
				for(BaseComponent[] components : this.simpleWiki.dataHolder.formatEntries(subCategory))
					player.spigot().sendMessage(components);
				player.sendMessage(Util.getEnd());
				break;
			default:
				break;
		}

	}
}
