package dev.lightdream.api.managers;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import dev.lightdream.api.dto.PluginLocation;
import lombok.SneakyThrows;
import org.bukkit.entity.Player;

public class ProtocolLibManager {

    private final ProtocolManager protocolManager;

    public ProtocolLibManager() {
        protocolManager = ProtocolLibrary.getProtocolManager();
    }

    @SuppressWarnings({"unused", "deprecation"})
    @SneakyThrows
    public void sendWorldBorder(Player player, double x, double z, double diameter) {

        PacketContainer packet1 = protocolManager.createPacket(PacketType.Play.Server.WORLD_BORDER);
        PacketContainer packet2 = protocolManager.createPacket(PacketType.Play.Server.WORLD_BORDER);
        PacketContainer packet3 = protocolManager.createPacket(PacketType.Play.Server.WORLD_BORDER);
        packet1.getWorldBorderActions().writeSafely(0, EnumWrappers.WorldBorderAction.INITIALIZE);
        packet2.getWorldBorderActions().writeSafely(0, EnumWrappers.WorldBorderAction.SET_CENTER);
        packet3.getWorldBorderActions().writeSafely(0, EnumWrappers.WorldBorderAction.SET_SIZE);

        packet1.getDoubles()
                .write(0, x)
                .write(1, z)
                .write(2, diameter)
                .write(3, diameter);
        packet1.getLongs()
                .write(0, 1000000000000000000L);
        packet1.getIntegers()
                .write(0, 0)
                .write(1, 0);

        packet2.getDoubles()
                .write(0, x)
                .write(1, z);

        packet3
                .getDoubles()
                .write(0, diameter);

        protocolManager.sendServerPacket(player, packet1);
        protocolManager.sendServerPacket(player, packet2);
        protocolManager.sendServerPacket(player, packet3);
    }

    @SuppressWarnings({"unused"})
    @SneakyThrows
    public void sendWorldBorder(Player player, PluginLocation location, double newDiam) {
        sendWorldBorder(player, location.x, location.z, newDiam);
    }
}
