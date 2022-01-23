package dev.lightdream.api.databases;

import dev.lightdream.api.API;
import dev.lightdream.api.IAPI;
import dev.lightdream.api.dto.location.PluginLocation;
import dev.lightdream.api.utils.MessageBuilder;
import dev.lightdream.api.utils.Utils;
import dev.lightdream.databasemanager.annotations.database.DatabaseField;
import dev.lightdream.databasemanager.annotations.database.DatabaseTable;
import dev.lightdream.databasemanager.dto.DatabaseEntry;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

@DatabaseTable(table = "users")
public class User extends DatabaseEntry {

    @DatabaseField(columnName = "uuid", unique = true)
    public UUID uuid;
    @DatabaseField(columnName = "name", unique = true)
    public String name;
    @DatabaseField(columnName = "lang")
    public String lang;

    public User() {
        super(null);
    }

    public User(IAPI api, UUID uuid, String name, String lang) {
        super(api);
        this.uuid = uuid;
        this.name = name;
        this.lang = lang;
    }

    public @Nullable Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    @SuppressWarnings("NullableProblems")
    public @NotNull OfflinePlayer getOfflinePlayer() {
        return Bukkit.getOfflinePlayer(uuid);
    }

    @SuppressWarnings("unused")
    public @Nullable PluginLocation getLocation() {
        Player player = getPlayer();
        if (player == null) {
            return null;
        }
        return new PluginLocation(player.getLocation());
    }

    @SuppressWarnings({"unused", "BooleanMethodIsAlwaysInverted"})
    public boolean isOnline() {
        return getOfflinePlayer().isOnline();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @SuppressWarnings("unused")
    public void setLang(String lang) {
        this.lang = lang;
    }

    @SuppressWarnings("unused")
    public boolean hasMoney(double amount) {
        return API.instance.getEconomy().has(getOfflinePlayer(), amount);
    }

    @SuppressWarnings("unused")
    public void addMoney(double amount) {
        API.instance.getEconomy().depositPlayer(getOfflinePlayer(), amount);
    }

    @SuppressWarnings("unused")
    public void removeMoney(double amount) {
        API.instance.getEconomy().withdrawPlayer(getOfflinePlayer(), amount);
    }

    @SuppressWarnings("unused")
    public double getMoney() {
        return API.instance.getEconomy().getBalance(getOfflinePlayer());
    }

    @SuppressWarnings({"unused", "ConstantConditions"})
    public boolean hasXP(int xp) {
        if (!isOnline()) {
            return false;
        }
        return Utils.getTotalExperience(getPlayer()) >= xp;
    }

    @SuppressWarnings("unused")
    public void addXP(int xp) {
        if (!isOnline()) {
            return;
        }
        Utils.setTotalExperience(getPlayer(), getXP() + xp);
    }

    @SuppressWarnings("unused")
    public void removeXP(int xp) {
        if (!isOnline()) {
            return;
        }
        Utils.setTotalExperience(getPlayer(), getXP() - xp);
    }

    @SuppressWarnings({"unused", "ConstantConditions"})
    public int getXP() {
        if (!isOnline()) {
            return 0;
        }
        return Utils.getTotalExperience(getPlayer());
    }

    @SuppressWarnings("unused")
    public void sendMessage(IAPI api, String msg) {
        api.getMessageManager().sendMessage(this, msg);
    }

    @SuppressWarnings("unused")
    public void sendMessage(IAPI api, MessageBuilder msg) {
        api.getMessageManager().sendMessage(this, msg);
    }

    @SuppressWarnings("ConstantConditions")
    public boolean hasPermission(String permission) {
        if (!isOnline()) {
            return false;
        }
        return getPlayer().hasPermission(permission);
    }

    @SuppressWarnings("unused")
    public void teleport(PluginLocation pluginLocation) {
        teleport(pluginLocation.toLocation());
    }

    @SuppressWarnings("ConstantConditions")
    public void teleport(Location location) {
        if (!isOnline()) {
            return;
        }

        getPlayer().teleport(location);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", uuid=" + uuid +
                ", name='" + name + '\'' +
                ", lang='" + lang + '\'' +
                '}';
    }
}
