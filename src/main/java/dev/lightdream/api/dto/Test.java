package dev.lightdream.api.dto;

import java.util.function.Predicate;

public class Test {

    public boolean status;
    public Predicate<Void> consumer;

    public Test(Predicate<Void> consumer) {
        this.consumer = consumer;
    }

    public void test() {
        status = consumer.test(null);
    }

}
