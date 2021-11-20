package dev.lightdream.api.dto;

import dev.lightdream.api.utils.ItemBuilder;
import lombok.NoArgsConstructor;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@SuppressWarnings({"MethodDoesntCallSuperMethod", "unused"})
@NoArgsConstructor
public class Item {

    public XMaterial material;
    public Integer amount;
    public String displayName;
    public String headData;
    public String headOwner;
    public List<String> lore;
    public Integer slot;
    public HashMap<String, Object> nbtTags;

    public Item(ItemStack item) {
        if (item == null) {
            return;
        }
        this.material = XMaterial.matchXMaterial(item);
        this.amount = item.getAmount();
        if (item.hasItemMeta()) {
            this.displayName = item.getItemMeta().getDisplayName();
            this.lore = item.getItemMeta().getLore();
        } else {
            this.displayName = this.material.name();
            this.lore = new ArrayList<>();
        }
        this.nbtTags = new HashMap<>();
    }

    public Item(XMaterial material) {
        this.material = material;
        this.amount = 1;
        this.lore = new ArrayList<>();
        if (material.parseMaterial() == null) {
            this.displayName = "Item";
        } else {
            this.displayName = material.parseMaterial().name();
        }
        this.nbtTags = new HashMap<>();
    }

    public Item(XMaterial material, Integer amount) {
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

    public Item(XMaterial material, String displayName) {
        this.material = material;
        this.amount = 1;
        this.lore = new ArrayList<>();
        this.displayName = displayName;
        this.nbtTags = new HashMap<>();
    }

    public Item(XMaterial material, Integer amount, String displayName, List<String> lore, HashMap<String, Object> nbtTags) {
        this.material = material;
        this.amount = amount;
        this.lore = lore;
        this.displayName = displayName;
        this.nbtTags = nbtTags;
    }

    public Item(XMaterial material, Integer amount, String displayName, List<String> lore) {
        this.material = material;
        this.amount = amount;
        this.lore = lore;
        this.displayName = displayName;
        this.nbtTags = new HashMap<>();
    }

    public Item(XMaterial material, int slot, Integer amount, String displayName, List<String> lore, HashMap<String, Object> nbtTags) {
        this.material = material;
        this.amount = amount;
        this.lore = lore;
        this.displayName = displayName;
        this.slot = slot;
        this.nbtTags = nbtTags;
    }

    public Item(XMaterial material, int slot, Integer amount, String displayName, List<String> lore) {
        this.material = material;
        this.amount = amount;
        this.lore = lore;
        this.displayName = displayName;
        this.slot = slot;
        this.nbtTags = new HashMap<>();
    }

    public Item(XMaterial material, int slot, String headData, Integer amount, String displayName, List<String> lore, HashMap<String, Object> nbtTags) {
        this.material = material;
        this.amount = amount;
        this.lore = lore;
        this.displayName = displayName;
        this.slot = slot;
        this.headData = headData;
        this.nbtTags = nbtTags;
    }

    public Item(XMaterial material, int slot, String headData, Integer amount, String displayName, List<String> lore) {
        this.material = material;
        this.amount = amount;
        this.lore = lore;
        this.displayName = displayName;
        this.slot = slot;
        this.headData = headData;
        this.nbtTags = new HashMap<>();
    }

    public Item(XMaterial material, int slot, Integer amount, String displayName, String headOwner, List<String> lore, HashMap<String, Object> nbtTags) {
        this.material = material;
        this.amount = amount;
        this.lore = lore;
        this.displayName = displayName;
        this.headOwner = headOwner;
        this.slot = slot;
        this.nbtTags = nbtTags;
    }

    public Item(XMaterial material, int slot, Integer amount, String displayName, String headOwner, List<String> lore) {
        this.material = material;
        this.amount = amount;
        this.lore = lore;
        this.displayName = displayName;
        this.headOwner = headOwner;
        this.slot = slot;
        this.nbtTags = new HashMap<>();
    }

    public Item(XMaterial material, Integer amount, String displayName, String headOwner, List<String> lore, HashMap<String, Object> nbtTags) {
        this.material = material;
        this.amount = amount;
        this.lore = lore;
        this.displayName = displayName;
        this.headOwner = headOwner;
        this.nbtTags = nbtTags;
    }

    public Item(XMaterial material, Integer amount, String displayName, String headOwner, List<String> lore) {
        this.material = material;
        this.amount = amount;
        this.lore = lore;
        this.displayName = displayName;
        this.headOwner = headOwner;
        this.nbtTags = new HashMap<>();
    }

    public Item clone() {
        Item item = new Item(this.material, this.amount, this.displayName, this.lore, this.nbtTags);

        if (slot != null) item.slot = this.slot;
        if (headOwner != null) item.headOwner = this.headOwner;
        if (headData != null) item.headData = this.headData;

        return item;
    }

    public ItemStack parseItem() {
        return ItemBuilder.makeItem(this);
    }

    @Override
    public String toString() {
        return "Item{" +
                "material=" + material +
                ", amount=" + amount +
                ", displayName='" + displayName + '\'' +
                ", headData='" + headData + '\'' +
                ", headOwner='" + headOwner + '\'' +
                ", lore=" + lore +
                ", slot=" + slot +
                ", nbtTags=" + nbtTags +
                '}';
    }

    public boolean equals(Object o, boolean exact) {
        if (exact) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Item item = (Item) o;

            if (this.lore == null) {
                this.lore = new ArrayList<>();
            }
            if (item.lore == null) {
                item.lore = new ArrayList<>();
            }

            return amount.equals(item.amount) &&
                    material == item.material &&
                    Objects.equals(displayName, item.displayName) &&
                    Objects.equals(headData, item.headData) &&
                    Objects.equals(headOwner, item.headOwner) &&
                    Objects.equals(lore, item.lore) &&
                    Objects.equals(slot, item.slot) &&
                    Objects.equals(nbtTags, item.nbtTags);
        } else {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Item item = (Item) o;

            if (this.lore == null) {
                this.lore = new ArrayList<>();
            }
            if (item.lore == null) {
                item.lore = new ArrayList<>();
            }

            return material == item.material &&
                    Objects.equals(displayName, item.displayName) &&
                    Objects.equals(headData, item.headData) &&
                    Objects.equals(headOwner, item.headOwner) &&
                    Objects.equals(lore, item.lore) &&
                    Objects.equals(slot, item.slot) &&
                    Objects.equals(nbtTags, item.nbtTags);
        }
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(Object o) {
        return equals(o, true);
    }

    @Override
    public int hashCode() {
        return Objects.hash(material, amount, displayName, headData, headOwner, lore, slot, nbtTags);
    }
}
