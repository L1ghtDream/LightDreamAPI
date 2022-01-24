package dev.lightdream.api.dto.test;

import java.util.function.Consumer;

public class Test {

    public boolean status;
    public Object result;
    public Object expectedResult;
    public final Consumer<Test> consumer;

    public Test(Object expectedResult, Consumer<Test> consumer) {
        this.consumer = consumer;
        this.expectedResult = expectedResult;
    }

    public void test() {
        consumer.accept(this);
    }

    @SuppressWarnings("unused")
    public void setExpectedResult(Object expectedResult) {
        this.expectedResult = expectedResult;
    }

    @SuppressWarnings("unused")
    public void submitResults(Object result) {
        this.status = result.equals(expectedResult);
        this.result = result;
    }

}
