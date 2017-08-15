package me.drewhoener.wiki.book;

import me.drewhoener.wiki.pages.generic.BookFormatted;
import me.drewhoener.wiki.util.Util;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftMetaBook;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BookUtil {

	private static final int LINE_CHARS = 19;

	public static ItemStack getWikiBook(BookFormatted section) {
		ItemStack stack = new ItemStack(Material.WRITTEN_BOOK);
		BookMeta meta = ((BookMeta) stack.getItemMeta());
		meta.setGeneration(BookMeta.Generation.TATTERED);
		meta.setAuthor(section.getAuthor());
		meta.setTitle(section.getName());
		if (meta instanceof CraftMetaBook) {
			CraftMetaBook metaBook = ((CraftMetaBook) meta);
			metaBook.pages.addAll(section.toPages());
		}
		stack.setItemMeta(meta);

		return stack;
	}

	public static String addSpaceBetween(String s1, String s2) {
		int length = s1.length() + s2.length();
		if (length > LINE_CHARS)
			return alignText(s1 + "\n" + s2, TextAlign.CENTER);
		if (length == LINE_CHARS)
			return s1 + s2;
		StringBuilder builder = new StringBuilder(s1);
		String space = spaceBetween(s1, s2);
		return builder.append(space).append(s2).toString();
	}

	public static String spaceBetween(String s1, String s2) {
		int length = s1.length() + s2.length();
		if (length > LINE_CHARS)
			return "";
		if (length == LINE_CHARS)
			return "";
		StringBuilder builder = new StringBuilder();
		int delta = LINE_CHARS - length;
		for (int i = 0; i < delta; i++)
			builder.append(" ");
		return builder.toString();
	}

	public static String alignText(String s, TextAlign align) {
		if (Math.abs(s.length() - LINE_CHARS) <= 1 || (s.length() > LINE_CHARS && s.indexOf(' ') == -1))
			return s;
		StringBuilder finalBuilder = new StringBuilder();
		StringBuilder tempBuilder = new StringBuilder();
		List<String> strings = new LinkedList<>(Arrays.asList(s.split(" ")));
		while (!strings.isEmpty()) {
			String toAdd = strings.remove(0);
			if ((tempBuilder.length() + toAdd.length()) > LINE_CHARS) {
				finalBuilder.append(addDelta(tempBuilder.toString(), align)).append("\n");
				tempBuilder = new StringBuilder();
			}

			tempBuilder.append(toAdd).append(" ");
		}

		finalBuilder.append(addDelta(tempBuilder.toString(), align));

		return finalBuilder.toString();
	}

	private static String addDelta(String s, TextAlign align) {
		s = s.trim();
		if (s.length() > LINE_CHARS)
			return s;
		StringBuilder sBuilder = new StringBuilder(s);
		int delta;
		switch (align) {
			case CENTER:
				delta = (LINE_CHARS - s.length()) / 2;
				for (int i = 0; i < delta; i++)
					sBuilder.insert(0, " ");
				break;
			case RIGHT:
				delta = (LINE_CHARS - s.length());
				for (int i = 0; i < delta; i++)
					sBuilder.insert(0, " ");
				break;
			case LEFT:
			default:
				break;
		}

		return sBuilder.toString();
	}

	static List<TextComponent> parseLines(String[] newLines) {
		List<TextComponent> components = new LinkedList<>();

		Pattern pattern = Pattern.compile("\\[(\\[.*])+]");

		for (String string : newLines) {
			TextComponent fullLine = new TextComponent();

			Matcher matcher = pattern.matcher(string);
			int start, end, oldStart, oldEnd;

			if (!matcher.find()) {
				String color = ChatColor.translateAlternateColorCodes('&', string);
				components.add(new TextComponent(TextComponent.fromLegacyText(color)));
				continue;
			}
			oldStart = matcher.start();
			oldEnd = matcher.end();

			while (matcher.find()) {

				start = matcher.start();
				end = matcher.end();

				String tag = string.substring(oldStart, oldEnd);
				String target = string.substring(oldEnd, start);
				TextComponent component = parseTag(tag, target);
				if (component != null)
					fullLine.addExtra(component);

				oldStart = start;
				oldEnd = end;

			}

			String tag = string.substring(oldStart, oldEnd);
			String target = string.substring(oldEnd);
			TextComponent component = parseTag(tag, target);
			if (component != null)
				fullLine.addExtra(component);

			components.add(fullLine);
		}

		return components;
	}

	private static TextComponent parseTag(String tag, String target) {
		while (tag.startsWith("\\["))
			tag = tag.substring(1);
		while (tag.endsWith("\\]"))
			tag = tag.substring(0, tag.length());
		String[] tagParts = tag.split(":");
		if (tagParts.length < 1)
			return null;

		target = ChatColor.translateAlternateColorCodes('&', target);
		TextComponent component = new TextComponent();


		//region Pre-Link formatting (centered text, etc)


		//endregion


		for (Iterator<String> iterator = Arrays.asList(target.split("\n")).iterator(); iterator.hasNext(); ) {
			String line = iterator.next();
			if (line.trim().isEmpty())
				component.addExtra(new TextComponent(line.trim()));
			if (iterator.hasNext())
				component.addExtra(Util.NEW_LINE);
		}

		while (target.endsWith("\n"))
			target = target.substring(0, tag.length());

		//region Link Formatting, hover

		switch (tagParts[0].toLowerCase()) {

			case "linkto":
				break;
			default:
				break;
		}

		//endregion
	}

	public enum TextAlign {
		LEFT,
		RIGHT,
		CENTER
	}

}
