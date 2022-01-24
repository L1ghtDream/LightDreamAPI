package dev.lightdream.api.dto.test;

import dev.lightdream.api.IAPI;
import dev.lightdream.logger.Debugger;
import org.fusesource.jansi.Ansi;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("unused")
public class TestBattery {

    public List<Test> tests;
    public final IAPI api;

    public TestBattery(IAPI api, List<Test> tests) {
        this.api = api;
        this.tests = tests;
    }

    public void test() {
        AtomicInteger testCount = new AtomicInteger();
        StringBuilder output = new StringBuilder();
        tests.forEach(test -> {
            Debugger.info("Starting test " + testCount);

            test.test();

            //noinspection StringConcatenationInsideStringBufferAppend
            output.append("\nTest " + testCount + ": " + (test.status ? Ansi.ansi().fg(Ansi.Color.GREEN).boldOff() + "Passed" + Ansi.ansi()
                    .fg(Ansi.Color.DEFAULT)
                    .boldOff() : Ansi.ansi()
                    .fg(Ansi.Color.RED)
                    .boldOff() + "Failed\nResult: " + test.result + "\nExpected result: " + test.expectedResult) + Ansi.ansi()
                    .fg(Ansi.Color.DEFAULT)
                    .boldOff());
            testCount.getAndIncrement();
        });
        Debugger.info(output.toString());
    }


}
