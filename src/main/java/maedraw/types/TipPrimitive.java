package maedraw.types;

import maedraw.Tip;

public class TipPrimitive extends Primitive {

    private Tip value;

    public TipPrimitive(Tip value) {
        this.value = value;
    }

    public Tip getValue() {
        return value;
    }

    @Override
    public  String toString(){
        return this.value.toString();
    }

    @Override
    public  boolean equals(
            Object o){
        if (!(o instanceof TipPrimitive)) {
            return false;
        }
        return ((TipPrimitive) o).value.getColor() == this.value.getColor() &&
                ((TipPrimitive) o).value.getMarkingState() == this.value.getMarkingState() &&
                ((TipPrimitive) o).value.getClass() == this.value.getClass();

    }

    @Override
    public  int hashCode(){
        return this.value.hashCode();
    }
}
