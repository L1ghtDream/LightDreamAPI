package dev.lightdream.api.utils;

import lombok.Getter;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class NmsBookHelper {

    private static final String version;
    private static final boolean doubleHands;

    private static final Class<?> craftMetaBookClass;
    private static final Field craftMetaBookField;
    private static final Method chatSerializerA;

    private static final Method craftMetaBookInternalAddPageMethod;

    private static final Method craftPlayerGetHandle;
    private static final Method entityPlayerOpenBook;
    private static final Object[] hands;

    private static final Method nmsItemStackSave;
    private static final Constructor<?> nbtTagCompoundConstructor;

    private static final Method craftItemStackAsNMSCopy;

    static {
        version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        final int major, minor;
        Pattern pattern = Pattern.compile("v([0-9]+)_([0-9]+)");
        Matcher m = pattern.matcher(version);
        if (m.find()) {
            major = Integer.parseInt(m.group(1));
            minor = Integer.parseInt(m.group(2));
        } else {
            throw new IllegalStateException("Cannot parse version \"" + version + "\", make sure it follows \"v<major>_<minor>...\"");
        }
        doubleHands = major <= 1 && minor >= 9;
        try {
            craftMetaBookClass = getCraftClass("inventory.CraftMetaBook");

            craftMetaBookField = craftMetaBookClass.getDeclaredField("pages");
            craftMetaBookField.setAccessible(true);

            Method cmbInternalAddMethod = null;
            try {
                //method is protected
                cmbInternalAddMethod = craftMetaBookClass.getDeclaredMethod("internalAddPage", String.class);
                cmbInternalAddMethod.setAccessible(true);
            } catch (NoSuchMethodException e) {
                // Internal data change in 1.16.4
                // To detect if the server is using the new internal format we check if the internalAddPageMethod exists
                // see https://hub.spigotmc.org/stash/projects/SPIGOT/repos/craftbukkit/commits/560b65c4f8a15619aaa4a1737c7040f21e725cce
            }
            craftMetaBookInternalAddPageMethod = cmbInternalAddMethod;

            Class<?> chatSerializer = getNmsClass("IChatBaseComponent$ChatSerializer", "network.chat", false);
            if (chatSerializer == null) {
                chatSerializer = getNmsClass("ChatSerializer", true);
            }

            chatSerializerA = chatSerializer.getDeclaredMethod("a", String.class);

            final Class<?> craftPlayerClass = getCraftClass("entity.CraftPlayer");
            craftPlayerGetHandle = craftPlayerClass.getMethod("getHandle");

            final Class<?> entityPlayerClass = getNmsClass("EntityPlayer", "server.level", true);
            final Class<?> itemStackClass = getNmsClass("ItemStack", "world.item", true);
            if (doubleHands) {
                final Class<?> enumHandClass = getNmsClass("EnumHand", "world", true);

                Method openBookMethod;

                try {
                    openBookMethod = entityPlayerClass.getMethod("a", itemStackClass, enumHandClass);
                } catch (NoSuchMethodException e) {
                    openBookMethod = entityPlayerClass.getMethod("openBook", itemStackClass, enumHandClass);
                }

                entityPlayerOpenBook = openBookMethod;

                hands = enumHandClass.getEnumConstants();
            } else {
                entityPlayerOpenBook = entityPlayerClass.getMethod("openBook", itemStackClass);
                hands = null;
            }

            final Class<?> craftItemStackClass = getCraftClass("inventory.CraftItemStack");
            craftItemStackAsNMSCopy = craftItemStackClass.getMethod("asNMSCopy", ItemStack.class);
            Class<?> nbtTagCompoundClass = getNmsClass("NBTTagCompound", "nbt", true);
            nmsItemStackSave = itemStackClass.getMethod("save", nbtTagCompoundClass);
            nbtTagCompoundConstructor = nbtTagCompoundClass.getConstructor();
        } catch (Exception e) {
            throw new IllegalStateException("Cannot initiate reflections for " + version, e);
        }
    }

    @SuppressWarnings({"unchecked", "ConstantConditions"}) // reflections = unchecked warnings
    public static void setPages(BookMeta meta, BaseComponent[][] components) {
        try {
            List<Object> pages = (List<Object>) craftMetaBookField.get(meta);
            if (pages != null) {
                pages.clear();
            }
            for (BaseComponent[] c : components) {
                if (c == null) {
                    continue;
                }
                final String json = ComponentSerializer.toString(c);
                if (craftMetaBookInternalAddPageMethod != null) {
                    craftMetaBookInternalAddPageMethod.invoke(meta, json);
                } else {
                    pages.add(chatSerializerA.invoke(null, json));
                }
            }
        } catch (Exception e) {
            throw new UnsupportedVersionException(e);
        }
    }

    public static void openBook(Player player, ItemStack book, boolean offHand) {
        try {
            if (doubleHands) {
                entityPlayerOpenBook.invoke(toNms(player), nmsCopy(book), hands[offHand ? 1 : 0]);
            } else {
                entityPlayerOpenBook.invoke(toNms(player), nmsCopy(book));
            }
        } catch (Exception e) {
            throw new UnsupportedVersionException(e);
        }
    }

    public static BaseComponent[] itemToComponents(ItemStack item) {
        return jsonToComponents(itemToJson(item));
    }

    public static BaseComponent[] jsonToComponents(String json) {
        return new BaseComponent[]{new TextComponent(json)};
    }

    private static String itemToJson(ItemStack item) {
        try {
            Object nmsItemStack = nmsCopy(item);
            Object emptyTag = nbtTagCompoundConstructor.newInstance();
            Object json = nmsItemStackSave.invoke(nmsItemStack, emptyTag);
            return json.toString();
        } catch (Exception e) {
            throw new UnsupportedVersionException(e);
        }
    }

    public static Object toNms(Player player) throws InvocationTargetException, IllegalAccessException {
        return craftPlayerGetHandle.invoke(player);
    }

    public static Object nmsCopy(ItemStack item) throws InvocationTargetException, IllegalAccessException {
        return craftItemStackAsNMSCopy.invoke(null, item);
    }

    public static Class<?> getNmsClass(String className, boolean required) {
        return getNms17PlusClass("server." + version + "." + className, required);
    }

    public static Class<?> getNmsClass(String className, String post17middlePackage, boolean required) {
        Class<?> pre = getNmsClass(className, false);
        if (pre != null) {
            return pre;
        }
        return getNms17PlusClass(post17middlePackage + "." + className, required);
    }

    private static Class<?> getNms17PlusClass(String className, boolean required) {
        try {
            return Class.forName("net.minecraft." + className);
        } catch (ClassNotFoundException e) {
            if (required) {
                throw new RuntimeException("Cannot find NMS class " + className, e);
            }
            return null;
        }
    }

    @Deprecated
    public static Class<?> getNmsClass(String className) {
        return getNmsClass(className, false);
    }

    private static Class<?> getCraftClass(String path) {
        try {
            return Class.forName("org.bukkit.craftbukkit." + version + "." + path);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Cannot find CraftBukkit class at path: " + path, e);
        }
    }

    public static class UnsupportedVersionException extends RuntimeException {
        @Getter
        private final String version = NmsBookHelper.version;

        public UnsupportedVersionException(Exception e) {
            super("Error while executing reflections, submit to developers the following log (version: " + NmsBookHelper.version + ")", e);
        }
    }
}