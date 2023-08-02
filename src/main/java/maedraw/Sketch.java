package maedraw;

import java.awt.*;
import java.util.ArrayList;

public class Sketch {
    // data structure representing every drawing part added move after move
    ArrayList<Tip> drawingParts = new ArrayList<>();


    public void addDrawingPart(Tip dp){
        this.drawingParts.add(dp);
    }

    public void draw(Graphics2D g2d){
        for (int i = 0; i < drawingParts.size(); i++){
            drawingParts.get(i).draw(g2d);
        }
    }
}
