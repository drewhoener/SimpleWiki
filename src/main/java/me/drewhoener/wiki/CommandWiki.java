package me.drewhoener.wiki;

import com.google.common.collect.ImmutableList;
import me.drewhoener.wiki.data.DataHolder;
import me.drewhoener.wiki.pages.Category;
import me.drewhoener.wiki.pages.Entry;
import me.drewhoener.wiki.pages.PluginWiki;
import me.drewhoener.wiki.pages.SubCategory;
import me.drewhoener.wiki.util.Util;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.bukkit.ChatColor.RED;

class CommandWiki implements TabExecutor {

	private final SimpleWiki simpleWiki;

	CommandWiki(SimpleWiki simpleWiki) {
		this.simpleWiki = simpleWiki;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {

		if (!(sender instanceof Player)) {
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

		switch (length) {
			case 0:
				player.sendMessage(Util.getHeader("Wikis"));
				player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "" + "Click the entries to navigate!");

				for (TextComponent[] component : this.simpleWiki.dataHolder.formatWikiEntries(player))
					player.spigot().sendMessage((BaseComponent[]) component);
				player.sendMessage(Util.getEnd());
				break;
			case 1:
				wiki = this.simpleWiki.dataHolder.getWikiByName(args[0]);
				if (wiki == null) {
					player.sendMessage(RED + "Couldn't find the wiki requested!");
					return;
				}
				if (!wiki.hasPermission(player)) {
					player.sendMessage(ChatColor.RED + "You don't have permission to view this Wiki!");
					return;
				}

				player.sendMessage(Util.getHeader(WordUtils.capitalizeFully(wiki.getName().replaceAll("_", " "))));
				if (wiki.getSubHeader() != null)
					player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "" + wiki.getSubHeader());
				for (TextComponent[] component : this.simpleWiki.dataHolder.formatCategoryEntries(wiki, player))
					player.spigot().sendMessage((BaseComponent[]) component);
				player.sendMessage(Util.getEnd());
				break;
			case 2:
				wiki = this.simpleWiki.dataHolder.getWikiByName(args[0]);
				if (wiki == null) {
					player.sendMessage(RED + "Couldn't find the wiki requested!");
					return;
				}
				category = this.simpleWiki.dataHolder.getCategory(wiki, args[1]);
				if (category == null) {
					player.sendMessage(RED + "Couldn't find the category requested!");
					return;
				}
				if (!category.hasPermission(player)) {
					player.sendMessage(ChatColor.RED + "You don't have permission to view this Category!");
					return;
				}

				player.sendMessage(Util.getHeader(WordUtils.capitalizeFully(category.getName().replaceAll("_", " "))));
				if (category.getSubHeader() != null)
					player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "" + category.getSubHeader());
				for (TextComponent[] component : this.simpleWiki.dataHolder.formatSubCategoryEntries(category, player))
					player.spigot().sendMessage((BaseComponent[]) component);
				player.sendMessage(Util.getEnd());
				break;
			case 3:
				wiki = this.simpleWiki.dataHolder.getWikiByName(args[0]);
				if (wiki == null) {
					player.sendMessage(RED + "Couldn't find the wiki requested!");
					return;
				}
				category = this.simpleWiki.dataHolder.getCategory(wiki, args[1]);
				if (category == null) {
					player.sendMessage(RED + "Couldn't find the category requested!");
					return;
				}
				subCategory = this.simpleWiki.dataHolder.getSubCategory(category, args[2]);
				if (subCategory == null) {
					player.sendMessage(RED + "Couldn't find the sub-category requested!");
					return;
				}
				if (!subCategory.hasPermission(player)) {
					player.sendMessage(ChatColor.RED + "You don't have permission to view this SubCategory!");
					return;
				}
				try {
					player.sendMessage(Util.getHeader(WordUtils.capitalizeFully(subCategory.getName().replaceAll("_", " "))));
					if (subCategory.getSubHeader() != null)
						player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "" + subCategory.getSubHeader());
					for (TextComponent[] component : this.simpleWiki.dataHolder.formatEntries(subCategory, player))
						player.spigot().sendMessage((BaseComponent[]) component);
					player.sendMessage(Util.getEnd());
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case 4:
				wiki = this.simpleWiki.dataHolder.getWikiByName(args[0]);
				if (wiki == null) {
					player.sendMessage(RED + "Couldn't find the wiki requested!");
					return;
				}
				category = this.simpleWiki.dataHolder.getCategory(wiki, args[1]);
				if (category == null) {
					player.sendMessage(RED + "Couldn't find the category requested!");
					return;
				}
				subCategory = this.simpleWiki.dataHolder.getSubCategory(category, args[2]);
				if (subCategory == null) {
					player.sendMessage(RED + "Couldn't find the sub-category requested!");
					return;
				}
				entry = this.simpleWiki.dataHolder.getEntry(subCategory, args[3]);
				if (entry == null) {
					player.sendMessage(RED + "Couldn't find the entry requested!");
					return;
				}
				if (!entry.hasPermission(player)) {
					player.sendMessage(ChatColor.RED + "You don't have permission to view this Entry!");
					return;
				}
				if (!entry.getRecipeList().isEmpty()) {
					Inventory inv = this.simpleWiki.getServer().createInventory(player, InventoryType.WORKBENCH);
					inv.setStorageContents(entry.getRecipeList().toArray(new ItemStack[10]));
					this.simpleWiki.dataHolder.noInteract.add(player.getUniqueId());
					player.openInventory(inv);
					player.updateInventory();
				}

				if (player.hasPermission(Util.Config.DEFAULT_PERMISSION)) {
					HoverEvent event = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to share this Entry in chat!").color(ChatColor.GOLD).create());
					ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/swiki " + entry.getParent().getParent().getParent().getName() + " " + entry.getParent().getParent().getName() + " " + entry.getParent().getName() + " " + entry.getName() + " broadcast");
					player.spigot().sendMessage((BaseComponent[]) Util.getHeader(WordUtils.capitalizeFully(entry.getName().replaceAll("_", " ")), event, clickEvent, ChatColor.getByChar(entry.getColor())));
				} else {
					player.sendMessage(Util.getHeader(WordUtils.capitalizeFully(entry.getName().replaceAll("_", " "))));
				}
				for (String line : entry.getDescriptionList()) {
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
				}
				player.sendMessage(Util.getEnd());
				break;
			case 5:
				wiki = this.simpleWiki.dataHolder.getWikiByName(args[0]);
				if (wiki == null) {
					player.sendMessage(RED + "Couldn't find the wiki requested!");
					return;
				}
				category = this.simpleWiki.dataHolder.getCategory(wiki, args[1]);
				if (category == null) {
					player.sendMessage(RED + "Couldn't find the category requested!");
					return;
				}
				subCategory = this.simpleWiki.dataHolder.getSubCategory(category, args[2]);
				if (subCategory == null) {
					player.sendMessage(RED + "Couldn't find the sub-category requested!");
					return;
				}
				entry = this.simpleWiki.dataHolder.getEntry(subCategory, args[3]);
				if (entry == null) {
					player.sendMessage(RED + "Couldn't find the entry requested!");
					return;
				}
				if (!entry.hasPermission(player)) {
					player.sendMessage(ChatColor.RED + "You don't have permission to view this Entry!");
					return;
				}
				if (!args[4].equalsIgnoreCase("broadcast")) {
					player.sendMessage(ChatColor.RED + "Invalid Wiki Format!");
					return;
				}

				for (Player par1Player : Bukkit.getOnlinePlayers()) {
					if (entry.hasPermission(par1Player)) {
						par1Player.spigot().sendMessage(shareEntry(entry, player));
					}
				}
				break;
			default:
				break;
		}

	}

	public BaseComponent[] shareEntry(Entry entry, Player playerSharing) {
		LinkedList<TextComponent> components = new LinkedList<>();
		String normalizedName = WordUtils.capitalizeFully(entry.getName().replaceAll("_", " "));

		components.add(new TextComponent(new ComponentBuilder(playerSharing.getName()).color(ChatColor.BLUE).append(" has shared a Wiki Entry: ").color(ChatColor.GOLD).create()));
		components.add(DataHolder.getFormattedPiece(normalizedName,
				new HoverEvent(HoverEvent.Action.SHOW_TEXT, entry.getHover()),
				new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/swiki " + entry.getParent().getParent().getParent().getName().toLowerCase() + " " + entry.getParent().getParent().getName() + " " + entry.getParent().getName() + " " + entry.getName()),
				entry.getColor()));
		return components.toArray(new TextComponent[components.size()]);
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
