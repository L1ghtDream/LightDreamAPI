package dev.lightdream.api;

import com.fasterxml.jackson.databind.module.SimpleModule;
import dev.lightdream.api.commands.Command;
import dev.lightdream.api.configs.Config;
import dev.lightdream.api.configs.Lang;
import dev.lightdream.api.databases.ConsoleUser;
import dev.lightdream.api.databases.User;
import dev.lightdream.api.managers.DatabaseManager;
import dev.lightdream.api.managers.EventManager;
import dev.lightdream.api.managers.KeyDeserializerManager;
import dev.lightdream.api.managers.MessageManager;
import dev.lightdream.databasemanager.DatabaseMain;
import dev.lightdream.databasemanager.database.IDatabaseManager;
import dev.lightdream.filemanager.FileManagerMain;
import dev.lightdream.logger.LoggableMain;
import fr.minuskube.inv.InventoryManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public interface IAPI extends LoggableMain, DatabaseMain, FileManagerMain {

    JavaPlugin getPlugin();

    Economy getEconomy();

    Lang getLang();

    Config getSettings();

    MessageManager getMessageManager();

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

    Command getBaseCommand();

    ConsoleUser getConsoleUser();

    EventManager getEventManager();

    boolean debug();

    void registerUser(Player player);

    @Override
    DatabaseManager getDatabaseManager();
}
