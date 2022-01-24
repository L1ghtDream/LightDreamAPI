package dev.lightdream.api.dto;

import dev.lightdream.api.databases.User;
import dev.lightdream.api.enums.RewardType;
import dev.lightdream.api.utils.ItemBuilder;
import dev.lightdream.api.utils.MessageBuilder;
import org.bukkit.Bukkit;

import java.util.HashMap;

@SuppressWarnings("unused")
public class Reward {

    public final RewardType type;
    public Item item;
    public String command;
    public int money;

    public Reward(Item item) {
        this.item = item;
        this.type = RewardType.ITEM;
    }

    public Reward(String command) {
        this.command = command;
        this.type = RewardType.COMMAND;
    }

    public Reward(int money) {
        this.money = money;
        this.type = RewardType.MONEY;
    }

    @SuppressWarnings("ConstantConditions")
    public void win(User user) {
        switch (this.type) {
            case ITEM:
                if (!user.isOnline()) {
                    return;
                }
                user.getPlayer().getInventory().addItem(ItemBuilder.makeItem(item));
                break;
            case COMMAND:
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), new MessageBuilder(command).addPlaceholders(new HashMap<String, String>() {{
                    put("player_name", user.name);
                }}).parseString());
                break;
            case MONEY:
                user.addMoney(money);
                break;
        }
    }

    @Override
    public String toString() {
        return "Reward{" + "type=" + type + ", item=" + item + ", command='" + command + '\'' + ", money=" + money + '}';
    }
}
