package dev.lightdream.api.dto.location;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.lightdream.api.dto.Serializable;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Objects;

@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Position extends Serializable implements java.io.Serializable {

    public Double x;
    public Double y;
    public Double z;

    @SuppressWarnings("unused")
    public Position(Integer x, Integer y, Integer z) {
        super();
        this.x = Double.valueOf(x);
        this.y = Double.valueOf(y);
        this.z = Double.valueOf(z);
    }

    @SuppressWarnings("unused")
    @JsonIgnore
    public PluginLocation getPluginLocation(String world) {
        return new PluginLocation(world, x, y, z);
    }

    public boolean smaller(PluginLocation pos) {
        return pos.x >= x &&
                pos.y >= y &&
                pos.z >= z;
    }

    public boolean bigger(PluginLocation pos) {
        return pos.x <= x &&
                pos.y <= y &&
                pos.z <= z;
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public Position clone() {
        return new Position(x, y, z);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return x.equals(position.x) && y.equals(position.y) && z.equals(position.z);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }

    @SuppressWarnings("unused")
    public void flip() {
        double tmp = this.x;
        this.x = this.z;
        this.z = tmp;
    }

    @SuppressWarnings("unused")
    public Position newFlip() {
        return new Position(this.z, y, this.x);
    }

    @Override
    public String toString() {
        return "Position{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
