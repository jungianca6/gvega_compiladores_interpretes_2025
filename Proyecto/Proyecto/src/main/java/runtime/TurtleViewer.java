package runtime;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;

public class TurtleViewer extends JFrame {
    private TurtleRuntime turtle;
    private TurtleCanvas canvas;

    // Dimensiones por defecto del lienzo
    private static final int CANVAS_WIDTH = 800;
    private static final int CANVAS_HEIGHT = 600;

    public TurtleViewer(TurtleRuntime turtle) {
        this.turtle = turtle;
        setTitle("Turtle Graphics Viewer");
        setSize(CANVAS_WIDTH, CANVAS_HEIGHT);

        // FIX: Usa EXIT_ON_CLOSE para asegurar que la JVM termine cuando el usuario cierre la ventana.
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.canvas = new TurtleCanvas(turtle);
        add(this.canvas);
    }

    public void display() {
        setVisible(true);
    }

    /**
     * Guarda el contenido del dibujo de la tortuga en un archivo PNG.
     * La imagen se genera a partir del contenido del canvas.
     * @param filePath La ruta completa donde se guardará el archivo (incluyendo la carpeta y el nombre).
     * @return true si se guardó con éxito, false en caso contrario.
     */
    public boolean saveImage(String filePath) {
        // Usamos las dimensiones por defecto (800x600) para la imagen a guardar
        int w = CANVAS_WIDTH;
        int h = CANVAS_HEIGHT;

        // Crear una imagen en memoria
        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();

        // Rellenar el fondo de blanco
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, w, h);

        // Dibujar el contenido de la tortuga en la imagen
        this.canvas.drawToGraphics(g2d, w, h);

        g2d.dispose();

        try {
            File outputfile = new File(filePath);

            // Asegurar que la carpeta IMGResultados exista
            File parentDir = outputfile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                if (!parentDir.mkdirs()) {
                    System.err.println("Error: No se pudo crear el directorio: " + parentDir.getAbsolutePath());
                    return false;
                }
            }

            // Escribir la imagen al archivo en formato PNG
            ImageIO.write(image, "png", outputfile);
            return true;
        } catch (IOException e) {
            System.err.println("Error al guardar la imagen en: " + filePath + ". Detalle: " + e.getMessage());
            return false;
        }
    }

    /**
     * Lienzo interno para dibujar los comandos de la tortuga.
     */
    private class TurtleCanvas extends JPanel {
        private TurtleRuntime turtle;

        public TurtleCanvas(TurtleRuntime turtle) {
            this.turtle = turtle;
            setBackground(Color.WHITE);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            drawToGraphics(g2d, getWidth(), getHeight());
            g2d.dispose();
        }

        /**
         * Lógica de dibujo central que puede ser usada tanto por paintComponent como por saveImage.
         */
        public void drawToGraphics(Graphics2D g2d, int width, int height) {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Center offset
            int offsetX = width / 2;
            int offsetY = height / 2;

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
        }

        private Color getColor(String colorName) {
            switch (colorName.toLowerCase()) {
                case "azul": return Color.BLUE;
                case "rojo": return Color.RED;
                case "negro": return Color.BLACK;
                case "verde": return Color.GREEN;
                case "amarillo": return Color.YELLOW;
                case "naranja": return Color.ORANGE;
                default: return Color.BLACK;
            }
        }
    }
}
