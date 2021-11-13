package dev.lightdream.api.managers;

import dev.lightdream.api.IAPI;
import dev.lightdream.api.events.BalanceChangeEvent;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.UUID;

public class BalanceChangeEventRunnable {

    public final HashMap<UUID, Double> balance = new HashMap<>();
    private final IAPI api;

    public BalanceChangeEventRunnable(IAPI api) {
        this.api = api;
        if (api.getAPI().apiConfig.useEconomy) {
            registerBalanceChangeEvent();
        }
    }

    public void registerBalanceChangeEvent() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(api.getPlugin(), () -> Bukkit.getOnlinePlayers().forEach(player -> {
            double finalBalance = api.getEconomy().getBalance(player);
            double initialBalance = balance.getOrDefault(player.getUniqueId(), 0.0);

            if (initialBalance != finalBalance) {
                BalanceChangeEvent event = new BalanceChangeEvent(player, initialBalance, finalBalance);
                Bukkit.getPluginManager().callEvent(event);

                balance.put(player.getUniqueId(), event.getFinalBalance());
                if (event.getFinalBalance() > finalBalance) {
                    api.getEconomy().depositPlayer(player, event.getFinalBalance() - finalBalance);
                } else {
                    api.getEconomy().withdrawPlayer(player, finalBalance - event.getFinalBalance());
                }
            }
        }), 1, 1);
    }

}
