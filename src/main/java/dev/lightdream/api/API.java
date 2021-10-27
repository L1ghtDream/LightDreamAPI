package dev.lightdream.api;

import com.fasterxml.jackson.databind.module.SimpleModule;
import dev.lightdream.api.commands.SubCommand;
import dev.lightdream.api.commands.commands.base.HelpCommand;
import dev.lightdream.api.commands.commands.base.ReloadCommand;
import dev.lightdream.api.commands.commands.base.VersionCommand;
import dev.lightdream.api.commands.commands.ldapi.ChoseLangCommand;
import dev.lightdream.api.commands.commands.ldapi.PluginsCommand;
import dev.lightdream.api.configs.Config;
import dev.lightdream.api.configs.Lang;
import dev.lightdream.api.configs.SQLConfig;
import dev.lightdream.api.databases.ConsoleUser;
import dev.lightdream.api.databases.User;
import dev.lightdream.api.dto.Position;
import dev.lightdream.api.managers.*;
import fr.minuskube.inv.InventoryManager;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

@SuppressWarnings("CanBeFinal")
public final class API implements IAPI {

    //Settings
    public static API instance;
    private final JavaPlugin plugin;
    public SQLConfig sqlConfig;
    public Config config;
    public Lang lang;
    public boolean enabled;

    //Plugins
    public List<LightDreamPlugin> plugins = new ArrayList<>();

    public Economy economy = null;
    public Permission permission = null;

    //Managers
    public LangManager langManager;
    public MessageManager messageManager;
    public DatabaseManager databaseManager;
    public FileManager fileManager;
    public KeyDeserializerManager keyDeserializerManager;
    public CommandManager commandManager;
    public EventManager eventManager;

    public API(JavaPlugin plugin) {
        this.plugin = plugin;
        init();
    }

    public void init() {
        instance = this;
        enabled = true;

        //Events
        new BalanceChangeEventRunnable(this);

        //Placeholders
        new PAPI(this).register();

        //Setups
        economy = setupEconomy();
        permission = setupPermissions();

        //Managers

        keyDeserializerManager = new KeyDeserializerManager(new HashMap<String, Class<?>>() {{
            put("Position", Position.class);
        }});
        fileManager = new FileManager(this, FileManager.PersistType.YAML);
        loadConfigs();

        messageManager = new MessageManager(this, API.class);
        this.databaseManager = new DatabaseManager(this);
        this.databaseManager.setup(User.class);
        this.langManager = new LangManager(API.class, getLangs());
        this.eventManager = new EventManager(this);

        //Commands
        List<SubCommand> baseSubCommands = new ArrayList<>(getBaseCommands());
        commandManager = new CommandManager(this, getProjectID(), baseSubCommands);

        getLogger().info(ChatColor.GREEN + getProjectName() + "(by github.com/L1ghtDream) has been enabled");
    }

    private Economy setupEconomy() {
        RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);
        return rsp.getProvider();
    }

    private Permission setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = plugin.getServer().getServicesManager().getRegistration(Permission.class);
        return rsp.getProvider();
    }

    @SuppressWarnings({"SwitchStatementWithTooFewBranches", "unused"})
    public @NotNull String parsePapi(OfflinePlayer player, String identifier) {
        switch (identifier) {
            case "api_version":
                return getProjectVersion();
        }
        return "";
    }

    public List<SubCommand> getBaseCommands() {
        return Arrays.asList(
                new ChoseLangCommand(this),
                new ReloadCommand(this),
                new VersionCommand(this),
                new PluginsCommand(this),
                new HelpCommand(this)
        );
    }

    public void loadConfigs() {
        sqlConfig = fileManager.load(SQLConfig.class, fileManager.getFile("LightDreamAPI", SQLConfig.class.getSimpleName()));
        config = fileManager.load(Config.class, fileManager.getFile("LightDreamAPI", Config.class.getSimpleName()));
        lang = fileManager.load(Lang.class, fileManager.getFile("LightDreamAPI", config.baseLang));
    }

    @Override
    public void disable() {
        this.databaseManager.save();
        this.enabled = false;
    }

    @Override
    public boolean isLEnabled() {
        return enabled;
    }

    @Override
    public void registerFileManagerModules() {

    }

    @Override
    public CommandManager getBaseCommandManager() {
        return commandManager;
    }

    @Override
    public ConsoleUser getConsoleUser() {
        return new ConsoleUser();
    }

    @Override
    public EventManager getEventManager() {
        return eventManager;
    }

    @Override
    public InventoryManager getInventoryManager() {
        return null;
    }

    @Override
    public List<SimpleModule> getSimpleModules() {
        return new ArrayList<>();
    }

    @Override
    public API getAPI() {
        return this;
    }

    @Override
    public KeyDeserializerManager getKeyDeserializerManager() {
        return keyDeserializerManager;
    }

    public HashMap<String, Object> getLangs() {
        HashMap<String, Object> langs = new HashMap<>();

        config.langs.forEach(lang -> {
            Lang l = fileManager.load(Lang.class, fileManager.getFile(lang));
            langs.put(lang, l);
        });

        return langs;
    }

    @Override
    public JavaPlugin getPlugin() {
        return plugin;
    }

    @Override
    public Economy getEconomy() {
        return economy;
    }

    @Override
    public Lang getLang() {
        return lang;
    }

    @Override
    public Config getSettings() {
        return config;
    }

    @Override
    public SQLConfig getSQLConfig() {
        return sqlConfig;
    }

    @Override
    public MessageManager getMessageManager() {
        return messageManager;
    }

    @Override
    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public File getDataFolder() {
        return new File("plugins/LightDreamAPI");
    }

    @Override
    public Logger getLogger() {
        return getPlugin().getLogger();
    }

    @Override
    public String getProjectName() {
        return "LightDreamAPI";
    }

    @Override
    public String getProjectID() {
        return "ld-api";
    }

    @Override
    public String getProjectVersion() {
        return "3.57";
    }

    @Override
    public void setLang(Player player, String lang) {
        setLang(databaseManager.getUser(player), lang);
    }

    @Override
    public void setLang(User user, String lang) {
        user.setLang(lang);
        user.save();
    }

}
