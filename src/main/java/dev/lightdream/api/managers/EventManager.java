package dev.lightdream.api.managers;

import dev.lightdream.api.IAPI;
import dev.lightdream.api.gui.GUI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.List;

public class EventManager implements Listener {

    private final IAPI api;
    public List<GUI> registeredGUIs = new ArrayList<>();

    public EventManager(IAPI api) {
        api.getAPI().getPlugin().getServer().getPluginManager().registerEvents(this, api.getAPI().getPlugin());
        this.api = api;
    }

    @EventHandler
    public void onPlayerInventoryClick(InventoryClickEvent event) {
        registeredGUIs.forEach(gui -> gui.c(event));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        api.registerUser(event.getPlayer());
    }

}
