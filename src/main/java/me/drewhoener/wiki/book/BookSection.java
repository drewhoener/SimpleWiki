package me.drewhoener.wiki.book;

import me.drewhoener.wiki.pages.generic.BookFormatted;
import me.drewhoener.wiki.util.Util;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BookSection extends BookFormatted {

	private LinkedList<BookSection> sections = new LinkedList<>();
	private LinkedList<BookEntry> entries = new LinkedList<>();

	private LinkedList<TextComponent> lines = new LinkedList<>();

	public BookSection(String name, String description) {
		super(name, description);
	}

	private BookSection(String name, String description, int sectionID, int parentSection, LinkedList<BookSection> sections, LinkedList<BookEntry> entries, LinkedList<TextComponent> lines) {
		super(name, description, sectionID, parentSection);
		this.sections = sections;
		this.entries = entries;
		this.lines = lines;
	}

	@SuppressWarnings("unchecked")
	public static BookSection deserialize(Map<String, Object> map) {
		String name = map.getOrDefault("name", DEFAULT_NAME).toString();
		String description = map.getOrDefault("description", DEFAULT_DESCRIPTION).toString();
		int sectionID = ((int) map.getOrDefault("sectionID", -1));
		int parentSection = ((int) map.getOrDefault("parentSection", -1));
		LinkedList<BookSection> sections = ((LinkedList) map.getOrDefault("sections", new LinkedList<>()));
		LinkedList<BookEntry> entries = ((LinkedList) map.getOrDefault("entries", new LinkedList<>()));
		LinkedList<TextComponent> lines = ((List<String>) map.getOrDefault("lines", new LinkedList<>())).stream()
				.map(Object::toString)
				.map(str -> new TextComponent(ComponentSerializer.parse(str)))
				.collect(Collectors.toCollection(LinkedList::new));

		return new BookSection(name, description, sectionID, parentSection, sections, entries, lines);
	}

	@Override
	public LinkedList<TextComponent> getLines() {
		return this.lines;
	}

	@Override
	public void setLines(String rawText) {
		//Nothing to see here
		this.populateComponents();
	}

	public void addSection(BookSection section) {
		this.sections.add(section);
		this.sections.sort(Comparator.comparing(BookSection::getName));
	}

	public void addEntry(BookEntry entry) {
		this.entries.add(entry);
		this.entries.sort(Comparator.comparing(BookEntry::getName));
	}

	@Override
	protected void generateHover() {
		List<TextComponent> components = new LinkedList<>();
		components.add(new TextComponent(new ComponentBuilder(this.getName()).create()));
		components.add(Util.NEW_LINE);
		components.add(new TextComponent(new ComponentBuilder(this.getDescription()).create()));

		components.add(Util.NEW_LINE);
		components.add(new TextComponent(new ComponentBuilder("Sections: ").color(ChatColor.GOLD).append("" + this.sections.size()).color(ChatColor.GREEN).create()));
		components.add(new TextComponent(new ComponentBuilder("Entries: ").color(ChatColor.GOLD).append("" + this.entries.size()).color(ChatColor.GREEN).create()));


		this.hoverComponent = components.toArray(new BaseComponent[components.size()]);
	}

	@Override
	protected void populateComponents() {
		this.lines.clear();

		HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, getIDHover());
		this.lines.add(new TextComponent(new ComponentBuilder(BookUtil.alignText(this.getName(), BookUtil.TextAlign.CENTER)).bold(true).event(hoverEvent).create()));
		this.lines.add(new TextComponent(new ComponentBuilder(BookUtil.alignText(this.getDescription(), BookUtil.TextAlign.CENTER)).italic(true).create()));

		TextComponent newEntrySection = new TextComponent();
		BaseComponent[] newSection = new ComponentBuilder("Click to create a new Wiki Section here!").create();
		BaseComponent[] newEntry = new ComponentBuilder("Click to create a new Wiki Entry here!").create();

		newEntrySection.addExtra(new TextComponent(new ComponentBuilder("[Add Section]").event(getCommandEvent("/swiki " + getSectionID() + " newsection")).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, newSection)).create()));
		newEntrySection.addExtra(new TextComponent(BookUtil.spaceBetween("[Add Section]", "[Add Entry]")));
		newEntrySection.addExtra(new TextComponent(new ComponentBuilder("[Add Entry]").event(getCommandEvent("/swiki " + getSectionID() + " newentry")).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, newEntry)).create()));

		this.lines.add(newEntrySection);

		if (this.sections.isEmpty() && this.entries.isEmpty()) {
			this.lines.add(new TextComponent());
			this.lines.add(new TextComponent(new ComponentBuilder("Woah! An empty section!").italic(true).create()));
			return;
		}

		TextComponent backBroadcast = new TextComponent();
		BaseComponent[] backHover = new ComponentBuilder("Click to go back to the previous section!").create();
		BaseComponent[] broadcastHover = new ComponentBuilder("Click to broadcast this section to chat!").create();

		backBroadcast.addExtra(new TextComponent(new ComponentBuilder("[Back]").event(getClickEventFor(this.getParent())).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, backHover)).create()));
		backBroadcast.addExtra(new TextComponent(BookUtil.spaceBetween("[Back]", "[Broadcast]")));
		backBroadcast.addExtra(new TextComponent(new ComponentBuilder("[Broadcast]").event(getBroadcastEvent()).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, broadcastHover)).create()));

		this.lines.add(backBroadcast);

		if (!this.sections.isEmpty()) {
			this.lines.add(new TextComponent(new ComponentBuilder("Sections: ").bold(true).create()));
			this.lines.add(new TextComponent());

			this.sections.forEach(section -> this.lines.add(this.buildDisplayComponent(section)));
		}

		if (!this.entries.isEmpty()) {
			this.lines.add(new TextComponent(new ComponentBuilder("Entries: ").bold(true).create()));
			this.lines.add(new TextComponent());

			this.entries.forEach(entry -> this.lines.add(this.buildDisplayComponent(entry)));
		}
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = super.serialize();
		map.put("sections", this.sections);
		map.put("entries", this.entries);
		map.put("lines", this.lines.stream().map(ComponentSerializer::toString).collect(Collectors.toList()));
		return map;
	}
}
