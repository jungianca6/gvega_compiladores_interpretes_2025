package runtime;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class TurtleViewer extends JFrame {
    private TurtleRuntime turtle;

    public TurtleViewer(TurtleRuntime turtle) {
        this.turtle = turtle;
        setTitle("Turtle Graphics Viewer");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        TurtleCanvas canvas = new TurtleCanvas(turtle);
        add(canvas);
    }

    public void show() {
        setVisible(true);
    }

    private static class TurtleCanvas extends JPanel {
        private TurtleRuntime turtle;

        public TurtleCanvas(TurtleRuntime turtle) {
            this.turtle = turtle;
            setBackground(Color.WHITE);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Center offset
            int offsetX = getWidth() / 2;
            int offsetY = getHeight() / 2;

            // Draw commands
            List<TurtleRuntime.DrawCommand> commands = turtle.getCommands();
            for (TurtleRuntime.DrawCommand cmd : commands) {
                if ("line".equals(cmd.type)) {
                    g2d.setColor(getColor(cmd.color));
                    g2d.setStroke(new BasicStroke(2));
                    g2d.drawLine(
                        offsetX + cmd.x1,
                        offsetY + cmd.y1,
                        offsetX + cmd.x2,
                        offsetY + cmd.y2
                    );
                }
            }

            // Draw axes for reference
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.setStroke(new BasicStroke(1));
            g2d.drawLine(0, offsetY, getWidth(), offsetY); // X axis
            g2d.drawLine(offsetX, 0, offsetX, getHeight()); // Y axis
        }

        private Color getColor(String colorName) {
            switch (colorName) {
                case "azul": return Color.BLUE;
                case "rojo": return Color.RED;
                case "negro": return Color.BLACK;
                default: return Color.BLACK;
            }
        }
    }
}

