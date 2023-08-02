package maedraw.types;

public enum Size {
    SMALL(1.),
    MED(10.),
    LARGE(40.);

    public final double size;

    private Size(double size) {
        this.size = size;
    }
}
