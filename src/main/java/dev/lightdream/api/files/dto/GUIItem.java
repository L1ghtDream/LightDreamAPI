package dev.lightdream.api.files.dto;

import com.google.gson.JsonElement;
import dev.lightdream.api.utils.JsonUtils;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
public class GUIItem {

    public Item item;
    public String args;

    public List<String> getFunctions() {
        return new JsonUtils(this.args).getStringList("functions");
    }

    public JsonElement getFunctionArgs(String function) {
        return new JsonUtils(this.args).getJsonElement(function);
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public GUIItem clone() {
        String args = this.args;
        return new GUIItem(item, args);
    }

    public GUIItem deepClone() {
        String args = this.args;
        return new GUIItem(item.clone(), args);
    }

    @Override
    public String toString() {
        return "GUIItem{" +
                "item=" + item +
                ", args='" + args + '\'' +
                '}';
    }
}
