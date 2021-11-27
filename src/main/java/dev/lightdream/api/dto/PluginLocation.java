package dev.lightdream.api.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.Objects;


@NoArgsConstructor
@AllArgsConstructor
public class PluginLocation extends Position {

    public String world;
    public float rotationX;
    public float rotationY;

    public PluginLocation(Location location) {
        this.world = location.getWorld().getName();
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        this.rotationX = location.getYaw();
        this.rotationY = location.getPitch();
    }

    public PluginLocation(String world, double x, double y, double z) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public PluginLocation(String world, double x, double y, double z, float rotationX, float rotationY) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.rotationX = rotationX;
        this.rotationY = rotationY;
    }

    public Location toLocation() {
        return new Location(Bukkit.getWorld(world), x, y, z, rotationX, rotationY);
    }

    public Block getBlock() {
        return Bukkit.getWorld(world).getBlockAt(toLocation());
    }

    @SuppressWarnings("unused")
    public void setBlock(Material material) {
        getBlock().setType(material);
    }

    public PluginLocation clone() {
        return new PluginLocation(toLocation());
    }

    @Override
    public String toString() {
        return "PluginLocation{" +
                "world='" + world + '\'' +
                ", rotationX=" + rotationX +
                ", rotationY=" + rotationY +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PluginLocation that = (PluginLocation) o;
        return Double.compare(that.x, x) == 0 && Double.compare(that.y, y) == 0 && Double.compare(that.z, z) == 0 && Objects.equals(world, that.world);
    }

    @Override
    public int hashCode() {
        return Objects.hash(world, x, y, z);
    }

    @SuppressWarnings("unused")
    public void offset(Position position) {
        this.x += position.x;
        this.y += position.y;
        this.z += position.z;
    }

    @SuppressWarnings("unused")
    public void offset(double x, double y, double z) {
        this.x += x;
        this.y += y;
        this.z += z;
    }

    @SuppressWarnings("unused")
    public PluginLocation newOffset(Position position) {
        return new PluginLocation(world, x + position.x, y + position.y, z + position.z, rotationX, rotationY);
    }

    @SuppressWarnings("unused")
    public PluginLocation newRotation(int rotationX, int rotationY) {
        return new PluginLocation(world, this.x, this.y, this.z, rotationX, rotationY);
    }

    @SuppressWarnings("unused")
    public PluginLocation newOffset(double x, double y, double z) {
        return new PluginLocation(world, this.x + x, this.y + y, this.z + z, rotationX, rotationY);
    }

    @SuppressWarnings("unused")
    public void unOffset(Position position) {
        this.x -= position.x;
        this.y -= position.y;
        this.z -= position.z;
    }

    @SuppressWarnings("unused")
    public PluginLocation newUnOffset(Position position) {
        return new PluginLocation(world, x - position.x, y - position.y, z - position.z, rotationX, rotationY);
    }

    @SuppressWarnings("unused")
    public Position toPosition() {
        return new Position(x, y, z);
    }

    @SuppressWarnings("unused")
    public void round() {
        this.x = Math.floor(x);
        this.y = Math.floor(y);
        this.z = Math.floor(z);
    }

    @SuppressWarnings("unused")
    public PluginLocation multiply(Position position) {
        return new PluginLocation(world, x * position.x, y * position.y, z * position.z);
    }

}
