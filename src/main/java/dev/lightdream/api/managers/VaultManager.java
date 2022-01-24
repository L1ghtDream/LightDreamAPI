package dev.lightdream.api.managers;

import dev.lightdream.api.API;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.Nullable;

public class VaultManager {

    private final API api;
    public Economy economy = null;
    public Permission permission = null;

    public VaultManager(API api) {
        this.api = api;
        if (api.useEconomy()) {
            economy = setupEconomy();
            new BalanceChangeEventRunnable(api);
        }
        if (api.usePermissions()) {
            permission = setupPermissions();
        }
    }


    private @Nullable Economy setupEconomy() {
        RegisteredServiceProvider<Economy> rsp = api.getPlugin().getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return null;
        }
        return rsp.getProvider();
    }

    private Permission setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = api.getPlugin().getServer().getServicesManager().getRegistration(Permission.class);
        if (rsp == null) {
            return null;
        }
        return rsp.getProvider();
    }

}
