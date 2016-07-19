package me.drewhoener.wiki.data;

import me.drewhoener.wiki.SimpleWiki;
import me.drewhoener.wiki.pages.Category;
import me.drewhoener.wiki.pages.Entry;
import me.drewhoener.wiki.pages.PluginWiki;
import me.drewhoener.wiki.pages.SubCategory;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DataHolder {

	private SimpleWiki simpleWiki;

	private List<PluginWiki> wikiList = new ArrayList<>();

	public DataHolder(SimpleWiki simpleWiki) {
		this.simpleWiki = simpleWiki;
	}

	public void addWiki(PluginWiki wiki){
		this.wikiList.add(wiki);
	}

	public List<PluginWiki> getWikiList() {
		return wikiList;
	}

	private void sortList(){
		Collections.sort(this.wikiList, new Comparator<PluginWiki>() {
			@Override
			public int compare(PluginWiki o1, PluginWiki o2) {
				return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
			}
		});
	}

	public PluginWiki getWikiByName(String arg) {
		for(PluginWiki wiki : this.wikiList)
			if(arg.equalsIgnoreCase(wiki.getName()))
				return wiki;
		return null;
	}

	public Category getCategory(PluginWiki wiki, String arg) {
		for(Category category : wiki.getCategoryList())
			if(category.getName().equalsIgnoreCase(arg))
				return category;
		return null;
	}

	public SubCategory getSubCategory(Category category, String arg) {
		for(SubCategory subCategory : category.getSubCategoryList()) {
			Bukkit.broadcastMessage(subCategory.getName());
			if (subCategory.getName().equalsIgnoreCase(arg))
				return subCategory;
		}
		return null;
	}

	public BaseComponent[][] formatWikiEntries() {

		this.sortList();
		List<BaseComponent[]> components = new ArrayList<>();
		int componentCounter = 0;
		int totalCounter = 1;
		ComponentBuilder builder = new ComponentBuilder("");
		String spacer = "   ";
		for(PluginWiki wiki : this.wikiList){
			String normalizedName = WordUtils.capitalizeFully(wiki.getName().replaceAll("_", " "));
			ComponentBuilder textBuilder = new ComponentBuilder(ChatColor.GREEN + "Click me to go to the " + ChatColor.GOLD + normalizedName + ChatColor.GREEN + " wiki");
			builder.append("[").color(ChatColor.DARK_AQUA).append(normalizedName).color(ChatColor.GRAY)
					.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, textBuilder.create()))
					.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/swiki " + wiki.getName().toLowerCase()))
					.append("]").color(ChatColor.DARK_AQUA);

			if(componentCounter >= 2 || totalCounter == this.wikiList.size()){

				components.add(builder.create());
				builder = new ComponentBuilder("");

				componentCounter = -1;
			}else{

				builder.append(spacer);
			}

			componentCounter++;
			totalCounter++;
		}

		return components.toArray(new BaseComponent[components.size()][]);
	}

	public BaseComponent[][] formatCategoryEntries(PluginWiki wiki) {

		wiki.sortCategories();
		List<BaseComponent[]> components = new ArrayList<>();
		int componentCounter = 0;
		int totalCounter = 1;
		ComponentBuilder builder = new ComponentBuilder("");
		String spacer = "   ";
		for(Category category : wiki.getCategoryList()){
			String normalizedName = WordUtils.capitalizeFully(category.getName().replaceAll("_", " "));
			ComponentBuilder textBuilder = new ComponentBuilder(ChatColor.GREEN + "Click me to go to the " + ChatColor.GOLD + normalizedName + ChatColor.GREEN + " category");
			builder.append("[").color(ChatColor.DARK_AQUA).append(normalizedName).color(ChatColor.GRAY)
					.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, textBuilder.create()))
					.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/swiki " + category.getParent().getName().toLowerCase() + " " + category.getName().toLowerCase()))
					.append("]").color(ChatColor.DARK_AQUA);

			if(componentCounter >= 2 || totalCounter == wiki.getCategoryList().size()){

				components.add(builder.create());
				builder = new ComponentBuilder("");

				componentCounter = -1;
			}else{

				builder.append(spacer);
			}

			componentCounter++;
			totalCounter++;
		}

		return components.toArray(new BaseComponent[components.size()][]);

	}

	public BaseComponent[][] formatSubCategoryEntries(Category category) {
		category.sortSubCategories();
		List<BaseComponent[]> components = new ArrayList<>();
		int componentCounter = 0;
		int totalCounter = 1;
		ComponentBuilder builder = new ComponentBuilder("");
		String spacer = "   ";
		for(SubCategory subCategory : category.getSubCategoryList()){
			String normalizedName = WordUtils.capitalizeFully(subCategory.getName().replaceAll("_", " "));
			ComponentBuilder textBuilder = new ComponentBuilder(ChatColor.GREEN + "Click me to go to the " + ChatColor.GOLD + normalizedName + ChatColor.GREEN + " sub-category");
			builder.append("[").color(ChatColor.DARK_AQUA).append(normalizedName).color(ChatColor.GRAY)
					.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, textBuilder.create()))
					.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/swiki " + category.getParent().getName().toLowerCase() + " " + category.getName().toLowerCase() + " " + subCategory.getName().toLowerCase()))
					.append("]").color(ChatColor.DARK_AQUA);

			if(componentCounter >= 2 || totalCounter == category.getSubCategoryList().size()){

				components.add(builder.create());
				builder = new ComponentBuilder("");

				componentCounter = -1;
			}else{

				builder.append(spacer);
			}

			componentCounter++;
			totalCounter++;
		}

		return components.toArray(new BaseComponent[components.size()][]);
	}

	public BaseComponent[][] formatEntries(SubCategory subCategory) {
		subCategory.sortEntries();
		List<BaseComponent[]> components = new ArrayList<>();
		for(Entry entry : subCategory.getEntryList()){
			components.add(entry.formatText());
		}

		return components.toArray(new BaseComponent[components.size()][]);
	}
}
