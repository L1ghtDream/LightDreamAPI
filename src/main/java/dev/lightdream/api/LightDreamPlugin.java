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
import dev.lightdream.api.databases.ConsoleUser;
import dev.lightdream.api.databases.User;
import dev.lightdream.api.managers.*;
import dev.lightdream.databasehandler.dto.SQLConfig;
import dev.lightdream.logger.Logger;
import fr.minuskube.inv.InventoryManager;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.MessageActivity;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
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
public abstract class LightDreamPlugin extends JavaPlugin implements IAPI {

    //Settings
    public String projectName = "Undefined";
    public String projectID = "Undefined";
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
    public DatabaseManager databaseManager;

    @SuppressWarnings("unused")
    @SneakyThrows
    public void init(String projectName, String projectID, String baseCommand) {
        if (API.instance == null) {
            api = new API(this);
        } else {
            api = API.instance;
        }

        this.projectName = projectName;
        this.projectID = projectID;
        enabled = true;

        //Files
        fileManager = new FileManager(this, FileManager.PersistType.YAML);
        registerFileManagerModules();
        loadConfigs();

        //Managers
        registerLangManager();
        this.economy = api.economy;
        this.permission = api.permission;
        this.databaseManager = new DatabaseManager(this);
        this.inventoryManager = new InventoryManager(this);
        this.inventoryManager.init();
        this.messageManager = instantiateMessageManager();
        this.eventManager = new EventManager(this);
        this.protocolLibManager = new ProtocolLibManager();

        //Commands
        List<SubCommand> baseSubCommands = new ArrayList<>();
        baseSubCommands.add(new ReloadCommand(this, baseCommand));
        baseSubCommands.add(new VersionCommand(this, baseCommand));
        baseSubCommands.add(new HelpCommand(this, baseCommand));
        baseSubCommands.addAll(getBaseSubCommands());
        this.baseCommand = new Command(this, baseCommand, baseSubCommands);

        //Bot
        if (baseJdaConfig != null) {
            if (baseJdaConfig.useJDA) {
                bot = JDABuilder.createDefault(baseJdaConfig.botToken).build();
            }
        }

        //Register
        API.instance.plugins.add(this);
        Logger.good(projectName + "(by github.com/L1ghtDream) has been enabled");
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
    public SQLConfig getSqlConfig() {
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

    @SuppressWarnings("ConstantConditions")
    @SneakyThrows
    @Override
    public String getProjectVersion() {
        MavenXpp3Reader reader = new MavenXpp3Reader();

        if ((new File("pom.xml")).exists()) {
            return reader.read(new FileReader("pom.xml")).getVersion();
        }

        return reader.read(new InputStreamReader(MessageActivity.Application.class.getResourceAsStream("/META-INF/maven/dev.lightdream/" + projectID + "/pom.xml"))).getVersion();
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
        return getAPI().debug();
    }

    @Override
    public void log(String s) {
        System.out.println(s);
    }


}
