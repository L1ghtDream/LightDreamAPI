package dev.lightdream.api.databases;

import dev.lightdream.api.IAPI;
import dev.lightdream.api.dto.location.PluginLocation;
import dev.lightdream.api.utils.MessageBuilder;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class ConsoleUser extends User {
    public ConsoleUser() {
        super(null, null, "CONSOLE", "");
    }

    @Override
    public Player getPlayer() {
        return null;
    }

    @SuppressWarnings({"NullableProblems", "ConstantConditions"})
    @Override
    public OfflinePlayer getOfflinePlayer() {
        return null;
    }

    @Override
    public @Nullable PluginLocation getLocation() {
        return null;
    }

    @Override
    public boolean isOnline() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public void setLang(String lang) {
    }

    @Override
    public boolean hasMoney(double amount) {
        return true;
    }

    @Override
    public void addMoney(double amount) {
    }

    @Override
    public void removeMoney(double amount) {
    }

    @Override
    public double getMoney() {
        return 0;
    }

    @Override
    public boolean hasXP(int xp) {
        return true;
    }

    @Override
    public void addXP(int xp) {
    }

    @Override
    public void removeXP(int xp) {
    }

    @Override
    public int getXP() {
        return 0;
    }

    @Override
    public void sendMessage(IAPI api, String msg) {
        Bukkit.getConsoleSender().sendMessage(msg);
    }

    @Override
    public void sendMessage(IAPI api, MessageBuilder msg) {
        Bukkit.getConsoleSender().sendMessage(msg.parseString());
    }

    @Override
    public boolean hasPermission(String permission) {
        return true;
    }


}
