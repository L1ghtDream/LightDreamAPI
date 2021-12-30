package dev.lightdream.api.dto.gui;

import dev.lightdream.api.dto.Item;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.HashMap;

@AllArgsConstructor
@NoArgsConstructor
public class GUIConfig {

    public String id;
    public String type;
    public String title;
    public int rows;
    public int columns;
    public Item fillItem;
    public HashMap<String, GUIItem> items;
    public boolean update;

    @SuppressWarnings("unused")
    public GUIConfig(String id, String type, String title, int rows, Item fillItem, HashMap<String, GUIItem> items, Boolean update) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.rows = rows;
        this.columns = 9;
        this.fillItem = fillItem;
        this.items = items;
        this.update = update;
    }


}