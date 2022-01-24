package dev.lightdream.api.utils;

import com.sk89q.worldedit.*;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.regions.CuboidRegion;
import dev.lightdream.api.LightDreamPlugin;
import dev.lightdream.api.dto.location.PluginLocation;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.io.File;

@SuppressWarnings({"deprecation", "unused"})
public class WorldEditUtils {

    public static BlockArrayClipboard copy(PluginLocation pos1, PluginLocation pos2) throws WorldEditException {
        World world = Bukkit.getWorld(pos1.world);
        CuboidRegion region = new CuboidRegion(new BukkitWorld(world), new Vector(pos1.x, pos1.y, pos1.z), new Vector(pos2.x, pos2.y, pos2.z));
        BlockArrayClipboard clipboard = new BlockArrayClipboard(region);

        EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(new BukkitWorld(world), 10000000);
        ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(editSession,
                region,
                clipboard,
                new Vector(Math.min(pos1.x, pos2.x), Math.min(pos1.y, pos2.y), Math.min(pos1.z, pos2.z)));
        Operations.complete(forwardExtentCopy);

        return clipboard;
    }

    //TODO save schematic function

    @SneakyThrows
    public static void paste(PluginLocation pos, CuboidClipboard clipboard) {
        World world = Bukkit.getWorld(pos.world);
        EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(new BukkitWorld(world), 1000000000);
        clipboard.paste(editSession, new Vector(pos.x, pos.y, pos.z), true);
    }

    @SneakyThrows
    public static CuboidClipboard load(String subFolder, String name, LightDreamPlugin plugin) {
        File file = new File(plugin.getDataFolder().getPath() + "/" + subFolder + "/" + name + ".schematic");
        return CuboidClipboard.loadSchematic(file);
    }

}
