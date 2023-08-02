package maedraw;

import java.awt.*;
import java.awt.geom.Ellipse2D;

/**
 * DRAWING MATRIX of width = 800 and height = 600
 * 0,0              800,0
 *
 *
 *
 * 0,600            800,600
 */

class PencilTip extends Tip {
    Double tipSize;
    Ellipse2D.Double tip;

    public PencilTip(Double x, Double y, Double tipSize, Color color) {
        this.x = x;
        this.y = y;
        this.tipSize = tipSize;
        this.color = color;
        this.markingState = MarkingState.ON;
        this.tip = new Ellipse2D.Double(x, y, tipSize, tipSize);
    }

    public PencilTip(PencilTip tip){
        this.x = tip.x;
        this.y = tip.y;
        this.tipSize = tip.tipSize;
        this.color = tip.color;
        this.markingState = tip.markingState;
        this.tip = tip.tip;
    }


    public Tip singleMoveAngle(double angleDirection){
        double angleRadian = (angleDirection * Math.PI / 180);
        PencilTip olderTip = new PencilTip(this);
        this.x = olderTip.x + (1 * Math.cos(angleRadian));
        this.y = olderTip.y - (1 * Math.sin(angleRadian));
        this.tip = new Ellipse2D.Double(x, y, tipSize, tipSize);
        return olderTip;
    }

    public Tip singleMovePos(double dest_x, double dest_y){
        PencilTip olderTip = new PencilTip(this);
        double a_y = dest_y - Math.floor(this.y);
        double a_x = dest_x - Math.floor(this.x);
        if (Math.abs(a_x) < 1E-10){
            if (Math.abs(a_y) < 1E-10){
                this.x = dest_x;
                this.y = dest_y;

            } else {
                this.y = (this.y > dest_y) ? olderTip.y - 1 : olderTip.y + 1 ;
                this.x = dest_x;
            }
        } else {
            double a = (dest_y - Math.floor(this.y))/(dest_x - Math.floor(this.x));
            double b = this.y - (a * this.x);
            if (a < 0){
                this.x = a_x < 0 ? olderTip.x - 1 : olderTip.x + 1 ;
                this.y = (a * this.x) + b;
            } else if (a < 1E-10) {
                this.x = (this.x > dest_x) ? olderTip.x - 1 : olderTip.x + 1 ;
            } else if (a > 0){
                this.x = a_x < 0 ? olderTip.x - 1 : olderTip.x + 1 ;
                this.y = (a * this.x) + b;
            }

        }
        this.tip = new Ellipse2D.Double(x, y, tipSize, tipSize);
        return olderTip;
    }


    public void draw(Graphics2D g2d){
        if (this.markingState == MarkingState.ON){
            g2d.setColor(this.color);
            g2d.fill(this.tip);
            g2d.draw(this.tip);
        }

    }

    public String toString(){
        return this.getClass() + " " + this.color + " " + this.tipSize;
    }

}