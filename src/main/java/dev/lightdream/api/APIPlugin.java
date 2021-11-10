package dev.lightdream.api;

import dev.lightdream.api.databases.User;
import dev.lightdream.api.dto.Test;
import dev.lightdream.api.managers.database.HikariDatabaseManager;
import dev.lightdream.api.managers.database.HikariDatabaseManagerImpl;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("unused")
public final class APIPlugin extends JavaPlugin {

    public API api;

    @Override
    public void onEnable() {
        this.api = new API(this);

        test( Arrays.asList(
                new Test((x) -> {
                    return true;
                }),
                new Test((x)->{
                    HikariDatabaseManagerImpl dbManager = new HikariDatabaseManagerImpl(api);
                    UUID uuid =UUID.randomUUID();
                    User user=dbManager.getUser(uuid);
                    System.out.println(dbManager.getUser(uuid));
                    return true;
                })
            ));
    }

    @Override
    public void onDisable() {
        api.disable();
    }

    public void test(List<Test> tests) {
        AtomicInteger testCount = new AtomicInteger();
        tests.forEach(test -> {
            test.test();
            getLogger().info("Test " + testCount + ": " + (test.status ? "Passed" : "Failed"));
            testCount.getAndIncrement();
        });
    }


}
