package maedraw.types;

public class BoolPrimitive
        extends Primitive {

    public static final BoolPrimitive TRUE = new BoolPrimitive(true);

    public static final BoolPrimitive FALSE = new BoolPrimitive(false);

    private boolean value;

    private BoolPrimitive(
            boolean value) {

        this.value = value;
    }

    public static Primitive get(
            boolean b) {

        if (b) {
            return TRUE;
        }
        else {
            return FALSE;
        }
    }

    public boolean getValue() {

        return this.value;
    }

    @Override
    public String toString() {

        return String.valueOf(this.value);
    }

    @Override
    public boolean equals(
            Object o) {

        if (!(o instanceof BoolPrimitive)) {
            return false;
        }

        boolean b = ((BoolPrimitive) o).value;
        return b == this.value;
    }

    @Override
    public int hashCode() {

        return this.value ? 1 : 0;
    }
}
