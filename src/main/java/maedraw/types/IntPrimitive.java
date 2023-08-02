package maedraw.types;


public class IntPrimitive
        extends Primitive {

    private int value;

    public IntPrimitive(
            int value) {

        this.value = value;
    }

    public int getValue() {

        return this.value;
    }

    @Override
    public String toString() {

        return String.valueOf(this.value);
    }

    @Override
    public boolean equals(
            Object o) {

        if (!(o instanceof IntPrimitive)) {
            return false;
        }

        int i = ((IntPrimitive) o).value;
        return i == this.value;
    }

    @Override
    public int hashCode() {

        return this.value;
    }
}

