package me.drewhoener.wiki.pages.generic;

import me.drewhoener.wiki.util.Util;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import net.minecraft.server.v1_12_R1.IChatBaseComponent;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public abstract class BookFormatted implements IBookGUI, ConfigurationSerializable {

	public static int NEXT_ID = 0;
	protected static String DEFAULT_NAME = "MissingNo";
	protected static String DEFAULT_DESCRIPTION = "No Description Provided!";
	protected BaseComponent[] hoverComponent = new BaseComponent[0];
	private String name;
	private String description;
	private int sectionID;
	private int parentSection;
	private LinkedList<IChatBaseComponent> pages = new LinkedList<>();

	public BookFormatted(String name, String description) {
		this(name, description, NEXT_ID++, -1);
	}

	public BookFormatted(String name, String description, int parent) {
		this(name, description, NEXT_ID++, parent);
	}

	public BookFormatted(String name, String description, int sectionID, int parentSection) {
		this.name = name;
		this.description = description;
		this.sectionID = sectionID;
		this.parentSection = parentSection;
		this.populateComponents();
		toPages(true);
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getDescription() {
		return this.description;
	}

	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public abstract LinkedList<TextComponent> getLines();

	protected abstract void populateComponents();

	public int getSectionID() {
		return sectionID;
	}

	public int getParent() {
		return this.parentSection;
	}

	public void setParent(int parent) {
		this.parentSection = parent;
	}

	public List<IChatBaseComponent> toPages() {
		return toPages(false);
	}

	protected void generateHover() {

	}

	public BaseComponent[] getHoverDescription() {
		if (hoverComponent == null || hoverComponent.length == 0)
			this.generateHover();
		return this.hoverComponent;
	}

	protected LinkedList<IChatBaseComponent> toPages(boolean force) {
		List<TextComponent> lines = getLines();
		if (force || (this.pages.isEmpty() && !lines.isEmpty())) {

			this.pages.clear();

			TextComponent curPage = new TextComponent();
			int pageLength = 0;
			for (TextComponent component : lines) {

				String text = BaseComponent.toPlainText(component);

				if (text.length() + pageLength > 256) {
					this.pages.add(IChatBaseComponent.ChatSerializer.a(ComponentSerializer.toString(curPage)));
					curPage = new TextComponent();
					pageLength = 0;
					curPage.addExtra(component);
					continue;
				}

				curPage.addExtra(Util.NEW_LINE);
				curPage.addExtra(component);
				pageLength += text.length();
			}
			this.pages.add(IChatBaseComponent.ChatSerializer.a(ComponentSerializer.toString(curPage)));

		}

		return this.pages;
	}

	protected BaseComponent[] getIDHover() {
		List<BaseComponent> components = new ArrayList<>();
		TextComponent idBuilder = new TextComponent(new ComponentBuilder("ComponentID: ").color(ChatColor.GOLD).append("" + this.sectionID).color(ChatColor.GREEN).create());
		TextComponent parentBuilder = new TextComponent(new ComponentBuilder("ParentID: ").color(ChatColor.GOLD).append("" + this.parentSection).color(ChatColor.GREEN).create());

		components.add(idBuilder);
		components.add(Util.NEW_LINE);
		components.add(parentBuilder);

		return components.toArray(new BaseComponent[components.size()]);
	}

	protected ClickEvent getClickEventFor(int id) {
		return new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/swiki " + id);
	}

	protected ClickEvent getCommandEvent(String command) {
		return new ClickEvent(ClickEvent.Action.RUN_COMMAND, command);
	}

	protected ClickEvent getBroadcastEvent() {
		return new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/swiki " + sectionID + " broadcast");
	}

	protected TextComponent buildDisplayComponent(BookFormatted bookFormatted) {
		ComponentBuilder builder = new ComponentBuilder(bookFormatted.getName()).bold(true).append(" - ").bold(false).append(bookFormatted.getDescription()).italic(true);
		TextComponent component = new TextComponent(builder.create());
		component.setClickEvent(getClickEventFor(bookFormatted.getSectionID()));
		component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, bookFormatted.getHoverDescription()));
		return component;
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new TreeMap<>();
		map.put("name", this.name);
		map.put("description", this.description);
		map.put("sectionID", this.sectionID);
		map.put("parentSection", this.parentSection);

		return map;
	}
}
