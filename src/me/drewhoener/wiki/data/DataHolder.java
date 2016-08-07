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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class DataHolder {

	private SimpleWiki simpleWiki;

	private List<PluginWiki> wikiList = new ArrayList<>();
	public List<UUID> noInteract = new ArrayList<>();

	public DataHolder(SimpleWiki simpleWiki) {
		this.simpleWiki = simpleWiki;
	}

	public void addWiki(PluginWiki wiki){
		this.wikiList.add(wiki);
	}

	public List<PluginWiki> getWikiList() {
		return wikiList;
	}

	public void clearWikiList() {
		this.wikiList.clear();
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
			if (subCategory.getName().equalsIgnoreCase(arg))
				return subCategory;
		}
		return null;
	}

	public Entry getEntry(SubCategory subCategory, String arg) {
		for(Entry entry : subCategory.getEntryList()) {
			if (entry.getName().equalsIgnoreCase(arg))
				return entry;
		}
		return null;
	}

	public BaseComponent[][] formatWikiEntries() {

		this.sortList();
		List<BaseComponent[]> components = new ArrayList<>();
		ComponentBuilder builder = new ComponentBuilder("");
		String spacer = "   ";
		StringBuilder counter = new StringBuilder();
		int tally = 1;
		for(PluginWiki wiki : this.wikiList){
			String normalizedName = WordUtils.capitalizeFully(wiki.getName().replaceAll("_", " "));
			if(counter.append("[").append(normalizedName).append("]").length() <= 60) {
				ComponentBuilder textBuilder = new ComponentBuilder(ChatColor.GREEN + "Click me to go to the " + ChatColor.GOLD + normalizedName + ChatColor.GREEN + " wiki");
				builder.append("[").color(ChatColor.DARK_GREEN).append(normalizedName).color(ChatColor.GRAY)
						.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, textBuilder.create()))
						.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/swiki " + wiki.getName().toLowerCase()))
						.append("]").color(ChatColor.DARK_GREEN);
				builder.append(spacer);
				counter.append(spacer);
			}else{
				components.add(builder.create());
				builder = new ComponentBuilder("");
				ComponentBuilder textBuilder = new ComponentBuilder(ChatColor.GREEN + "Click me to go to the " + ChatColor.GOLD + normalizedName + ChatColor.GREEN + " wiki");
				builder.append("[").color(ChatColor.DARK_GREEN).append(normalizedName).color(ChatColor.GRAY)
						.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, textBuilder.create()))
						.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/swiki " + wiki.getName().toLowerCase()))
						.append("]").color(ChatColor.DARK_GREEN);
				builder.append(spacer);
				counter = new StringBuilder("").append("[").append(normalizedName).append("]").append(spacer);
			}

			if(tally == this.wikiList.size()){
				components.add(builder.create());
			}
			tally++;
		}

		return components.toArray(new BaseComponent[components.size()][]);
	}

	public BaseComponent[][] formatCategoryEntries(PluginWiki wiki) {

		wiki.sortCategories();
		List<BaseComponent[]> components = new ArrayList<>();
		ComponentBuilder builder = new ComponentBuilder("");
		StringBuilder counter = new StringBuilder();
		String spacer = "   ";
		int tally = 1;
		for(Category category : wiki.getCategoryList()){
			String normalizedName = WordUtils.capitalizeFully(category.getName().replaceAll("_", " "));
			if(counter.append("[").append(normalizedName).append("]").length() <= 60) {
				ComponentBuilder textBuilder = new ComponentBuilder(ChatColor.GREEN + "Click me to go to the " + ChatColor.GOLD + normalizedName + ChatColor.GREEN + " category");
				builder.append("[").color(ChatColor.DARK_GREEN).append(normalizedName).color(ChatColor.GRAY)
						.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, textBuilder.create()))
						.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/swiki " + category.getParent().getName().toLowerCase() + " " + category.getName().toLowerCase()))
						.append("]").color(ChatColor.DARK_GREEN);
				builder.append(spacer);
				counter.append(spacer);
			}else{
				components.add(builder.create());
				builder = new ComponentBuilder("");
				ComponentBuilder textBuilder = new ComponentBuilder(ChatColor.GREEN + "Click me to go to the " + ChatColor.GOLD + normalizedName + ChatColor.GREEN + " category");
				builder.append("[").color(ChatColor.DARK_GREEN).append(normalizedName).color(ChatColor.GRAY)
						.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, textBuilder.create()))
						.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/swiki " + category.getParent().getName().toLowerCase() + " " + category.getName().toLowerCase()))
						.append("]").color(ChatColor.DARK_GREEN);
				builder.append(spacer);
				counter = new StringBuilder("").append("[").append(normalizedName).append("]").append(spacer);
			}

			if(tally == wiki.getCategoryList().size()){
				components.add(builder.create());
			}
			tally++;
		}

		return components.toArray(new BaseComponent[components.size()][]);

	}

	public BaseComponent[][] formatSubCategoryEntries(Category category) {
		category.sortSubCategories();
		List<BaseComponent[]> components = new ArrayList<>();
		ComponentBuilder builder = new ComponentBuilder("");
		StringBuilder counter = new StringBuilder();
		String spacer = "   ";
		int tally = 1;
		for(SubCategory subCategory : category.getSubCategoryList()){
			String normalizedName = WordUtils.capitalizeFully(subCategory.getName().replaceAll("_", " "));
			if(counter.append("[").append(normalizedName).append("]").length() <= 60) {
				ComponentBuilder textBuilder = new ComponentBuilder(ChatColor.GREEN + "Click me to go to the " + ChatColor.GOLD + normalizedName + ChatColor.GREEN + " sub-category");
				builder.append("[").color(ChatColor.DARK_GREEN).append(normalizedName).color(ChatColor.GRAY)
						.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, textBuilder.create()))
						.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/swiki " + category.getParent().getName().toLowerCase() + " " + category.getName().toLowerCase() + " " + subCategory.getName().toLowerCase()))
						.append("]").color(ChatColor.DARK_GREEN);
				builder.append(spacer);
				counter.append(spacer);
			}else{
				components.add(builder.create());
				builder = new ComponentBuilder("");
				ComponentBuilder textBuilder = new ComponentBuilder(ChatColor.GREEN + "Click me to go to the " + ChatColor.GOLD + normalizedName + ChatColor.GREEN + " sub-category");
				builder.append("[").color(ChatColor.DARK_GREEN).append(normalizedName).color(ChatColor.GRAY)
						.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, textBuilder.create()))
						.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/swiki " + category.getParent().getName().toLowerCase() + " " + category.getName().toLowerCase() + " " + subCategory.getName().toLowerCase()))
						.append("]").color(ChatColor.DARK_GREEN);
				builder.append(spacer);
				counter = new StringBuilder("").append("[").append(normalizedName).append("]").append(spacer);
			}
			if(tally == category.getSubCategoryList().size()){
				components.add(builder.create());
			}
			tally++;
		}

		return components.toArray(new BaseComponent[components.size()][]);
	}

	public BaseComponent[][] formatEntries(SubCategory subCategory){
		subCategory.sortEntries();
		List<BaseComponent[]> components = new ArrayList<>();
		ComponentBuilder builder = new ComponentBuilder("");
		StringBuilder counter = new StringBuilder();
		String spacer = "   ";
		int tally = 1;
		for(Entry entry : subCategory.getEntryList()){
			String normalizedName = WordUtils.capitalizeFully(entry.getName().replaceAll("_", " "));
			if(counter.append("[").append(normalizedName).append("]").length() <= 60) {
				builder.append("[").color(ChatColor.DARK_GREEN)
						.append(org.apache.commons.lang.WordUtils.capitalizeFully(entry.getName().replaceAll("_", " "))).color(ChatColor.GRAY)
						.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, entry.getHover()))
						.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/swiki " + entry.getParent().getParent().getParent().getName().toLowerCase() + " " + entry.getParent().getParent().getName() + " " + entry.getParent().getName() + " " + entry.getName()))
						.append("]").color(ChatColor.DARK_GREEN);
				builder.append(spacer);
				counter.append(spacer);
			}else{
				components.add(builder.create());
				builder = new ComponentBuilder("");
				builder.append("[").color(ChatColor.DARK_GREEN)
						.append(org.apache.commons.lang.WordUtils.capitalizeFully(entry.getName().replaceAll("_", " "))).color(ChatColor.GRAY)
						.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, entry.getHover()))
						.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/swiki " + entry.getParent().getParent().getParent().getName().toLowerCase() + " " + entry.getParent().getParent().getName() + " " + entry.getParent().getName() + " " + entry.getName()))
						.append("]").color(ChatColor.DARK_GREEN);
				builder.append(spacer);
				counter = new StringBuilder("").append("[").append(normalizedName).append("]").append(spacer);
			}
			if(tally == subCategory.getEntryList().size()){
				components.add(builder.create());
			}
			tally++;
		}

		return components.toArray(new BaseComponent[components.size()][]);
	}

}
