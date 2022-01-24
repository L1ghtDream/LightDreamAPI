package dev.lightdream.api.utils;

import dev.lightdream.api.events.CustomBookOpenEvent;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.*;

public final class BookUtils {
    private static final boolean canTranslateDirectly;

    static {
        boolean success = true;
        try {
            ChatColor.BLACK.asBungee();
        } catch (NoSuchMethodError e) {
            success = false;
        }
        canTranslateDirectly = success;
    }

    @SuppressWarnings("unused")
    public static void openPlayer(Player p, ItemStack book) {
        CustomBookOpenEvent event = new CustomBookOpenEvent(p, book, false);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return;
        p.closeInventory();
        ItemStack hand = p.getItemInHand();

        p.setItemInHand(event.getBook());
        p.updateInventory();

        NmsBookHelper.openBook(p, event.getBook(), event.getHand() == CustomBookOpenEvent.Hand.OFF_HAND);

        p.setItemInHand(hand);
        p.updateInventory();
    }

    @SuppressWarnings("unused")
    public static BookBuilder writtenBook() {
        return new BookBuilder(new ItemStack(Material.WRITTEN_BOOK));
    }

    public interface ClickAction {
        @SuppressWarnings("unused")
        static ClickAction runCommand(String command) {
            return new SimpleClickAction(ClickEvent.Action.RUN_COMMAND, command);
        }

        @Deprecated
        static ClickAction suggestCommand(String command) {
            return new SimpleClickAction(ClickEvent.Action.SUGGEST_COMMAND, command);
        }

        @SuppressWarnings({"HttpUrlsUsage", "unused"})
        static ClickAction openUrl(String url) {
            if (url.startsWith("http://") || url.startsWith("https://")) return new SimpleClickAction(ClickEvent.Action.OPEN_URL, url);
            else throw new IllegalArgumentException("Invalid url: \"" + url + "\", it should start with http:// or https://");
        }

        @SuppressWarnings("unused")
        static ClickAction changePage(int page) {
            return new SimpleClickAction(ClickEvent.Action.CHANGE_PAGE, Integer.toString(page));
        }

        ClickEvent.Action action();

        String value();

        @Getter
        @Accessors(fluent = true)
        @RequiredArgsConstructor
        class SimpleClickAction implements ClickAction {
            private final ClickEvent.Action action;
            private final String value;
        }
    }

    public interface HoverAction {
        @SuppressWarnings("unused")
        static HoverAction showText(BaseComponent... text) {
            return new SimpleHoverAction(HoverEvent.Action.SHOW_TEXT, text);
        }

        @SuppressWarnings("unused")
        static HoverAction showText(String text) {
            return new SimpleHoverAction(HoverEvent.Action.SHOW_TEXT, new TextComponent(text));
        }

        @SuppressWarnings("unused")
        static HoverAction showItem(BaseComponent... item) {
            return new SimpleHoverAction(HoverEvent.Action.SHOW_ITEM, item);
        }

        @SuppressWarnings("unused")
        static HoverAction showItem(ItemStack item) {
            return new SimpleHoverAction(HoverEvent.Action.SHOW_ITEM, NmsBookHelper.itemToComponents(item));
        }

        @SuppressWarnings("unused")
        static HoverAction showEntity(BaseComponent... entity) {
            return new SimpleHoverAction(HoverEvent.Action.SHOW_ENTITY, entity);
        }

        static HoverAction showEntity(UUID uuid, String type, String name) {
            return new SimpleHoverAction(HoverEvent.Action.SHOW_ENTITY,
                    NmsBookHelper.jsonToComponents("{id:\"" + uuid + "\",type:\"" + type + "\"name:\"" + name + "\"}"));
        }

        @SuppressWarnings({"unused", "deprecation"})
        static HoverAction showEntity(Entity entity) {
            return showEntity(entity.getUniqueId(), entity.getType().getName(), entity.getName());
        }

        @SuppressWarnings("unused")
        static HoverAction showAchievement(String achievementId) {
            return new SimpleHoverAction(HoverEvent.Action.SHOW_ACHIEVEMENT, new TextComponent("achievement." + achievementId));
        }

        @SuppressWarnings("unused")
        static HoverAction showStatistic(String statisticId) {
            return new SimpleHoverAction(HoverEvent.Action.SHOW_ACHIEVEMENT, new TextComponent("statistic." + statisticId));
        }

        HoverEvent.Action action();

        BaseComponent[] value();

        @Getter
        @Accessors(fluent = true)
        class SimpleHoverAction implements HoverAction {
            private final HoverEvent.Action action;
            private final BaseComponent[] value;

            public SimpleHoverAction(HoverEvent.Action action, BaseComponent... value) {
                this.action = action;
                this.value = value;
            }
        }
    }

    public static class BookBuilder {
        private final BookMeta meta;
        private final ItemStack book;

        public BookBuilder(ItemStack book) {
            this.book = book;
            this.meta = (BookMeta) book.getItemMeta();
        }

        @SuppressWarnings("unused")
        public BookBuilder title(String title) {
            if (title.length() > 32) {
                throw new IllegalArgumentException("The book title must be at most 32 characters");
            }
            meta.setTitle(title);
            return this;
        }

        @SuppressWarnings("unused")
        public BookBuilder author(String author) {
            meta.setAuthor(author);
            return this;
        }

        @SuppressWarnings("unused")
        public BookBuilder pagesRaw(String... pages) {
            meta.setPages(pages);
            return this;
        }

        @SuppressWarnings("unused")
        public BookBuilder pagesRaw(List<String> pages) {
            meta.setPages(pages);
            return this;
        }

        @SuppressWarnings("UnusedReturnValue")
        public BookBuilder pages(BaseComponent[]... pages) {
            NmsBookHelper.setPages(meta, pages);
            return this;
        }

        @SuppressWarnings("unused")
        public BookBuilder pages(List<BaseComponent[]> pages) {
            NmsBookHelper.setPages(meta, pages.toArray(new BaseComponent[0][]));
            return this;
        }

        @SuppressWarnings("unused")
        public ItemStack build() {
            if (!meta.hasAuthor()) {
                meta.setAuthor("");
            }
            if (!meta.hasTitle()) {
                meta.setTitle("");
            }
            if (!meta.hasPages()) {
                this.pages(new BaseComponent[]{});
            }
            book.setItemMeta(meta);
            return book;
        }
    }

    @SuppressWarnings("unused")
    public static class PageBuilder {
        private final List<BaseComponent> text = new ArrayList<>();

        public static PageBuilder of(String text) {
            return new PageBuilder().add(text);
        }

        public static PageBuilder of(BaseComponent text) {
            return new PageBuilder().add(text);
        }

        public static PageBuilder of(BaseComponent... text) {
            PageBuilder res = new PageBuilder();
            for (BaseComponent b : text)
                res.add(b);
            return res;
        }

        public PageBuilder add(String text) {
            this.text.add(TextBuilder.of(text).build());
            return this;
        }

        public PageBuilder add(BaseComponent component) {
            this.text.add(component);
            return this;
        }

        public PageBuilder add(BaseComponent... components) {
            this.text.addAll(Arrays.asList(components));
            return this;
        }

        public PageBuilder add(Collection<BaseComponent> components) {
            this.text.addAll(components);
            return this;
        }

        public PageBuilder newLine() {
            this.text.add(new TextComponent("\n"));
            return this;
        }

        public BaseComponent[] build() {
            return text.toArray(new BaseComponent[0]);
        }
    }

    @Setter
    @Getter
    @Accessors(fluent = true,
            chain = true)
    public static class TextBuilder {
        private String text = "";
        private ClickAction onClick = null;
        private HoverAction onHover = null;
        private ChatColor color = ChatColor.BLACK;

        @Setter(AccessLevel.NONE)
        private ChatColor[] style;

        public static TextBuilder of(String text) {
            return new TextBuilder().text(text);
        }

        @SuppressWarnings("unused")
        public TextBuilder color(ChatColor color) {
            if (color != null && !color.isColor()) throw new IllegalArgumentException("Argument isn't a color!");
            this.color = color;
            return this;
        }

        @SuppressWarnings("unused")
        public TextBuilder style(ChatColor... style) {
            for (ChatColor c : style)
                if (!c.isFormat()) throw new IllegalArgumentException("Argument isn't a style!");
            this.style = style;
            return this;
        }

        public BaseComponent build() {
            TextComponent res = new TextComponent(text);
            if (onClick != null) res.setClickEvent(new ClickEvent(onClick.action(), onClick.value()));
            if (onHover != null) res.setHoverEvent(new HoverEvent(onHover.action(), onHover.value()));
            if (color != null) {
                if (canTranslateDirectly) res.setColor(color.asBungee());
                else res.setColor(net.md_5.bungee.api.ChatColor.getByChar(color.getChar()));
            }
            if (style != null) {
                for (ChatColor c : style) {
                    switch (c) {
                        case MAGIC:
                            res.setObfuscated(true);
                            break;
                        case BOLD:
                            res.setBold(true);
                            break;
                        case STRIKETHROUGH:
                            res.setStrikethrough(true);
                            break;
                        case UNDERLINE:
                            res.setUnderlined(true);
                            break;
                        case ITALIC:
                            res.setItalic(true);
                            break;
                    }
                }
            }
            return res;
        }
    }

}
