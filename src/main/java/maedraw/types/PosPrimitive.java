package maedraw.types;

import java.awt.geom.Point2D;

public class PosPrimitive extends Primitive{

    Point2D value;

    public PosPrimitive(Point2D pos) {
        this.value = pos;
    }

    public Point2D getValue(){
        return this.value;
    }

    @Override
    public String toString() {
        return this.value.toString();
    }

    @Override
    public boolean equals(
            Object o) {
        if (!(o instanceof PosPrimitive)) {
            return false;
        }
        return ((PosPrimitive) o).value == this.value;

    }

    @Override
    public int hashCode() {
        return this.value.hashCode();
    }
}
