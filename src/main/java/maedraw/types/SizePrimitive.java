package maedraw.types;


public class SizePrimitive extends Primitive{

    private Size value;

    public SizePrimitive(Size value) {
        this.value = value;
    }

    public Size getSize() {
        return this.value;
    }

    public double getValue(){
        return this.value.size;
    }

    @Override
    public String toString() {
        return this.value.toString();
    }

    @Override
    public boolean equals(
            Object o) {
        if (!(o instanceof SizePrimitive)) {
            return false;
        }
        return ((SizePrimitive) o).value == this.value;

    }

    @Override
    public int hashCode() {
        return this.value.hashCode();
    }
}
