package me.drewhoener.wiki;

import com.google.common.collect.ImmutableList;
import me.drewhoener.wiki.pages.Category;
import me.drewhoener.wiki.pages.Entry;
import me.drewhoener.wiki.pages.PluginWiki;
import me.drewhoener.wiki.pages.SubCategory;
import me.drewhoener.wiki.util.Util;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static org.bukkit.ChatColor.RED;

class CommandWiki implements TabExecutor {

	private final SimpleWiki simpleWiki;

	CommandWiki(SimpleWiki simpleWiki) {
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

	private void runWikiCommand(Player player, String[] args) {

		int length = args.length;
		PluginWiki wiki;
		Category category;
		SubCategory subCategory;
		Entry entry;

		switch(length){
			case 0:
				player.sendMessage(Util.getHeader("Wikis"));
				player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "" + "Click the entries to navigate!");

				for (TextComponent component : this.simpleWiki.dataHolder.formatWikiEntries(player))
					player.spigot().sendMessage(component);
				player.sendMessage(Util.getEnd());
				break;
			case 1:
				wiki = this.simpleWiki.dataHolder.getWikiByName(args[0]);
				if(wiki == null){
					player.sendMessage(RED + "Couldn't find the wiki requested!");
					return;
				}
				if (wiki.getPermissionNode() != null && !player.hasPermission(wiki.getPermissionNode())) {
					player.sendMessage(ChatColor.RED + "You don't have permission to view this Wiki!");
					return;
				}

				player.sendMessage(Util.getHeader(WordUtils.capitalizeFully(wiki.getName().replaceAll("_", " "))));
				if(wiki.getSubHeader() != null)
					player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "" + wiki.getSubHeader());
				for (TextComponent component : this.simpleWiki.dataHolder.formatCategoryEntries(wiki, player))
					player.spigot().sendMessage(component);
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
				if (category.getPermissionNode() != null && !player.hasPermission(category.getPermissionNode())) {
					player.sendMessage(ChatColor.RED + "You don't have permission to view this Category!");
					return;
				}

				player.sendMessage(Util.getHeader(WordUtils.capitalizeFully(category.getName().replaceAll("_", " "))));
				if(category.getSubHeader() != null)
					player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "" + category.getSubHeader());
				for (TextComponent component : this.simpleWiki.dataHolder.formatSubCategoryEntries(category, player))
					player.spigot().sendMessage(component);
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
					player.sendMessage(RED + "Couldn't find the sub-category requested!");
					return;
				}
				if (subCategory.getPermissionNode() != null && !player.hasPermission(subCategory.getPermissionNode())) {
					player.sendMessage(ChatColor.RED + "You don't have permission to view this SubCategory!");
					return;
				}
				player.sendMessage(Util.getHeader(WordUtils.capitalizeFully(subCategory.getName().replaceAll("_", " "))));
				if(subCategory.getSubHeader() != null)
					player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "" + subCategory.getSubHeader());
				for (TextComponent component : this.simpleWiki.dataHolder.formatEntries(subCategory, player))
					player.spigot().sendMessage(component);
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
					player.sendMessage(RED + "Couldn't find the sub-category requested!");
					return;
				}
				entry = this.simpleWiki.dataHolder.getEntry(subCategory, args[3]);
				if(entry == null){
					player.sendMessage(RED + "Couldn't find the entry requested!");
					return;
				}
				if (entry.getPermissionNode() != null && !player.hasPermission(entry.getPermissionNode())) {
					player.sendMessage(ChatColor.RED + "You don't have permission to view this Entry!");
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

	@Override
	public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {

		List<String> results = new ArrayList<>();
		String search;

		if (commandSender instanceof Player) {

			Player player = ((Player) commandSender);

			PluginWiki wiki;
			Category category;
			SubCategory subCategory;
			switch (args.length) {
				case 0:
					results.addAll(this.simpleWiki.dataHolder.getWikiNames(player));
					break;
				case 1:
					search = args[0];
					wiki = this.simpleWiki.dataHolder.getWikiByName(search);
					if (wiki != null) {
						results.addAll(this.simpleWiki.dataHolder.getCategoryNames(wiki, player));
					}
					for (String str : this.simpleWiki.dataHolder.getWikiNames(player))
						if (str.toLowerCase().startsWith(search.toLowerCase()))
							results.add(str);

					break;
				case 2:
					search = args[1];
					wiki = this.simpleWiki.dataHolder.getWikiByName(args[0]);
					if (wiki != null) {
						category = this.simpleWiki.dataHolder.getCategory(wiki, search);
						if (category != null) {
							results.addAll(this.simpleWiki.dataHolder.getSubCategoryNames(category, player));
						}
						for (String str : this.simpleWiki.dataHolder.getCategoryNames(wiki, player))
							if (str.toLowerCase().startsWith(search.toLowerCase()))
								results.add(str);
					}

					break;
				case 3:
					search = args[2];
					wiki = this.simpleWiki.dataHolder.getWikiByName(args[0]);
					if (wiki != null) {
						category = this.simpleWiki.dataHolder.getCategory(wiki, args[1]);
						if (category != null) {
							subCategory = this.simpleWiki.dataHolder.getSubCategory(category, search);
							if (subCategory != null) {
								results.addAll(this.simpleWiki.dataHolder.getEntryNames(subCategory, player));
							}
							for (String str : this.simpleWiki.dataHolder.getSubCategoryNames(category, player))
								if (str.toLowerCase().startsWith(search.toLowerCase()))
									results.add(str);
						}
					}

					break;
				case 4:
					search = args[3];
					wiki = this.simpleWiki.dataHolder.getWikiByName(args[0]);
					if (wiki != null) {
						category = this.simpleWiki.dataHolder.getCategory(wiki, args[1]);
						if (category != null) {
							subCategory = this.simpleWiki.dataHolder.getSubCategory(category, args[2]);
							if (subCategory != null) {
								for (String str : this.simpleWiki.dataHolder.getEntryNames(subCategory, player))
									if (str.toLowerCase().startsWith(search.toLowerCase()))
										results.add(str);
							}

						}
					}
				default:
					break;
			}
			return results;
		}

		return ImmutableList.of();
	}
}
