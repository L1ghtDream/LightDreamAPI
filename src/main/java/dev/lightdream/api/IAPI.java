package dev.lightdream.api;

import com.fasterxml.jackson.databind.module.SimpleModule;
import dev.lightdream.api.commands.Command;
import dev.lightdream.api.configs.Config;
import dev.lightdream.api.configs.Lang;
import dev.lightdream.api.configs.SQLConfig;
import dev.lightdream.api.databases.ConsoleUser;
import dev.lightdream.api.databases.User;
import dev.lightdream.api.managers.EventManager;
import dev.lightdream.api.managers.KeyDeserializerManager;
import dev.lightdream.api.managers.MessageManager;
import dev.lightdream.api.managers.database.IDatabaseManagerImpl;
import fr.minuskube.inv.InventoryManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

public interface IAPI {

    JavaPlugin getPlugin();

    Economy getEconomy();

    Lang getLang();

    Config getSettings();

    SQLConfig getSQLConfig();

    MessageManager getMessageManager();

    IDatabaseManagerImpl getDatabaseManager();

    File getDataFolder();

    Logger getLogger();

    String getProjectName();

    String getProjectID();

    String getProjectVersion();

    void setLang(Player player, String lang);

    void setLang(User user, String lang);

    void loadConfigs();

    InventoryManager getInventoryManager();

    @SuppressWarnings("unused")
    List<SimpleModule> getSimpleModules();

    API getAPI();

    KeyDeserializerManager getKeyDeserializerManager();

    void disable();

    boolean isLEnabled();

    @SuppressWarnings("EmptyMethod")
    void registerFileManagerModules();

    Command getBaseCommandManager();

    ConsoleUser getConsoleUser();

    EventManager getEventManager();

    boolean debug();

    void registerUser(Player player);
}
