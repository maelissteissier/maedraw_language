package maedraw;

import javax.swing.*;
import java.awt.*;

public class Drawing extends JFrame {


    private int canvasWidth;
    private int canvasHeight;
    private JPanel windowPanel;
    public DrawingPanel drawing;
    public Sketch sketch = new Sketch();
    public Tip pencilTip;


    public void setCanvasWidth(int width){
        this.canvasWidth = width;
    }

    public void setCanvasHeight(int height){
        this.canvasHeight = height;
    }

    public void setUpGUI()
    {
        Container window = getContentPane();
        // Drawing space
        drawing = new DrawingPanel();
        // Window panel
        windowPanel = new JPanel(new FlowLayout());

        window.add(windowPanel, BorderLayout.SOUTH);
        window.add(drawing, BorderLayout.CENTER);
    }

    class DrawingPanel extends JPanel
    {
        private static final long serialVersionUID = 1L;

        DrawingPanel()
        {
            setPreferredSize(new Dimension(canvasWidth, canvasHeight));
            this.setBackground(Color.WHITE);
        }
        // executes all paint methods
        @Override
        public void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // Draw sketch state
            sketch.draw(g2d);
        }
    }
}
