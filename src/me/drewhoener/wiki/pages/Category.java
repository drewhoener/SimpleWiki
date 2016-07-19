package me.drewhoener.wiki.pages;

import org.bukkit.configuration.ConfigurationSection;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class Category {

	private String name;
	private PluginWiki parent;
	private List<SubCategory> subCategoryList = new LinkedList<>();

	public Category(PluginWiki parent, ConfigurationSection section){
		this.name = section.getName();
		this.parent = parent;
		for(String categoryString : section.getKeys(false))
			this.subCategoryList.add(new SubCategory(this, section.getConfigurationSection(categoryString)));
	}

	public Category(PluginWiki parent, String name, ConfigurationSection section){
		this.name = name;
		this.parent = parent;
		for(String categoryString : section.getKeys(false))
			this.subCategoryList.add(new SubCategory(this, section.getConfigurationSection(categoryString)));
	}

	public void sortSubCategories() {
		Collections.sort(this.subCategoryList, new Comparator<SubCategory>() {
			@Override
			public int compare(SubCategory o1, SubCategory o2) {
				return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
			}
		});
	}

	public String getName() {
		return name;
	}

	public List<SubCategory> getSubCategoryList() {
		return subCategoryList;
	}

	public PluginWiki getParent() {
		return parent;
	}

	@Override
	public String toString() {
		return "Category{" +
				"name='" + name + '\'' +
				", subCategoryList=" + subCategoryList +
				'}';
	}
}
