package dev.lightdream.api.managers;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import lombok.SneakyThrows;
import org.bukkit.entity.Player;

public class ProtocolLibManager {

    private final ProtocolManager protocolManager;

    public ProtocolLibManager() {
        protocolManager = ProtocolLibrary.getProtocolManager();
    }

    @SneakyThrows
    public void sendWorldBorder(Player player, double x, double z, double newDiam) {
        PacketContainer packet1 = protocolManager.createPacket(PacketType.Play.Server.WORLD_BORDER);
        PacketContainer packet2 = protocolManager.createPacket(PacketType.Play.Server.WORLD_BORDER);

        packet1.getWorldBorderActions().writeSafely(0, EnumWrappers.WorldBorderAction.SET_CENTER);
        packet2.getWorldBorderActions().writeSafely(0, EnumWrappers.WorldBorderAction.SET_SIZE);

        packet1.getDoubles().write(0, x);
        packet1.getDoubles().write(1, z);

        packet2.getDoubles().write(0, 0.0);
        packet2.getDoubles().write(1, newDiam);

        packet2.getLongs().write(0, 0L);

        protocolManager.sendServerPacket(player, packet1);
        protocolManager.sendServerPacket(player, packet2);
    }
}
