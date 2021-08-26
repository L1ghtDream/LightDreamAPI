package dev.lightdream.api.files.dto;

import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings({"MethodDoesntCallSuperMethod", "unused"})
@NoArgsConstructor
public class Item {

    public XMaterial material;
    public int amount;
    public String displayName;
    public String headData;
    public String headOwner;
    public List<String> lore;
    public Integer slot;
    public HashMap<String, Object> nbtTags;

    public Item(XMaterial material, int amount) {
        this.material = material;
        this.amount = amount;
        this.lore = new ArrayList<>();
        if (material.parseMaterial() == null) {
            this.displayName = "Item";
        } else {
            this.displayName = material.parseMaterial().name();
        }
        this.nbtTags = new HashMap<>();
    }

    public Item(XMaterial material, int amount, String displayName, List<String> lore, HashMap<String, Object> nbtTags) {
        this.material = material;
        this.amount = amount;
        this.lore = lore;
        this.displayName = displayName;
        this.nbtTags = nbtTags;
    }

    public Item(XMaterial material, int slot, int amount, String displayName, List<String> lore, HashMap<String, Object> nbtTags) {
        this.material = material;
        this.amount = amount;
        this.lore = lore;
        this.displayName = displayName;
        this.slot = slot;
        this.nbtTags = nbtTags;
    }

    public Item(XMaterial material, int slot, String headData, int amount, String displayName, List<String> lore, HashMap<String, Object> nbtTags) {
        this.material = material;
        this.amount = amount;
        this.lore = lore;
        this.displayName = displayName;
        this.slot = slot;
        this.headData = headData;
        this.nbtTags = nbtTags;
    }

    public Item(XMaterial material, int slot, int amount, String displayName, String headOwner, List<String> lore, HashMap<String, Object> nbtTags) {
        this.material = material;
        this.amount = amount;
        this.lore = lore;
        this.displayName = displayName;
        this.headOwner = headOwner;
        this.slot = slot;
        this.nbtTags = nbtTags;
    }

    public Item(XMaterial material, int amount, String displayName, String headOwner, List<String> lore, HashMap<String, Object> nbtTags) {
        this.material = material;
        this.amount = amount;
        this.lore = lore;
        this.displayName = displayName;
        this.headOwner = headOwner;
        this.nbtTags = nbtTags;
    }

    public Item clone() {
        Item item = new Item(this.material, this.amount, this.displayName, this.lore, this.nbtTags);

        if (slot != null) item.slot = this.slot;
        if (headOwner != null) item.headOwner = this.headOwner;
        if (headData != null) item.headData = this.headData;

        return item;
    }

}