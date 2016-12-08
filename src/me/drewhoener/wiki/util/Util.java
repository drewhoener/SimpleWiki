package me.drewhoener.wiki.util;

import me.drewhoener.wiki.SimpleWiki;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Util {

	public static final File WIKI_DIR;
	public static final List<String> reservedWords = Arrays.asList("permissionNode", "subHeader");

	static {

		Bukkit.getLogger().info("Datafolder: " + SimpleWiki.dataFolder.getAbsolutePath());

		WIKI_DIR = new File(SimpleWiki.dataFolder, "wikis");
	}

	protected Util() {
	}

	public static String getHeader(String title) { //53 chars, 51 with brackets

		String insert = ChatColor.DARK_GREEN + "[" + ChatColor.DARK_AQUA + title + ChatColor.DARK_GREEN + "]";
		int insertLength = ChatColor.stripColor(insert).length();

		int s = (53 - insertLength);

		int ta = Math.round((s / 2));

		String o = "";

		for (int i = 0; i < ta; i++) {

			o = o + "-";

		}

		return ChatColor.DARK_AQUA + "" + ChatColor.STRIKETHROUGH + o + insert + ChatColor.DARK_AQUA + "" + ChatColor.STRIKETHROUGH + o;

	}

	public static TextComponent[] getHeader(String title, HoverEvent hoverEvent, ClickEvent clickEvent, net.md_5.bungee.api.ChatColor textColor) {
		LinkedList<TextComponent> components = new LinkedList<>();
		String insert = ChatColor.DARK_GREEN + "[" + textColor + ChatColor.stripColor(title) + ChatColor.DARK_GREEN + "]";
		int insertLength = ChatColor.stripColor(insert).length();

		int s = (53 - insertLength);

		int ta = Math.round((s / 2));

		String o = "";

		for (int i = 0; i < ta; i++) {

			o = o + "-";
		}
		components.add(new TextComponent(new ComponentBuilder(o).color(net.md_5.bungee.api.ChatColor.DARK_AQUA).strikethrough(true).create()));
		components.add(new TextComponent(new ComponentBuilder("").event(hoverEvent).event(clickEvent)
				.append("[").color(net.md_5.bungee.api.ChatColor.DARK_GREEN).append(title).color(textColor)
				.append("]").color(net.md_5.bungee.api.ChatColor.DARK_GREEN).create()));
		components.add(new TextComponent(new ComponentBuilder(o).color(net.md_5.bungee.api.ChatColor.DARK_AQUA).strikethrough(true).create()));
		return components.toArray(new TextComponent[components.size()]);
	}

	public static String getEnd() {

		return ChatColor.DARK_AQUA + "" + ChatColor.STRIKETHROUGH + "-----------------------------------------------------";
	}

	public static boolean simplePermissionCheck(Player player, String permissionNode) {
		return permissionNode == null || player.hasPermission(permissionNode);
	}

	/**
	 * Determines if a string is an integer
	 *
	 * @param str The string to check
	 * @return If it is a valid integer
	 */
	public static boolean isInteger(String str) {
		if (str == null) {
			return false;
		}
		int length = str.length();
		if (length == 0) {
			return false;
		}
		int i = 0;
		if (str.charAt(0) == '-') {
			if (length == 1) {
				return false;
			}
			i = 1;
		}
		for (; i < length; i++) {
			char c = str.charAt(i);
			if (c <= '/' || c >= ':') {
				return false;
			}
		}
		return true;
	}

	public static class Config {
		public static boolean DEBUG = false;
		public static String DEFAULT_PERMISSION = "";
	}

}
