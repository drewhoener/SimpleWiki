package me.drewhoener.wiki.pages;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Entry {
	private SubCategory parent;
	private String name;
	private String color;
	private List<String> descriptionList = new LinkedList<>();
	private ItemStack itemStack;

	public Entry(SubCategory parent, String name, String color, List<String> descriptionList, ItemStack itemStack) {
		this.parent = parent;
		this.name = name;
		this.color = color;
		this.descriptionList = descriptionList;
		this.itemStack = itemStack;
	}

	public Entry(SubCategory parent, ConfigurationSection configurationSection) {
		this(parent, configurationSection.getName(), configurationSection.getString("color", "&9"),
				configurationSection.getStringList("text"), new ItemStack(Material.matchMaterial(configurationSection.getString("itemType", "DIRT"))));
	}

	public String getName() {
		return name;
	}

	@SuppressWarnings("unchecked")
	public BaseComponent[] formatText(){

		ArrayList components = new ArrayList();

		TextComponent hoverMessage = new TextComponent(new ComponentBuilder("Description").color(ChatColor.DARK_AQUA).create());
		TextComponent newLineComp = new TextComponent(ComponentSerializer.parse("{text:\"\n\"}"));

		for(String line : this.descriptionList) {
			hoverMessage.addExtra(newLineComp);
			hoverMessage.addExtra(new TextComponent(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', line))));
		}

		components.add(hoverMessage);

		BaseComponent[] finalMessage = (BaseComponent[])components.toArray(new BaseComponent[components.size()]);

		return new ComponentBuilder(WordUtils.capitalizeFully(name.replaceAll("_", " "))).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, finalMessage)).create();
	}

	@Override
	public String toString() {
		return "Entry{" +
				"name='" + name + '\'' +
				'}';
	}

}
