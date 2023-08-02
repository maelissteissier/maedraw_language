package maedraw;

import java.awt.*;

public abstract class Tip{
    private final int SLEEP_MILLIS = 2;
    private final int SLEEP_NANO = 0;
    Double x,y;
    Color color;
    MarkingState markingState;

    public abstract String toString();
    public abstract void draw(Graphics2D g2d);
    public abstract Tip singleMoveAngle(double angleDirection);
    public abstract Tip singleMovePos(double dest_x, double dest_y);


    public void moveAngle(int lenght, Sketch sketch, Drawing.DrawingPanel drawing, double angleDirection){
        for (int i = 0 ; i < lenght; i++) {
            sketch.addDrawingPart(this.singleMoveAngle(angleDirection));
            // Repaint the whole frame
            drawing.repaint();
            try
            {
                Thread.sleep(SLEEP_MILLIS, SLEEP_NANO);
            } catch (InterruptedException e)
            {
            }
        }
    }

public void movePos(Sketch sketch, Drawing.DrawingPanel drawing, double dest_x, double dest_y){
    if (this.x < dest_x){
        while (Math.floor(this.x) < Math.floor(dest_x)) {
            drawMovePos(sketch, drawing, dest_x, dest_y);
        }
    } else if (Math.floor(this.x) > Math.floor(dest_x)){
        while (this.x > dest_x) {
            drawMovePos(sketch, drawing, dest_x, dest_y);
        }
    } else if (Math.floor(this.y) > Math.floor(dest_y)){
        while (this.y > dest_y) {
            drawMovePos(sketch, drawing, dest_x, dest_y);
        }
    }else if (Math.floor(this.y) < Math.floor(dest_y)){
        while (this.y < dest_y) {
            drawMovePos(sketch, drawing, dest_x, dest_y);
        }
    }
}

    public Color getColor() {
        return color;
    }

    public MarkingState getMarkingState() {
        return markingState;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setMarkingState(MarkingState markingState) {
        this.markingState = markingState;
    }

    private void drawMovePos(Sketch sketch, Drawing.DrawingPanel drawing, double dest_x, double dest_y){
        Tip formerTip = this.singleMovePos(dest_x, dest_y);
        if (formerTip.getMarkingState() == MarkingState.ON){
            sketch.addDrawingPart(formerTip);
            // Repaint the whole frame
            drawing.repaint();
            try
            {
                Thread.sleep(SLEEP_MILLIS, SLEEP_NANO);
            } catch (InterruptedException e)
            {
            }
        }
    }
}
