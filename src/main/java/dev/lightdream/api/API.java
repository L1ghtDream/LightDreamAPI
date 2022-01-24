package dev.lightdream.api;

import com.fasterxml.jackson.databind.module.SimpleModule;
import dev.lightdream.api.configs.ApiConfig;
import dev.lightdream.api.configs.Lang;
import dev.lightdream.api.databases.ConsoleUser;
import dev.lightdream.api.dto.location.Position;
import dev.lightdream.api.managers.*;
import dev.lightdream.databasemanager.dto.SQLConfig;
import dev.lightdream.filemanager.FileManager;
import dev.lightdream.logger.Debugger;
import dev.lightdream.logger.Logger;
import fr.minuskube.inv.InventoryManager;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.entities.MessageActivity;
import net.milkbowl.vault.economy.Economy;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("CanBeFinal")
public final class API implements IAPI {

    //Settings
    public static API instance;
    private final JavaPlugin plugin;
    public Lang lang;
    public boolean enabled;
    //Plugins
    public List<LightDreamPlugin> plugins = new ArrayList<>();
    //Managers
    public FileManager fileManager;
    public KeyDeserializerManager keyDeserializerManager;
    public EventManager eventManager;
    public CommandManager commandManager;
    public ProtocolLibManager protocolLibManager;
    public VaultManager vaultManager;
    private ApiConfig apiConfig;

    public API(JavaPlugin plugin) {
        this.plugin = plugin;
        init();
    }

    public void init() {
        Debugger.init(this);
        Logger.init(this);

        instance = this;
        enabled = true;

        //FileManager pre-setup
        keyDeserializerManager = new KeyDeserializerManager(new HashMap<String, Class<?>>() {{
            put("Position", Position.class);
        }});

        //FileManager
        fileManager = new FileManager(this, FileManager.PersistType.YAML);

        //Load settings
        loadConfigs();

        Logger.setting("API Settings:");
        Logger.setting("");
        Logger.setting("Use Economy (by Vault): " + apiConfig.useEconomy + (apiConfig.useEconomy ? " (if available)" : ""));
        Logger.setting("Use Permissions (by Vault): " + apiConfig.usePermissions + (apiConfig.usePermissions ? " (if available)" : ""));
        Logger.setting("Use PlaceholderAPI: " + apiConfig.usePAPI + (apiConfig.usePAPI ? " (if available)" : ""));
        Logger.setting("Use ProtocolLib: " + apiConfig.useProtocolLib + (apiConfig.useProtocolLib ? " (if available)" : ""));

        if (apiConfig.usePAPI && plugin.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PAPI(this).register();
        }

        if (apiConfig.useProtocolLib && plugin.getServer().getPluginManager().getPlugin("ProtocolLib") != null) {
            this.protocolLibManager = new ProtocolLibManager();
        }

        if (plugin.getServer().getPluginManager().getPlugin("Vault") != null) {
            this.vaultManager = new VaultManager(this);
        } else {
            apiConfig.useEconomy = false;
            apiConfig.usePermissions = false;
        }

        //Managers
        this.eventManager = new EventManager(this);

        //Commands
        commandManager = new CommandManager();

        commandManager.register(this, "api.commands");
        Logger.good(getProjectName() + "(by github.com/L1ghtDream) has been enabled");
    }

    @SuppressWarnings({"SwitchStatementWithTooFewBranches", "unused"})
    public @NotNull String parsePapi(OfflinePlayer player, String identifier) {
        switch (identifier) {
            case "api_version":
                return getProjectVersion();
        }
        return "";
    }

    public void loadConfigs() {
        lang = fileManager.load(Lang.class);
        apiConfig = fileManager.load(ApiConfig.class, fileManager.getFile("LightDreamAPI", ApiConfig.class.getSimpleName()));
    }

    @Override
    public void disable() {
        //this.databaseManager.save();
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

    @Override
    public JavaPlugin getPlugin() {
        return plugin;
    }

    @Override
    public Economy getEconomy() {
        if (vaultManager == null) {
            return null;
        }
        return vaultManager.economy;
    }

    @Override
    public DatabaseManager getDatabaseManager() {
        if (plugins.size() != 0) {
            return plugins.get(0).getDatabaseManager();
        }
        return null;
    }

    @Override
    public boolean useEconomy() {
        return apiConfig.useEconomy;
    }

    @Override
    public boolean usePermissions() {
        return apiConfig.usePermissions;
    }

    @Override
    public boolean usePAPI() {
        return apiConfig.usePAPI;
    }

    @Override
    public boolean useProtocolLib() {
        return apiConfig.useProtocolLib;
    }

    @Override
    public Lang getLang() {
        return lang;
    }

    @Override
    public CommandManager getCommandManager() {
        return commandManager;
    }

    public File getDataFolder() {
        return new File("plugins/LightDreamAPI");
    }

    @Override
    public SQLConfig getSqlConfig() {
        return null;
    }

    @Override
    public String getProjectName() {
        return "LightDreamAPI";
    }

    @Override
    public String getProjectID() {
        return "ld-api";
    }

    @SuppressWarnings("ConstantConditions")
    @SneakyThrows
    @Override
    public String getProjectVersion() {
        MavenXpp3Reader reader = new MavenXpp3Reader();

        if ((new File("pom.xml")).exists()) {
            return reader.read(new FileReader("pom.xml")).getVersion();
        }

        return reader.read(new InputStreamReader(MessageActivity.Application.class.getResourceAsStream(
                "/META-INF/maven/dev.lightdream/LightDreamAPI/pom.xml"))).getVersion();
    }

    @Override
    public boolean debug() {
        if (apiConfig == null) {
            return false;
        }
        return apiConfig.debug;
    }

    @Override
    public void log(String s) {
        System.out.println(s);
    }

    @Override
    public void registerUser(Player player) {
        plugins.forEach(plugin -> plugin.registerUser(player));
    }

}
