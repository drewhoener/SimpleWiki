package me.drewhoener.wiki.book;

import me.drewhoener.wiki.pages.generic.BookFormatted;
import me.drewhoener.wiki.util.Util;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BookEntry extends BookFormatted {

	private String rawText = "";
	private LinkedList<TextComponent> lines = new LinkedList<>();

	public BookEntry(String name) {
		this(name, DEFAULT_DESCRIPTION, "");
	}

	public BookEntry(String name, String rawText) {
		this(name, DEFAULT_DESCRIPTION, rawText);
	}

	public BookEntry(String name, String description, String rawText) {
		super(name, description);
		this.setLines(rawText);
	}

	private BookEntry(String name, String description, int sectionID, int parentSection, String rawText, LinkedList<TextComponent> lines) {
		super(name, description, sectionID, parentSection);
		this.rawText = rawText;
		this.setDescription(description);
		this.lines = lines;
	}

	@SuppressWarnings("unchecked")
	public static BookEntry deserialize(Map<String, Object> map) {
		String name = map.getOrDefault("name", DEFAULT_NAME).toString();
		String description = map.getOrDefault("description", DEFAULT_DESCRIPTION).toString();
		int sectionID = ((int) map.getOrDefault("sectionID", -1));
		int parentSection = ((int) map.getOrDefault("parentSection", -1));
		String rawText = map.getOrDefault("rawText", "").toString();
		LinkedList<TextComponent> lines = ((List<String>) map.getOrDefault("lines", new LinkedList<>())).stream()
				.map(Object::toString)
				.map(str -> new TextComponent(ComponentSerializer.parse(str)))
				.collect(Collectors.toCollection(LinkedList::new));

		return new BookEntry(name, description, sectionID, parentSection, rawText, lines);
	}

	@Override
	public LinkedList<TextComponent> getLines() {
		return lines;
	}

	@Override
	public void setLines(String rawText) {
		this.rawText = rawText;
		this.populateComponents();
	}

	@Override
	protected void populateComponents() {
		this.lines.clear();
		this.generateHover();
		String[] newLines = this.rawText.split("\n");
		//TODO add parser here
		this.lines.addAll(BookUtil.parseLines(newLines));
	}

	protected void generateHover() {
		List<TextComponent> components = new LinkedList<>();
		components.add(new TextComponent(new ComponentBuilder(this.getName()).create()));
		components.add(Util.NEW_LINE);
		components.add(new TextComponent(new ComponentBuilder(this.getDescription()).create()));

		this.hoverComponent = components.toArray(new BaseComponent[components.size()]);
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = super.serialize();
		map.put("rawText", this.rawText);
		map.put("lines", this.lines.stream().map(ComponentSerializer::toString).collect(Collectors.toList()));

		return map;
	}
}
