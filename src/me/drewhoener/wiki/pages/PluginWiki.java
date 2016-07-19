package me.drewhoener.wiki.pages;

import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PluginWiki {

	private String name;
	private List<Category> categoryList = new ArrayList<>();

	public PluginWiki(String name, YamlConfiguration wiki) {
		this.name = name;
		for(String categoryString : wiki.getKeys(false))
			this.categoryList.add(new Category(this, wiki.getConfigurationSection(categoryString)));
	}

	public String getName() {
		return name;
	}

	public List<Category> getCategoryList() {
		return categoryList;
	}

	public void sortCategories(){
		Collections.sort(this.categoryList, new Comparator<Category>() {
			@Override
			public int compare(Category o1, Category o2) {
				return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
			}
		});
	}

	@Override
	public String toString() {
		return "PluginWiki{" +
				"name='" + name + '\'' +
				", categoryList=" + categoryList +
				'}';
	}
}
