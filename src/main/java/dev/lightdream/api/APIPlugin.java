package dev.lightdream.api;

import dev.lightdream.api.configs.SQLConfig;
import dev.lightdream.api.dto.Test;
import dev.lightdream.api.dto.TestBattery;
import dev.lightdream.api.managers.database.HikariDatabaseManagerImpl;
import dev.lightdream.api.utils.Debugger;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.UUID;

@SuppressWarnings("unused")
public final class APIPlugin extends JavaPlugin {

    public API api;

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onEnable() {
        this.api = new API(this);

        if (!Debugger.enabled) {
            return;
        }

        new TestBattery(api, Arrays.asList(
                new Test(null, (test) -> test.submitResults(null)),
                new Test(null, (test) -> {
                    SQLConfig backup = api.sqlConfig;
                    api.sqlConfig = new SQLConfig();
                    api.sqlConfig.database = "tests";
                    HikariDatabaseManagerImpl dbManager = new HikariDatabaseManagerImpl(api);

                    UUID uuid1 = UUID.randomUUID();
                    dbManager.getUser(uuid1);

                    UUID uuid2 = UUID.randomUUID();
                    dbManager.getUser(uuid2);

                    api.sqlConfig = backup;
                    test.setExpectedResult(dbManager.getUser(uuid1));
                    test.submitResults(dbManager.getUser(uuid2));
                })
        )).test();
    }

    @Override
    public void onDisable() {
        api.disable();
    }

}
