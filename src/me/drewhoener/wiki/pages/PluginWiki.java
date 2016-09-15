package me.drewhoener.wiki.pages;

import me.drewhoener.wiki.pages.generic.INameable;
import me.drewhoener.wiki.util.Util;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PluginWiki implements INameable {

	private String name;
	String permissionNode;
	private String subHeader;
	private List<Category> categoryList = new ArrayList<>();

	public PluginWiki(String name, YamlConfiguration wiki) {
		this.name = name;
		this.permissionNode = wiki.getString("permissionNode", null);
		this.subHeader = wiki.getString("subHeader", null);
		for (String categoryString : wiki.getKeys(false))
			if (!Util.reservedWords.contains(categoryString))
				this.categoryList.add(new Category(this, wiki.getConfigurationSection(categoryString)));
	}

	public String getName() {
		return name;
	}

	public List<Category> getCategoryList() {
		return categoryList;
	}

	public boolean hasPermission(Player player) {

		return Util.simplePermissionCheck(player, this.permissionNode);
	}

	public String getSubHeader() {
		return subHeader;
	}

	@Override
	public String toString() {
		return "PluginWiki{" +
				"name='" + name + '\'' +
				", categoryList=" + categoryList +
				'}';
	}
}
