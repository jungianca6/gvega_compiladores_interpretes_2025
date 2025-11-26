package visualizer;

import ast.*;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Visualizador de AST en Full HD (1920x1080) con calidad profesional.
 * Usa reflexión para acceder a los campos sin requerir getters específicos.
 */
public class ASTVisualizer {

    // Configuración Full HD
    private static final int MIN_WIDTH = 1920;
    private static final int MIN_HEIGHT = 1080;

    // Configuración de nodos (más grandes y legibles)
    private static final int NODE_WIDTH = 180;
    private static final int NODE_HEIGHT = 70;
    private static final int HORIZONTAL_GAP = 50;
    private static final int VERTICAL_GAP = 100;
    private static final int PADDING = 60;

    private int maxDepth = 0;
    private int totalNodes = 0;

    /**
     * Genera una imagen Full HD del AST y la guarda en un archivo.
     */
    public boolean generateASTImage(List<ASTNode> programBody, String outputPath) {
        if (programBody == null || programBody.isEmpty()) {
            System.out.println("⚠️  No hay AST para visualizar");
            return false;
        }

        System.out.println("\n🌳 Generando visualización del AST en Full HD...");

        // Calcular dimensiones necesarias
        totalNodes = 0;
        maxDepth = 0;
        for (ASTNode node : programBody) {
            calculateTreeSize(node, 0);
        }

        // Dimensiones mínimas Full HD, expandir si es necesario
        int width = Math.max(MIN_WIDTH, (maxDepth + 1) * (NODE_WIDTH + HORIZONTAL_GAP) + PADDING * 2);
        int height = Math.max(MIN_HEIGHT, totalNodes * 85 + PADDING * 3 + 150);

        System.out.println("   📏 Resolución: " + width + "x" + height + " px (Full HD)");
        System.out.println("   📊 Nodos totales: " + totalNodes);
        System.out.println("   📊 Profundidad máxima: " + maxDepth);

        // Crear imagen
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        // Configuración de máxima calidad
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        // Fondo con gradiente profesional
        GradientPaint gradient = new GradientPaint(
                0, 0, new Color(240, 242, 245),
                0, height, new Color(255, 255, 255)
        );
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, width, height);

        // Borde decorativo
        g2d.setColor(new Color(200, 200, 210));
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRect(10, 10, width - 20, height - 20);

        // Encabezado profesional
        drawHeader(g2d, width);

        // Dibujar árbol
        int startY = 140;
        for (ASTNode node : programBody) {
            startY = drawTree(g2d, node, PADDING, startY, 0) + VERTICAL_GAP / 2;
        }

        // Pie de página con información
        drawFooter(g2d, width, height);

        g2d.dispose();

        // Guardar imagen con máxima calidad
        try {
            File outputFile = new File(outputPath);
            File parentDir = outputFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            ImageIO.write(image, "png", outputFile);
            System.out.println("✅ AST Full HD guardado en: " + outputFile.getAbsolutePath());
            return true;
        } catch (IOException e) {
            System.err.println("❌ Error al guardar imagen del AST: " + e.getMessage());
            return false;
        }
    }

    /**
     * Dibuja el encabezado profesional
     */
    private void drawHeader(Graphics2D g2d, int width) {
        // Fondo del encabezado
        GradientPaint headerGradient = new GradientPaint(
                0, 20, new Color(41, 128, 185),
                0, 120, new Color(52, 152, 219)
        );
        g2d.setPaint(headerGradient);
        g2d.fillRoundRect(30, 30, width - 60, 80, 15, 15);

        // Borde del encabezado
        g2d.setColor(new Color(31, 97, 141));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(30, 30, width - 60, 80, 15, 15);

        // Título principal
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 36));
        g2d.drawString("Abstract Syntax Tree", 50, 70);

        // Subtítulo
        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        g2d.drawString("Visualización del árbol sintáctico abstracto", 50, 95);

        // Información de nodos (derecha)
        String info = "Nodos: " + totalNodes + " | Profundidad: " + maxDepth;
        FontMetrics fm = g2d.getFontMetrics();
        int infoWidth = fm.stringWidth(info);
        g2d.drawString(info, width - infoWidth - 60, 95);
    }

    /**
     * Dibuja el pie de página
     */
    private void drawFooter(Graphics2D g2d, int width, int height) {
        g2d.setColor(new Color(150, 150, 150));
        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        String footer = "Generado por Compilador Logo-Turtle | Full HD (1920x1080)";
        FontMetrics fm = g2d.getFontMetrics();
        int footerWidth = fm.stringWidth(footer);
        g2d.drawString(footer, (width - footerWidth) / 2, height - 30);
    }

    /**
     * Calcula el tamaño del árbol
     */
    private void calculateTreeSize(ASTNode node, int depth) {
        if (node == null) return;
        totalNodes++;
        maxDepth = Math.max(maxDepth, depth);

        List<ASTNode> children = extractChildren(node);
        for (ASTNode child : children) {
            calculateTreeSize(child, depth + 1);
        }
    }

    /**
     * Dibuja un nodo y sus hijos recursivamente con estilo profesional
     */
    private int drawTree(Graphics2D g2d, ASTNode node, int x, int y, int depth) {
        if (node == null) return y;

        int currentX = x + (depth * (NODE_WIDTH + HORIZONTAL_GAP));
        int currentY = y;

        // Obtener información del nodo
        String className = node.getClass().getSimpleName();
        String details = extractNodeDetails(node);
        Color nodeColor = getColorForNodeType(className);

        // Obtener hijos
        List<ASTNode> children = extractChildren(node);

        // Dibujar conexiones a hijos con curvas suaves
        int childY = currentY;
        for (ASTNode child : children) {
            int nextChildY = childY + NODE_HEIGHT + 20;

            // Puntos de conexión
            int startX = currentX + NODE_WIDTH;
            int startY = currentY + NODE_HEIGHT / 2;
            int endX = currentX + NODE_WIDTH + HORIZONTAL_GAP;
            int endY = nextChildY + NODE_HEIGHT / 2;

            // Línea con curva suave (Bézier)
            g2d.setColor(new Color(120, 120, 140, 180));
            g2d.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            int ctrlX = (startX + endX) / 2;
            g2d.drawLine(startX, startY, ctrlX, startY);
            g2d.drawLine(ctrlX, startY, ctrlX, endY);
            g2d.drawLine(ctrlX, endY, endX, endY);

            // Flecha en el extremo
            drawArrow(g2d, ctrlX, endY, endX, endY);

            childY = drawTree(g2d, child, x, nextChildY, depth + 1);
        }

        // Dibujar el nodo actual con sombra y estilo moderno
        drawModernNode(g2d, currentX, currentY, className, details, nodeColor);

        return Math.max(currentY + NODE_HEIGHT, childY);
    }

    /**
     * Dibuja un nodo con estilo moderno y profesional
     */
    private void drawModernNode(Graphics2D g2d, int x, int y, String label, String details, Color baseColor) {
        // Sombra suave
        g2d.setColor(new Color(0, 0, 0, 30));
        for (int i = 0; i < 4; i++) {
            g2d.fillRoundRect(x + i + 2, y + i + 2, NODE_WIDTH, NODE_HEIGHT, 20, 20);
        }

        // Gradiente del nodo
        GradientPaint nodeGradient = new GradientPaint(
                x, y, brighten(baseColor, 0.2f),
                x, y + NODE_HEIGHT, baseColor
        );
        g2d.setPaint(nodeGradient);
        g2d.fillRoundRect(x, y, NODE_WIDTH, NODE_HEIGHT, 20, 20);

        // Borde con brillo
        g2d.setColor(brighten(baseColor, 0.3f));
        g2d.setStroke(new BasicStroke(1.5f));
        g2d.drawRoundRect(x, y, NODE_WIDTH, NODE_HEIGHT, 20, 20);

        g2d.setColor(darken(baseColor, 0.4f));
        g2d.setStroke(new BasicStroke(2f));
        g2d.drawRoundRect(x + 1, y + 1, NODE_WIDTH - 2, NODE_HEIGHT - 2, 18, 18);

        // Texto del nodo (nombre de clase)
        g2d.setColor(new Color(30, 30, 40));
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 14));
        FontMetrics fm = g2d.getFontMetrics();

        String displayLabel = label;
        if (displayLabel.length() > 18) {
            displayLabel = displayLabel.substring(0, 15) + "...";
        }

        int textWidth = fm.stringWidth(displayLabel);
        int textX = x + (NODE_WIDTH - textWidth) / 2;
        int textY = y + (NODE_HEIGHT - fm.getHeight()) / 2 + fm.getAscent() - 5;

        g2d.drawString(displayLabel, textX, textY);

        // Detalles adicionales con estilo
        if (details != null && !details.isEmpty()) {
            g2d.setFont(new Font("Consolas", Font.PLAIN, 11));
            g2d.setColor(new Color(70, 70, 90));
            fm = g2d.getFontMetrics();

            if (details.length() > 22) {
                details = details.substring(0, 19) + "...";
            }

            textWidth = fm.stringWidth(details);
            textX = x + (NODE_WIDTH - textWidth) / 2;
            textY = y + NODE_HEIGHT - 15;

            // Fondo semi-transparente para el detalle
            int padding = 4;
            g2d.setColor(new Color(255, 255, 255, 120));
            g2d.fillRoundRect(textX - padding, textY - fm.getAscent() - 1,
                    textWidth + padding * 2, fm.getHeight() + 2, 8, 8);

            g2d.setColor(new Color(50, 50, 70));
            g2d.drawString(details, textX, textY);
        }
    }

    /**
     * Dibuja una flecha en el extremo de la línea
     */
    private void drawArrow(Graphics2D g2d, int x1, int y1, int x2, int y2) {
        int arrowSize = 8;
        g2d.fillPolygon(
                new int[] {x2, x2 - arrowSize, x2 - arrowSize},
                new int[] {y2, y2 - arrowSize/2, y2 + arrowSize/2},
                3
        );
    }

    /**
     * Extrae los hijos de un nodo usando reflexión
     */
    private List<ASTNode> extractChildren(ASTNode node) {
        List<ASTNode> children = new ArrayList<>();
        if (node == null) return children;

        try {
            // Intentar métodos getter comunes
            tryAddChild(children, node, "getCondition");
            tryAddChild(children, node, "getExpression");
            tryAddChild(children, node, "getLeft");
            tryAddChild(children, node, "getRight");
            tryAddChild(children, node, "getDistancia");
            tryAddChild(children, node, "getGrados");
            tryAddChild(children, node, "getVeces");

            // Intentar listas de hijos
            tryAddChildren(children, node, "getIfBody");
            tryAddChildren(children, node, "getElseBody");
            tryAddChildren(children, node, "getBody");
            tryAddChildren(children, node, "getOrdenes");
            tryAddChildren(children, node, "getCuerpo");
            tryAddChildren(children, node, "getArgumentos");
            tryAddChildren(children, node, "getOperandos");

            // Si no hay métodos, intentar con campos directos
            if (children.isEmpty()) {
                for (Field field : node.getClass().getDeclaredFields()) {
                    field.setAccessible(true);
                    Object value = field.get(node);

                    if (value instanceof ASTNode) {
                        children.add((ASTNode) value);
                    } else if (value instanceof List) {
                        List<?> list = (List<?>) value;
                        for (Object item : list) {
                            if (item instanceof ASTNode) {
                                children.add((ASTNode) item);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            // Ignorar errores de reflexión
        }

        return children;
    }

    /**
     * Intenta agregar un hijo único
     */
    private void tryAddChild(List<ASTNode> children, ASTNode node, String methodName) {
        try {
            Method method = node.getClass().getMethod(methodName);
            Object result = method.invoke(node);
            if (result instanceof ASTNode) {
                children.add((ASTNode) result);
            }
        } catch (Exception e) {
            // Método no existe
        }
    }

    /**
     * Intenta agregar una lista de hijos
     */
    private void tryAddChildren(List<ASTNode> children, ASTNode node, String methodName) {
        try {
            Method method = node.getClass().getMethod(methodName);
            Object result = method.invoke(node);
            if (result instanceof List) {
                List<?> list = (List<?>) result;
                for (Object item : list) {
                    if (item instanceof ASTNode) {
                        children.add((ASTNode) item);
                    }
                }
            }
        } catch (Exception e) {
            // Método no existe
        }
    }

    /**
     * Extrae detalles del nodo
     */
    private String extractNodeDetails(ASTNode node) {
        try {
            // Intentar getName
            try {
                Method getName = node.getClass().getMethod("getName");
                Object name = getName.invoke(node);
                if (name != null) return name.toString();
            } catch (Exception e) {}

            // Intentar getValue
            try {
                Method getValue = node.getClass().getMethod("getValue");
                Object value = getValue.invoke(node);
                if (value != null) return value.toString();
            } catch (Exception e) {}

            // Buscar en campos
            for (Field field : node.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                String fieldName = field.getName().toLowerCase();

                if (fieldName.equals("name") || fieldName.equals("value") ||
                        fieldName.equals("color") || fieldName.equals("text")) {
                    Object value = field.get(node);
                    if (value != null && !(value instanceof List) && !(value instanceof ASTNode)) {
                        return value.toString();
                    }
                }
            }
        } catch (Exception e) {
            // Ignorar
        }

        return "";
    }

    /**
     * Colores profesionales según tipo de nodo
     */
    private Color getColorForNodeType(String className) {
        className = className.toLowerCase();

        // Control de flujo - Azul
        if (className.contains("si") || className.contains("mientras") ||
                className.contains("repite") || className.contains("hasta") ||
                className.contains("hazmientras")) {
            return new Color(100, 181, 246);
        }

        // Variables - Verde
        if (className.contains("var") || className.contains("inic")) {
            return new Color(129, 199, 132);
        }

        // Aritméticas - Naranja
        if (className.contains("suma") || className.contains("diferencia") ||
                className.contains("producto") || className.contains("division") ||
                className.contains("potencia") || className.contains("addition") ||
                className.contains("substraction") || className.contains("multiplication")) {
            return new Color(255, 183, 77);
        }

        // Lógicas - Amarillo
        if (className.contains("menor") || className.contains("mayor") ||
                className.contains("iguales") || className.contains("and") ||
                className.contains("or") || className.contains("equal") ||
                className.contains("greater") || className.contains("less")) {
            return new Color(255, 238, 88);
        }

        // Funciones - Morado
        if (className.contains("funcion") || className.contains("llamada")) {
            return new Color(186, 104, 200);
        }

        // Tortuga - Cyan
        if (className.contains("avanza") || className.contains("retrocede") ||
                className.contains("gira") || className.contains("lapiz") ||
                className.contains("color") || className.contains("centro") ||
                className.contains("pon")) {
            return new Color(77, 208, 225);
        }

        // Constantes - Gris
        if (className.contains("constant")) {
            return new Color(224, 224, 224);
        }

        // Default - Blanco perla
        return new Color(250, 250, 250);
    }

    /**
     * Oscurece un color
     */
    private Color darken(Color color, float factor) {
        int r = Math.max(0, (int)(color.getRed() * (1 - factor)));
        int g = Math.max(0, (int)(color.getGreen() * (1 - factor)));
        int b = Math.max(0, (int)(color.getBlue() * (1 - factor)));
        return new Color(r, g, b);
    }

    /**
     * Aclara un color
     */
    private Color brighten(Color color, float factor) {
        int r = Math.min(255, (int)(color.getRed() + (255 - color.getRed()) * factor));
        int g = Math.min(255, (int)(color.getGreen() + (255 - color.getGreen()) * factor));
        int b = Math.min(255, (int)(color.getBlue() + (255 - color.getBlue()) * factor));
        return new Color(r, g, b);
    }
}