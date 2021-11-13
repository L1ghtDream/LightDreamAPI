package dev.lightdream.api;

import com.fasterxml.jackson.databind.module.SimpleModule;
import dev.lightdream.api.commands.Command;
import dev.lightdream.api.commands.SubCommand;
import dev.lightdream.api.commands.commands.base.HelpCommand;
import dev.lightdream.api.commands.commands.base.ReloadCommand;
import dev.lightdream.api.commands.commands.base.VersionCommand;
import dev.lightdream.api.configs.Config;
import dev.lightdream.api.configs.JdaConfig;
import dev.lightdream.api.configs.Lang;
import dev.lightdream.api.configs.SQLConfig;
import dev.lightdream.api.databases.ConsoleUser;
import dev.lightdream.api.databases.User;
import dev.lightdream.api.managers.*;
import dev.lightdream.api.managers.database.OmrLiteDatabaseManagerImpl;
import dev.lightdream.api.utils.Logger;
import fr.minuskube.inv.InventoryManager;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("CanBeFinal")
public abstract class LightDreamPlugin extends JavaPlugin implements IAPI {

    //Settings
    public String projectName = "Undefined";
    public String projectID = "Undefined";
    public String projectVersion = "Undefined";
    public boolean enabled;

    //Config
    public SQLConfig sqlConfig;
    public JdaConfig baseJdaConfig;
    public Config baseConfig;
    public Lang baseLang;

    //Managers
    public Economy economy;
    public Permission permission;
    public FileManager fileManager;
    public InventoryManager inventoryManager;
    public MessageManager messageManager;
    public Command baseCommand;
    public EventManager eventManager;
    public ProtocolLibManager protocolLibManager;
    //Bot
    public JDA bot;
    //API
    public API api;
    //Commands
    private OmrLiteDatabaseManagerImpl databaseManager;

    @SuppressWarnings("unused")
    @SneakyThrows
    public void init(String projectName, String projectID, String version) {
        if (API.instance == null) {
            api = new API(this);
        } else {
            api = API.instance;
        }

        this.projectName = projectName;
        this.projectID = projectID;
        this.projectVersion = version;
        enabled = true;

        //Files
        fileManager = new FileManager(this, FileManager.PersistType.YAML);
        registerFileManagerModules();
        loadConfigs();

        //Managers
        registerLangManager();
        this.economy = api.economy;
        this.permission = api.permission;
        this.databaseManager = new OmrLiteDatabaseManagerImpl(this);
        this.inventoryManager = new InventoryManager(this);
        this.inventoryManager.init();
        this.messageManager = instantiateMessageManager();
        this.eventManager = new EventManager(this);
        this.protocolLibManager = new ProtocolLibManager();

        //Commands
        List<SubCommand> baseSubCommands = new ArrayList<>();
        baseSubCommands.add(new ReloadCommand(this));
        baseSubCommands.add(new VersionCommand(this));
        baseSubCommands.add(new HelpCommand(this));
        baseSubCommands.addAll(getBaseSubCommands());
        baseCommand = new Command(this, projectID, baseSubCommands);

        //Bot
        if (baseJdaConfig != null) {
            if (baseJdaConfig.useJDA) {
                bot = JDABuilder.createDefault(baseJdaConfig.botToken).build();
            }
        }


        //Register
        API.instance.plugins.add(this);
        Logger.info(projectName + "(by github.com/L1ghtDream) has been enabled");
    }

    @Override
    public void onDisable() {
        if (api.isLEnabled()) {
            api.disable();
        }
        if (this.isLEnabled()) {
            this.disable();
        }
    }

    public abstract @NotNull String parsePapi(OfflinePlayer player, String identifier);

    public void loadConfigs() {
        sqlConfig = fileManager.load(SQLConfig.class);
        baseConfig = fileManager.load(Config.class);
        baseJdaConfig = fileManager.load(JdaConfig.class);
        baseLang = fileManager.load(Lang.class, fileManager.getFile(baseConfig.baseLang));
    }

    public abstract List<SubCommand> getBaseSubCommands();

    public abstract MessageManager instantiateMessageManager();

    public abstract void registerLangManager();

    @SuppressWarnings("unused")
    public HashMap<String, Object> getLangs() {
        HashMap<String, Object> langs = new HashMap<>();

        baseConfig.langs.forEach(lang -> {
            Lang l = fileManager.load(Lang.class, fileManager.getFile(lang));
            langs.put(lang, l);
        });

        return langs;
    }

    @Override
    public JavaPlugin getPlugin() {
        return this;
    }

    @Override
    public Economy getEconomy() {
        return economy;
    }

    @Override
    public Lang getLang() {
        return baseLang;
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
    public Config getSettings() {
        return baseConfig;
    }

    @Override
    public String getProjectName() {
        return projectName;
    }

    @Override
    public String getProjectID() {
        return projectID;
    }

    @Override
    public String getProjectVersion() {
        return projectVersion;
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

    @Override
    public ConsoleUser getConsoleUser() {
        return new ConsoleUser();
    }

    @Override
    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }

    @Override
    public List<SimpleModule> getSimpleModules() {
        return new ArrayList<>();
    }

    @Override
    public API getAPI() {
        return api;
    }

    @Override
    public KeyDeserializerManager getKeyDeserializerManager() {
        return api.getKeyDeserializerManager();
    }

    @Override
    public boolean isLEnabled() {
        return enabled;
    }

    @Override
    public Command getBaseCommand() {
        return baseCommand;
    }

    @Override
    public EventManager getEventManager() {
        return eventManager;
    }

    @Override
    public boolean debug() {
        return baseConfig.debug;
    }
}
