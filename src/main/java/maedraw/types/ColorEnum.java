package maedraw.types;

import java.awt.*;

public enum ColorEnum {

    RED(Color.RED),
    YELLOW(Color.YELLOW),
    BLUE(Color.BLUE),
    WHITE(Color.WHITE),
    BLACK(Color.BLACK);

    public final Color color;

    private ColorEnum(Color color) {
        this.color = color;
    }
}
