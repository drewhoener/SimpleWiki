package me.drewhoener.wiki.pages;

import me.drewhoener.wiki.util.Util;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class SubCategory {

	private String name;
	private List<Entry> entryList = new LinkedList<>();
	private String permissionNode;
	private String subHeader;
	private Category parent;

	public SubCategory(Category parent, ConfigurationSection section) {
		this.name = section.getName();
		this.parent = parent;
		this.permissionNode = section.getString("permissionNode", null);
		this.subHeader = section.getString("subHeader", null);
		for (String entryString : section.getKeys(false)) {
			if (!Util.reservedWords.contains(entryString))
				this.entryList.add(new Entry(this, section.getConfigurationSection(entryString)));
		}
	}

	public String getName() {
		return name;
	}

	public void sortEntries() {
		Collections.sort(this.entryList, new Comparator<Entry>() {
			@Override
			public int compare(Entry o1, Entry o2) {
				return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
			}
		});
	}

	public List<Entry> getEntryList() {
		return entryList;
	}

	public Category getParent() {
		return parent;
	}

	public String getPermissionNode() {
		return permissionNode;
	}

	public String getSubHeader() {
		return subHeader;
	}

	@Override
	public String toString() {
		return "SubCategory{" +
				"name='" + name + '\'' +
				", pageList=" + entryList +
				'}';
	}
}
