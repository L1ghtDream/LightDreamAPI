package dev.lightdream.api.dto.gui;

import dev.lightdream.api.dto.Item;
import dev.lightdream.api.dto.Serializable;
import dev.lightdream.api.utils.MessageBuilder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiConsumer;

@NoArgsConstructor
public class GUIItem {

    public Item item;
    public GUIItemArgs args;
    public boolean repeated;
    public List<Integer> nextSlots;

    @SuppressWarnings("unused")
    public GUIItem(Item item, GUIItemArgs args) {
        this.item = item;
        this.args = args;
        this.repeated = false;
        this.nextSlots = new ArrayList<>();
    }

    @SuppressWarnings("unused")
    public GUIItem(Item item, HashMap<String, Object> args) {
        this.item = item;
        this.args = new GUIItemArgs(args);
        this.repeated = false;
        this.nextSlots = new ArrayList<>();
    }

    @SuppressWarnings("unused")
    public GUIItem(Item item) {
        this.item = item;
        this.args = new GUIItemArgs();
        this.repeated = false;
        this.nextSlots = new ArrayList<>();
    }

    @SuppressWarnings("unused")
    public GUIItem(Item item, GUIItemArgs args, List<Integer> nextSlots) {
        this.item = item;
        this.args = args;
        this.repeated = true;
        this.nextSlots = nextSlots;
    }

    @SuppressWarnings("unused")
    public GUIItem(Item item, List<Integer> nextSlots) {
        this.item = item;
        this.repeated = true;
        this.nextSlots = nextSlots;
    }

    private GUIItem(Item item, GUIItemArgs args, boolean repeated, List<Integer> nextSlots) {
        this.item = item;
        this.args = args;
        this.repeated = repeated;
        this.nextSlots = nextSlots;
    }

    public List<String> functions() {
        return args.args();
    }

    public List<String> functionArgs(String function) {
        return args.functionArgs(function);
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public GUIItem clone() {
        return new GUIItem(item.clone(), args.clone(), repeated, nextSlots);
    }

    @Override
    public String toString() {
        return "GUIItem{" + "item=" + item + ", args=" + args + ", repeatedItem=" + repeated + '}';
    }

    @NoArgsConstructor
    public static class GUIItemArg extends Serializable implements java.io.Serializable {
        public String function;
        public List<String> args;

        public GUIItemArg(String function, String arg) {
            this.function = function;
            this.args = new ArrayList<>();
            this.args.add(arg);
        }

        public GUIItemArg(String function, List<String> args) {
            this.function = function;
            this.args = args;
        }

        @SuppressWarnings("MethodDoesntCallSuperMethod")
        public GUIItemArg clone() {
            return new GUIItemArg(function, new ArrayList<>(args));
        }

        @Override
        public String toString() {
            return "GUIItemArg{" + "function='" + function + '\'' + ", args=" + args + '}';
        }
    }

    @NoArgsConstructor
    public static class GUIItemArgs extends Serializable implements java.io.Serializable {
        @Getter
        private List<GUIItemArg> args = new ArrayList<>();

        public GUIItemArgs(List<GUIItemArg> args) {
            this.args = args;
        }

        @SuppressWarnings({"unused", "unchecked"})
        public GUIItemArgs(HashMap<String, Object> functions) {
            functions.forEach((function, args) -> {
                if (args instanceof String) {
                    this.args.add(new GUIItemArg(function, (String) args));
                } else {
                    this.args.add(new GUIItemArg(function, (List<String>) args));
                }
            });
        }

        public List<String> args() {
            List<String> functions = new ArrayList<>();
            for (GUIItemArg arg : this.args) {
                functions.add(arg.function);
            }
            return functions;
        }

        public List<String> functionArgs(String function) {
            for (GUIItemArg guiItemArg : this.args) {
                if (guiItemArg.function.equals(function)) {
                    return guiItemArg.args;
                }
            }
            return null;
        }

        @SuppressWarnings({"MethodDoesntCallSuperMethod"})
        public GUIItemArgs clone() {
            List<GUIItemArg> args = new ArrayList<>();

            this.args.forEach(arg -> args.add(arg.clone()));

            return new GUIItemArgs(args);
        }

        public GUIItemArgs parse(BiConsumer<MessageBuilder, MessageBuilder> parser) {
            List<GUIItemArg> args = new ArrayList<>();
            this.args.forEach(arg -> parser.andThen((k, v) -> args.add(new GUIItemArg(k.getBaseString(), v.getBaseList())))
                    .accept(new MessageBuilder(arg.function), new MessageBuilder(arg.args)));
            return new GUIItemArgs(args);
        }

        @Override
        public String toString() {
            return "GUIItemArgs{" + "args=" + args + '}';
        }
    }
}
