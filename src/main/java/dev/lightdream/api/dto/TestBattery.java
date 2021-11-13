package dev.lightdream.api.dto;

import dev.lightdream.api.IAPI;
import org.bukkit.plugin.java.JavaPlugin;
import org.fusesource.jansi.Ansi;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TestBattery {

    public List<Test> tests;
    public IAPI api;

    public TestBattery(IAPI api, List<Test> tests){
        this.api=api;
        this.tests = tests;
    }

    public void test(){
        AtomicInteger testCount = new AtomicInteger();
        tests.forEach(test -> {
            test.test();
            api.getLogger().info("Test " + testCount + ": " + (test.status ?
                    Ansi.ansi().fg(Ansi.Color.GREEN).boldOff() + "Passed" :
                    Ansi.ansi().fg(Ansi.Color.RED).boldOff() + "Failed"));
            testCount.getAndIncrement();
        });
    }


}
