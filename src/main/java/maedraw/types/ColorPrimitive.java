package maedraw.types;


import java.awt.*;

public class ColorPrimitive extends Primitive {

    private Color value;

    public ColorPrimitive(ColorEnum value) {
        this.value = value.color;
    }

    public ColorPrimitive(Color color) {
        this.value = color;
    }


    public ColorPrimitive brightenColor(int index){
        Color brighterColor = new Color(this.value.getRed(), this.value.getGreen(), this.value.getBlue());
        for (int i = 0; i < index; i++){
            brighterColor = brighterColor.brighter();
        }
        return new ColorPrimitive(brighterColor);
    }

    public ColorPrimitive darkenColor(int index){
        Color darkerColor = new Color(this.value.getRed(), this.value.getGreen(), this.value.getBlue());
        for (int i = 0; i < index; i++){
            darkerColor = darkerColor.darker();
        }
        return new ColorPrimitive(darkerColor);
    }

    public void mixColor(Color otherColor){
        // function found at http://www.java2s.com/Code/Java/2D-Graphics-GUI/Blendtwocolors.htm
        // Attention : rgb color mixing doesn't behave like real colors.
        // For example Blue and yellow mixed give grey and not green. It's a normal behaviour for rgb
        double totalAlpha = this.value.getAlpha() + otherColor.getAlpha();
        double weight0 = this.value.getAlpha() / totalAlpha;
        double weight1 = otherColor.getAlpha() / totalAlpha;

        double r = weight0 * this.value.getRed() + weight1 * otherColor.getRed();
        double g = weight0 * this.value.getGreen() + weight1 * otherColor.getGreen();
        double b = weight0 * this.value.getBlue() + weight1 * otherColor.getBlue();
        double a = Math.max(this.value.getAlpha(), otherColor.getAlpha());

        this.value = new Color((int) r, (int) g, (int) b, (int) a);
    }

    public Color getValue(){
        return this.value;
    }

    @Override
    public String toString() {
        return this.value.toString();
    }

    @Override
    public boolean equals(
            Object o) {
        if (!(o instanceof ColorPrimitive)) {
            return false;
        }
        return ((ColorPrimitive) o).value == this.value;

    }

    @Override
    public int hashCode() {
        return this.value.hashCode();
    }
}


