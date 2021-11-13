package dev.lightdream.api.utils;

import dev.lightdream.api.dto.Item;
import dev.lightdream.api.dto.PluginLocation;
import dev.lightdream.api.dto.Randomizable;
import fr.minuskube.inv.content.SlotPos;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Utils {

    public static @NotNull List<String> color(@NotNull List<String> list) {
        List<String> output = new ArrayList<>();
        list.forEach(line -> output.add(color(line)));
        return output;
    }

    public static @NotNull String color(@NotNull String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }

    @SuppressWarnings("unused")
    public static boolean checkExecute(double chance) {
        if (chance >= 100) {
            return true;
        }
        double result = Math.random() * 101 + 0;
        return result < chance;
    }

    @SuppressWarnings("unused")
    public static int generateRandom(int low, int high) {
        Random r = new Random();
        return r.nextInt(high - low) + low;
    }

    @SuppressWarnings("unused")
    public static double generateRandom(double a, double b) {
        if (b < a) {
            return Math.random() * (a - b + 1) + b;
        }
        return Math.random() * (b - a + 1) + a;
    }

    @SuppressWarnings("unused")
    public static void spawnFireworks(@NotNull Location location, int amount, @NotNull Color color, boolean flicker) {
        Firework fw = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
        FireworkMeta fwm = fw.getFireworkMeta();

        fwm.setPower(2);
        fwm.addEffect(FireworkEffect.builder().withColor(color).flicker(flicker).build());

        fw.setFireworkMeta(fwm);
        fw.detonate();

        for (int i = 0; i < amount; i++) {
            Firework fw2 = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
            fw2.setFireworkMeta(fwm);
        }
    }

    @SuppressWarnings("unused")
    public static boolean compareItemToItemStack(ItemStack itemStack, Item item) {
        if (itemStack == null) {
            return false;
        }
        ItemMeta meta = itemStack.getItemMeta();
        if (!itemStack.getType().equals(item.material.parseMaterial())) {
            return false;
        }
        if (!meta.getDisplayName().equals(item.displayName)) {
            return false;
        }
        if (!meta.getLore().equals(item.lore)) {
            return false;
        }
        for (String attribute : item.nbtTags.keySet()) {
            Object value = item.nbtTags.get(attribute);
            Object itemStackValue = NbtUtils.getNBT(itemStack, attribute);
            if (!value.equals(itemStackValue)) {
                return false;
            }
        }
        return true;
    }

    public static SlotPos getSlotPosition(int slot) {
        slot++;
        int row = slot / 9; //6
        int column = slot % 9;
        if (column == 0) {
            column = 9;
            row--;
        }
        return new SlotPos(row, column - 1);
    }

    public static PluginLocation maxPluginLocation(PluginLocation pos1, PluginLocation pos2) {
        return new PluginLocation(pos1.world, Math.max(pos1.x, pos2.x), Math.max(pos1.y, pos2.y), Math.max(pos1.z, pos2.z));
    }

    public static PluginLocation minPluginLocation(PluginLocation pos1, PluginLocation pos2) {
        return new PluginLocation(pos1.world, Math.min(pos1.x, pos2.x), Math.min(pos1.y, pos2.y), Math.min(pos1.z, pos2.z));
    }

    @SuppressWarnings("unused")
    public static PluginLocation maxPluginLocation(List<PluginLocation> positions) {
        double maxX = -Double.MAX_VALUE;
        double maxY = -Double.MAX_VALUE;
        double maxZ = -Double.MAX_VALUE;

        for (PluginLocation position : positions) {
            maxX = Math.max(maxX, position.x);
            maxY = Math.max(maxY, position.y);
            maxZ = Math.max(maxZ, position.z);
        }

        return new PluginLocation(positions.get(0).world, maxX, maxY, maxZ);
    }

    @SuppressWarnings("unused")
    public static PluginLocation minPluginLocation(List<PluginLocation> positions) {
        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double minZ = Double.MAX_VALUE;

        for (PluginLocation position : positions) {
            minX = Math.min(minX, position.x);
            minY = Math.min(minY, position.y);
            minZ = Math.min(minZ, position.z);
        }

        return new PluginLocation(positions.get(0).world, minX, minY, minZ);
    }

    public static int getTotalExperience(int level) {
        int xp = 0;

        if (level >= 0 && level <= 15) {
            xp = (int) Math.round(Math.pow(level, 2) + 6 * level);
        } else if (level > 15 && level <= 30) {
            xp = (int) Math.round((2.5 * Math.pow(level, 2) - 40.5 * level + 360));
        } else if (level > 30) {
            xp = (int) Math.round(((4.5 * Math.pow(level, 2) - 162.5 * level + 2220)));
        }
        return xp;
    }

    @SuppressWarnings("unused")
    public static int getTotalExperience(Player player) {
        return Math.round(player.getExp() * player.getExpToLevel()) + getTotalExperience(player.getLevel());
    }

    @SuppressWarnings("unused")
    public static void setTotalExperience(Player player, int amount) {
        int level;
        int xp;
        float a = 0;
        float b = 0;
        float c = -amount;

        if (amount > getTotalExperience(0) && amount <= getTotalExperience(15)) {
            a = 1;
            b = 6;
        } else if (amount > getTotalExperience(15) && amount <= getTotalExperience(30)) {
            a = 2.5f;
            b = -40.5f;
            c += 360;
        } else if (amount > getTotalExperience(30)) {
            a = 4.5f;
            b = -162.5f;
            c += 2220;
        }
        level = (int) Math.floor((-b + Math.sqrt(Math.pow(b, 2) - (4 * a * c))) / (2 * a));
        xp = amount - getTotalExperience(level);
        player.setLevel(level);
        player.setExp(0);
        player.giveExp(xp);
    }

    public static int getTotalChances(List<Randomizable> objects) {
        int output = 0;
        for (Randomizable object : objects) {
            output += object.getChance();
        }
        return output;
    }

    @SuppressWarnings({"unchecked", "unused"})
    public static <T> T getRandom(List<T> objects) {
        if (objects.size() == 0) {
            return null;
        }

        if (!(objects.get(0) instanceof Randomizable)) {
            return null;
        }
        int chances = getTotalChances((List<Randomizable>) objects); //200
        int index = -1;
        int rnd = generateRandom(0, chances); //90

        do {
            if (index >= objects.size()) { // 0>=2(false)
                break;
            }
            index++;
            rnd -= ((Randomizable) objects.get(index)).getChance(); // rnd=-10
        } while (rnd > 0);

        if (index >= objects.size()) {
            index = objects.size() - 1;
        }

        return objects.get(index);
    }

    public static double getRam() {
        return ((double) (Runtime.getRuntime().totalMemory() / 1024) / 1024) - ((double) (Runtime.getRuntime().freeMemory() / 1024) / 1024);
    }

    public static double getCpuLoad() {
        try {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            ObjectName name = ObjectName.getInstance("java.lang:type=OperatingSystem");
            AttributeList list = mbs.getAttributes(name, new String[]{"ProcessCpuLoad"});

            if (list.isEmpty())
                return Double.NaN;

            Attribute att = (Attribute) list.get(0);
            Double value = (Double) att.getValue();

            if (value == -1.0)
                return Double.NaN;
            return ((int) (value * 1000) / 10.0);
        } catch (Exception e) {
            return Double.NaN;
        }
    }

    public static String getJava() {
        return String.format("Java Version: %s %s. \n", System.getProperty("java.vendor"), System.getProperty("java.version"));
    }


}
