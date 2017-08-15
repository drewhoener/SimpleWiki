package me.drewhoener.wiki.pages.generic;

import net.md_5.bungee.api.chat.TextComponent;

import java.util.LinkedList;

public interface IBookGUI extends INameable {

	default String getAuthor() {
		return "red_stoner62";
	}

	String getDescription();

	void setDescription(String description);

	LinkedList<TextComponent> getLines();

	void setLines(String rawText);
}
