package dev.lightdream.api;

import com.fasterxml.jackson.databind.module.SimpleModule;
import dev.lightdream.api.configs.JdaConfig;
import dev.lightdream.api.configs.Lang;
import dev.lightdream.api.databases.ConsoleUser;
import dev.lightdream.api.managers.CommandManager;
import dev.lightdream.api.managers.DatabaseManager;
import dev.lightdream.api.managers.EventManager;
import dev.lightdream.api.managers.KeyDeserializerManager;
import dev.lightdream.databasemanager.dto.SQLConfig;
import dev.lightdream.filemanager.FileManager;
import dev.lightdream.logger.Logger;
import fr.minuskube.inv.InventoryManager;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.MessageActivity;
import net.milkbowl.vault.economy.Economy;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
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
    public Lang baseLang;

    //Managers
    public FileManager fileManager;
    public InventoryManager inventoryManager;
    public EventManager eventManager;
    //Bot
    public JDA bot;
    //API
    public API api;
    public DatabaseManager databaseManager;

    @SuppressWarnings("unused")
    @SneakyThrows
    public void init(String projectName, String projectID) {
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
        databaseManager = registerDatabaseManager();
        this.inventoryManager = new InventoryManager(this);
        this.inventoryManager.init();
        this.eventManager = new EventManager(this);

        //Bot
        if (baseJdaConfig != null) {
            if (baseJdaConfig.useJDA) {
                bot = JDABuilder.createDefault(baseJdaConfig.botToken).build();
            }
        }

        //Register
        API.instance.plugins.add(this);
        api.getCommandManager().register(this, getProjectID());
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

    public @NotNull String parsePapi(OfflinePlayer player, String identifier) {
        return "";
    }

    public void loadConfigs() {
        sqlConfig = fileManager.load(SQLConfig.class);
        baseJdaConfig = fileManager.load(JdaConfig.class);
        baseLang = fileManager.load(Lang.class);
    }

    @Override
    public JavaPlugin getPlugin() {
        return this;
    }

    @Override
    public Economy getEconomy() {
        return api.getEconomy();
    }

    @Override
    public SQLConfig getSqlConfig() {
        return sqlConfig;
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

        return reader.read(new InputStreamReader(MessageActivity.Application.class.getResourceAsStream("/META-INF/maven/dev.lightdream/" + projectID + "/pom.xml")))
                .getVersion();
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

    @Override
    public CommandManager getCommandManager() {
        return api.getCommandManager();
    }

    @Override
    public boolean useEconomy() {
        return api.useEconomy();
    }

    @Override
    public boolean usePermissions() {
        return api.usePermissions();
    }

    @Override
    public boolean usePAPI() {
        return api.usePAPI();
    }

    @Override
    public boolean useProtocolLib() {
        return api.useProtocolLib();
    }

    public DatabaseManager registerDatabaseManager() {
        return new DatabaseManager(this);
    }

    @Override
    public Lang getLang() {
        return baseLang;
    }
}
