package me.drewhoener.wiki.util;

import me.drewhoener.wiki.SimpleWiki;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
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

	public static List<String> splitString(String s, int size){
		List<String> stringParts = new ArrayList<>();
		String totalMessage;

		totalMessage = s;
		String[] parts = totalMessage.split(" ");
		String totalStr = "";
		String tempStr = parts[0];
		for(int i = 1; i < parts.length; i++){

			tempStr += (" " + parts[i]);

			if(tempStr.length() > size){

				stringParts.add(totalStr);
				tempStr = parts[i];

			}

			totalStr = tempStr;

		}

		stringParts.add(totalStr);
		return stringParts;
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

	public static String getEnd() {

		return ChatColor.DARK_AQUA + "" + ChatColor.STRIKETHROUGH + "-----------------------------------------------------";
	}

	public static String capitalizeFirst(String line) {
		line = line.toLowerCase();
		return Character.toUpperCase(line.charAt(0)) + line.substring(1);
	}

}
