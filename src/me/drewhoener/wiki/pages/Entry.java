package me.drewhoener.wiki.pages;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Bukkit;
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
	private List<ItemStack> recipeList = new LinkedList<>();
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
		if (configurationSection.contains("recipe")) {
			for (String s : configurationSection.getStringList("recipe")) {
				Bukkit.getLogger().severe("ItemStack: " + s);
				this.recipeList.add(new ItemStack(Material.matchMaterial(s)));
			}

		}
	}

	public String getName() {
		return name;
	}

	public List<ItemStack> getRecipeList() {
		return recipeList;
	}

	public SubCategory getParent() {
		return parent;
	}

	public List<String> getDescriptionList() {
		return descriptionList;
	}

	@SuppressWarnings("unchecked")
	public BaseComponent[] getHover(){

		ArrayList components = new ArrayList();

		TextComponent hoverMessage = new TextComponent(new ComponentBuilder("Description").color(ChatColor.DARK_AQUA).create());
		TextComponent newLineComp = new TextComponent(ComponentSerializer.parse("{text:\"\n\"}"));

		for(String line : this.descriptionList) {
			hoverMessage.addExtra(newLineComp);
			hoverMessage.addExtra(new TextComponent(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', line))));
		}

		components.add(hoverMessage);

		return (BaseComponent[])components.toArray(new BaseComponent[components.size()]);
	}

	@Override
	public String toString() {
		return "Entry{" +
				"name='" + name + '\'' +
				'}';
	}
}
