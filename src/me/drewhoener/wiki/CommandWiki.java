package me.drewhoener.wiki;

import me.drewhoener.wiki.pages.Category;
import me.drewhoener.wiki.pages.Entry;
import me.drewhoener.wiki.pages.PluginWiki;
import me.drewhoener.wiki.pages.SubCategory;
import me.drewhoener.wiki.util.Util;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

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
		Entry entry;

		switch(length){
			case 0:
				player.sendMessage(Util.getHeader("Wikis"));
				player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "" + "Click the entries to navigate!");

				for(BaseComponent[] components : this.simpleWiki.dataHolder.formatWikiEntries(player))
					player.spigot().sendMessage(components);
				player.sendMessage(Util.getEnd());
				break;
			case 1:
				wiki = this.simpleWiki.dataHolder.getWikiByName(args[0]);
				if(wiki == null){
					player.sendMessage(RED + "Couldn't find the wiki requested!");
					return;
				}

				player.sendMessage(Util.getHeader(WordUtils.capitalizeFully(wiki.getName().replaceAll("_", " "))));
				if(wiki.getSubHeader() != null)
					player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "" + wiki.getSubHeader());
				for(BaseComponent[] components : this.simpleWiki.dataHolder.formatCategoryEntries(wiki, player))
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

				player.sendMessage(Util.getHeader(WordUtils.capitalizeFully(category.getName().replaceAll("_", " "))));
				if(category.getSubHeader() != null)
					player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "" + category.getSubHeader());
				for(BaseComponent[] components : this.simpleWiki.dataHolder.formatSubCategoryEntries(category, player))
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
				if(subCategory == null){
					player.sendMessage(RED + "Couldn't find the sub-ctategory requested!");
					return;
				}
				player.sendMessage(Util.getHeader(WordUtils.capitalizeFully(subCategory.getName().replaceAll("_", " "))));
				if(subCategory.getSubHeader() != null)
					player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "" + subCategory.getSubHeader());
				for(BaseComponent[] components : this.simpleWiki.dataHolder.formatEntries(subCategory, player))
					player.spigot().sendMessage(components);
				player.sendMessage(Util.getEnd());
				break;
			case 4:
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
				if(subCategory == null){
					player.sendMessage(RED + "Couldn't find the sub-ctategory requested!");
					return;
				}
				entry = this.simpleWiki.dataHolder.getEntry(subCategory, args[3]);
				if(entry == null){
					player.sendMessage(RED + "Couldn't find the entry requested!");
					return;
				}
				if(!entry.getRecipeList().isEmpty()) {
					Inventory inv = this.simpleWiki.getServer().createInventory(player, InventoryType.WORKBENCH);
					inv.setStorageContents(entry.getRecipeList().toArray(new ItemStack[10]));
					this.simpleWiki.dataHolder.noInteract.add(player.getUniqueId());
					player.openInventory(inv);
					player.updateInventory();
				}
				player.sendMessage(Util.getHeader(WordUtils.capitalizeFully(entry.getName().replaceAll("_", " "))));
				for(String line : entry.getDescriptionList()) {
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
				}
				player.sendMessage(Util.getEnd());
				break;
			default:
				break;
		}

	}
}
