package me.drewhoener.wiki.data;

import me.drewhoener.wiki.pages.Category;
import me.drewhoener.wiki.pages.Entry;
import me.drewhoener.wiki.pages.generic.INameable;
import me.drewhoener.wiki.pages.PluginWiki;
import me.drewhoener.wiki.pages.SubCategory;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;

@SuppressWarnings("Duplicates")
public class DataHolder {

	private List<PluginWiki> wikiList = new ArrayList<>();
	public List<UUID> noInteract = new ArrayList<>();

	public void addWiki(PluginWiki wiki) {
		this.wikiList.add(wiki);
	}

	public List<PluginWiki> getWikiList() {
		return wikiList;
	}

	public void clearWikiList() {
		this.wikiList.clear();
	}

	private Comparator<INameable> nameableComparator = new Comparator<INameable>() {
		@Override
		public int compare(INameable o1, INameable o2) {
			return o1.getName().compareToIgnoreCase(o2.getName());
		}
	};

	public PluginWiki getWikiByName(String arg) {
		for (PluginWiki wiki : this.wikiList)
			if (arg.equalsIgnoreCase(wiki.getName()))
				return wiki;
		return null;
	}

	public Category getCategory(PluginWiki wiki, String arg) {
		for (Category category : wiki.getCategoryList())
			if (category.getName().equalsIgnoreCase(arg))
				return category;
		return null;
	}

	public SubCategory getSubCategory(Category category, String arg) {
		for (SubCategory subCategory : category.getSubCategoryList()) {
			if (subCategory.getName().equalsIgnoreCase(arg))
				return subCategory;
		}
		return null;
	}

	public Entry getEntry(SubCategory subCategory, String arg) {
		for (Entry entry : subCategory.getEntryList()) {
			if (entry.getName().equalsIgnoreCase(arg))
				return entry;
		}
		return null;
	}

	public Set<String> getWikiNames(Player player) {
		Set<String> names = new HashSet<>();
		for (PluginWiki wiki : this.wikiList) {
			if (player != null)
				if (!wiki.hasPermission(player))
					continue;
			names.add(wiki.getName());
		}
		return names;
	}

	public Set<String> getCategoryNames(PluginWiki wiki, Player player) {
		Set<String> names = new HashSet<>();
		for (Category category : wiki.getCategoryList()) {
			if (player != null)
				if (!category.hasPermission(player))
					continue;
			names.add(category.getName());
		}
		return names;
	}

	public Set<String> getSubCategoryNames(Category category, Player player) {
		Set<String> names = new HashSet<>();
		for (SubCategory subcat : category.getSubCategoryList()) {
			if (player != null)
				if (!subcat.hasPermission(player))
					continue;
			names.add(subcat.getName());
		}
		return names;
	}

	public Set<String> getEntryNames(SubCategory subCategory, Player player) {
		Set<String> names = new HashSet<>();
		for (Entry entry : subCategory.getEntryList()) {
			if (player != null)
				if (!entry.hasPermission(player))
					continue;
			names.add(entry.getName());
		}
		return names;
	}

	public TextComponent[][] formatWikiEntries(Player player) {

		TreeMap<PluginWiki, TextComponent> workingList = new TreeMap<>(nameableComparator);
		LinkedList<TextComponent[]> finalComponents = new LinkedList<>();
		LinkedList<TextComponent> workingComponent = new LinkedList<>();
		String spacer = "   ";
		StringBuilder counter = new StringBuilder();

		for (PluginWiki wiki : this.wikiList) {
			if (!wiki.hasPermission(player))
				continue;
			String normalizedName = WordUtils.capitalizeFully(wiki.getName().replaceAll("_", " "));
			ComponentBuilder textBuilder = new ComponentBuilder(ChatColor.GREEN + "Click me to go to the " + ChatColor.GOLD + normalizedName + ChatColor.GREEN + " wiki");
			workingList.put(wiki, getFormattedPiece(normalizedName,
					new HoverEvent(HoverEvent.Action.SHOW_TEXT, textBuilder.create()),
					new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/swiki " + wiki.getName().toLowerCase())));
		}
		for (Map.Entry<PluginWiki, TextComponent> entry : workingList.entrySet()) {
			String normalizedName = WordUtils.capitalizeFully(entry.getKey().getName().replaceAll("_", " "));
			if (counter.append("[").append(normalizedName).append("]").length() <= 60) {
				workingComponent.add(entry.getValue());
				workingComponent.add(new TextComponent(TextComponent.fromLegacyText(spacer)));
				counter.append(spacer);
			} else {
				finalComponents.add(workingComponent.toArray(new TextComponent[finalComponents.size()]));
				workingComponent.clear();
				workingComponent.add(entry.getValue());
				workingComponent.add(new TextComponent(TextComponent.fromLegacyText(spacer)));
				counter = new StringBuilder("").append("[").append(normalizedName).append("]").append(spacer);
			}
		}
		finalComponents.add(workingComponent.toArray(new TextComponent[finalComponents.size()]));

		return finalComponents.toArray(new TextComponent[finalComponents.size()][]);
	}

	public TextComponent[][] formatCategoryEntries(PluginWiki wiki, Player player) {

		TreeMap<Category, TextComponent> workingList = new TreeMap<>(nameableComparator);
		LinkedList<TextComponent[]> finalComponents = new LinkedList<>();
		LinkedList<TextComponent> workingComponent = new LinkedList<>();
		String spacer = "   ";
		StringBuilder counter = new StringBuilder();

		for (Category category : wiki.getCategoryList()) {
			if (!category.hasPermission(player))
				continue;
			String normalizedName = WordUtils.capitalizeFully(category.getName().replaceAll("_", " "));
			ComponentBuilder textBuilder = new ComponentBuilder(ChatColor.GREEN + "Click me to go to the " + ChatColor.GOLD + normalizedName + ChatColor.GREEN + " category");
			workingList.put(category, getFormattedPiece(normalizedName,
					new HoverEvent(HoverEvent.Action.SHOW_TEXT, textBuilder.create()),
					new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/swiki " + category.getParent().getName().toLowerCase() + " " + category.getName().toLowerCase())));
		}
		for (Map.Entry<Category, TextComponent> entry : workingList.entrySet()) {
			String normalizedName = WordUtils.capitalizeFully(entry.getKey().getName().replaceAll("_", " "));
			if (counter.append("[").append(normalizedName).append("]").length() <= 60) {
				workingComponent.add(entry.getValue());
				workingComponent.add(new TextComponent(TextComponent.fromLegacyText(spacer)));
				counter.append(spacer);
			} else {
				finalComponents.add(workingComponent.toArray(new TextComponent[finalComponents.size()]));
				workingComponent.clear();
				workingComponent.add(entry.getValue());
				workingComponent.add(new TextComponent(TextComponent.fromLegacyText(spacer)));
				counter = new StringBuilder("").append("[").append(normalizedName).append("]").append(spacer);
			}
		}
		finalComponents.add(workingComponent.toArray(new TextComponent[finalComponents.size()]));

		return finalComponents.toArray(new TextComponent[finalComponents.size()][]);

	}

	public TextComponent[][] formatSubCategoryEntries(Category category, Player player) {
		TreeMap<SubCategory, TextComponent> workingList = new TreeMap<>(nameableComparator);
		LinkedList<TextComponent[]> finalComponents = new LinkedList<>();
		LinkedList<TextComponent> workingComponent = new LinkedList<>();
		String spacer = "   ";
		StringBuilder counter = new StringBuilder();

		for (SubCategory subCategory : category.getSubCategoryList()) {
			if (!subCategory.hasPermission(player))
				continue;
			String normalizedName = WordUtils.capitalizeFully(subCategory.getName().replaceAll("_", " "));
			ComponentBuilder textBuilder = new ComponentBuilder(ChatColor.GREEN + "Click me to go to the " + ChatColor.GOLD + normalizedName + ChatColor.GREEN + " sub-category");
			workingList.put(subCategory, getFormattedPiece(normalizedName,
					new HoverEvent(HoverEvent.Action.SHOW_TEXT, textBuilder.create()),
					new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/swiki " + category.getParent().getName().toLowerCase() + " " + category.getName().toLowerCase() + " " + subCategory.getName().toLowerCase())));
		}
		for (Map.Entry<SubCategory, TextComponent> entry : workingList.entrySet()) {
			String normalizedName = WordUtils.capitalizeFully(entry.getKey().getName().replaceAll("_", " "));
			if (counter.append("[").append(normalizedName).append("]").length() <= 60) {
				workingComponent.add(entry.getValue());
				workingComponent.add(new TextComponent(TextComponent.fromLegacyText(spacer)));
				counter.append(spacer);
			} else {
				finalComponents.add(workingComponent.toArray(new TextComponent[finalComponents.size()]));
				workingComponent.clear();
				workingComponent.add(entry.getValue());
				workingComponent.add(new TextComponent(TextComponent.fromLegacyText(spacer)));
				counter = new StringBuilder("").append("[").append(normalizedName).append("]").append(spacer);
			}
		}
		finalComponents.add(workingComponent.toArray(new TextComponent[finalComponents.size()]));

		return finalComponents.toArray(new TextComponent[finalComponents.size()][]);
	}

	public TextComponent[][] formatEntries(SubCategory subCategory, Player player) {
		TreeMap<Entry, TextComponent> workingList = new TreeMap<>(nameableComparator);
		LinkedList<TextComponent[]> finalComponents = new LinkedList<>();
		LinkedList<TextComponent> workingComponent = new LinkedList<>();
		String spacer = "   ";
		StringBuilder counter = new StringBuilder();

		for (Entry entry : subCategory.getEntryList()) {
			if (!entry.hasPermission(player))
				continue;
			String normalizedName = WordUtils.capitalizeFully(entry.getName().replaceAll("_", " "));
			workingList.put(entry, getFormattedPiece(normalizedName,
					new HoverEvent(HoverEvent.Action.SHOW_TEXT, entry.getHover()),
					new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/swiki " + entry.getParent().getParent().getParent().getName().toLowerCase() + " " + entry.getParent().getParent().getName() + " " + entry.getParent().getName() + " " + entry.getName())));
		}
		for (Map.Entry<Entry, TextComponent> entry : workingList.entrySet()) {
			String normalizedName = WordUtils.capitalizeFully(entry.getKey().getName().replaceAll("_", " "));
			if (counter.append("[").append(normalizedName).append("]").length() <= 60) {
				workingComponent.add(entry.getValue());
				workingComponent.add(new TextComponent(TextComponent.fromLegacyText(spacer)));
				counter.append(spacer);
			} else {
				finalComponents.add(workingComponent.toArray(new TextComponent[finalComponents.size()]));
				workingComponent.clear();
				workingComponent.add(entry.getValue());
				workingComponent.add(new TextComponent(TextComponent.fromLegacyText(spacer)));
				counter = new StringBuilder("").append("[").append(normalizedName).append("]").append(spacer);
			}
		}
		finalComponents.add(workingComponent.toArray(new TextComponent[finalComponents.size()]));

		return finalComponents.toArray(new TextComponent[finalComponents.size()][]);
	}

	private TextComponent getFormattedPiece(String normalizedName, HoverEvent hoverEvent, ClickEvent clickEvent) {
		ComponentBuilder builder = new ComponentBuilder("");
		builder.event(hoverEvent)
				.event(clickEvent)
				.append("[").color(ChatColor.DARK_GREEN)
				.append(normalizedName).color(ChatColor.GRAY)
				.append("]").color(ChatColor.DARK_GREEN);
		return new TextComponent(builder.create());
	}

}
