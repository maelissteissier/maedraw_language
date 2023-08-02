package maedraw.types;

public abstract class Primitive {
    @Override
    public abstract String toString();

    @Override
    public abstract boolean equals(
            Object o);

    @Override
    public abstract int hashCode();
}
