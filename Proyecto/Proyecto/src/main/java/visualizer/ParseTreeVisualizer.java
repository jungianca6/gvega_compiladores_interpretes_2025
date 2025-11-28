package visualizer;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.Token;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Visualizador del Parse Tree completo de ANTLR4 en Full HD.
 * Muestra todas las reglas gramaticales y tokens léxicos.
 */
public class ParseTreeVisualizer {

    // Configuración Full HD
    private static final int MIN_WIDTH = 1920;
    private static final int MIN_HEIGHT = 1080;

    // Configuración de nodos
    private static final int NODE_WIDTH = 200;
    private static final int NODE_HEIGHT = 60;
    private static final int HORIZONTAL_GAP = 25;
    private static final int VERTICAL_GAP = 70;
    private static final int PADDING = 50;

    private int totalNodes = 0;
    private int maxDepth = 0;
    private int currentY = 0;

    /**
     * Genera una imagen del Parse Tree completo
     */
    public boolean generateParseTreeImage(ParseTree tree, String outputPath) {
        if (tree == null) {
            System.out.println("⚠️  No hay Parse Tree para visualizar");
            return false;
        }

        System.out.println("\n🌳 Generando visualización del Parse Tree en Full HD...");

        // Calcular dimensiones
        totalNodes = 0;
        maxDepth = 0;
        calculateTreeSize(tree, 0);

        // Calcular altura necesaria basada en nodos
        int width = Math.max(MIN_WIDTH, (maxDepth + 1) * (NODE_WIDTH + HORIZONTAL_GAP) + PADDING * 2);
        int height = Math.max(MIN_HEIGHT, totalNodes * 25 + 200);

        System.out.println("   📏 Resolución: " + width + "x" + height + " px");
        System.out.println("   📊 Nodos totales: " + totalNodes);
        System.out.println("   📊 Profundidad máxima: " + maxDepth);

        // Crear imagen
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        // Configuración de calidad
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // Fondo con gradiente
        GradientPaint gradient = new GradientPaint(
                0, 0, new Color(245, 247, 250),
                0, height, new Color(255, 255, 255)
        );
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, width, height);

        // Borde
        g2d.setColor(new Color(200, 200, 210));
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRect(10, 10, width - 20, height - 20);

        // Encabezado
        drawHeader(g2d, width);

        // Dibujar árbol
        currentY = 130;
        drawParseTree(g2d, tree, PADDING, 0);

        // Pie de página
        drawFooter(g2d, width, height);

        g2d.dispose();

        // Guardar
        try {
            File outputFile = new File(outputPath);
            File parentDir = outputFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            ImageIO.write(image, "png", outputFile);
            System.out.println("✅ Parse Tree guardado en: " + outputFile.getAbsolutePath());
            return true;
        } catch (IOException e) {
            System.err.println("❌ Error al guardar Parse Tree: " + e.getMessage());
            return false;
        }
    }

    /**
     * Dibuja el encabezado
     */
    private void drawHeader(Graphics2D g2d, int width) {
        GradientPaint headerGradient = new GradientPaint(
                0, 20, new Color(46, 125, 50),
                0, 100, new Color(67, 160, 71)
        );
        g2d.setPaint(headerGradient);
        g2d.fillRoundRect(30, 30, width - 60, 70, 15, 15);

        g2d.setColor(new Color(27, 94, 32));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(30, 30, width - 60, 70, 15, 15);

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 32));
        g2d.drawString("Parse Tree (ANTLR4)", 50, 65);

        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        g2d.drawString("Árbol de parseo completo con reglas y tokens", 50, 88);

        String info = "Nodos: " + totalNodes + " | Profundidad: " + maxDepth;
        FontMetrics fm = g2d.getFontMetrics();
        int infoWidth = fm.stringWidth(info);
        g2d.drawString(info, width - infoWidth - 60, 88);
    }

    /**
     * Dibuja el pie de página
     */
    private void drawFooter(Graphics2D g2d, int width, int height) {
        g2d.setColor(new Color(120, 120, 120));
        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        String footer = "Generado por Compilador Logo-Turtle | Parse Tree Visualization";
        FontMetrics fm = g2d.getFontMetrics();
        int footerWidth = fm.stringWidth(footer);
        g2d.drawString(footer, (width - footerWidth) / 2, height - 25);
    }

    /**
     * Calcula el tamaño del árbol
     */
    private void calculateTreeSize(ParseTree tree, int depth) {
        if (tree == null) return;
        totalNodes++;
        maxDepth = Math.max(maxDepth, depth);

        int childCount = tree.getChildCount();
        for (int i = 0; i < childCount; i++) {
            calculateTreeSize(tree.getChild(i), depth + 1);
        }
    }

    /**
     * Dibuja el parse tree recursivamente
     */
    private int drawParseTree(Graphics2D g2d, ParseTree tree, int x, int depth) {
        if (tree == null) return currentY;

        int nodeX = x + (depth * (NODE_WIDTH + HORIZONTAL_GAP));
        int nodeY = currentY;
        currentY += VERTICAL_GAP;

        // Obtener información del nodo
        String nodeName;
        String nodeValue = "";
        Color nodeColor;
        boolean isTerminal = tree instanceof TerminalNode;

        if (isTerminal) {
            TerminalNode terminal = (TerminalNode) tree;
            Token token = terminal.getSymbol();
            nodeName = getTokenName(token);
            nodeValue = token.getText();
            if (nodeValue.length() > 30) {
                nodeValue = nodeValue.substring(0, 27) + "...";
            }
            nodeColor = new Color(255, 183, 77); // Naranja para terminales
        } else {
            ParserRuleContext ctx = (ParserRuleContext) tree;
            nodeName = getRuleName(ctx);
            nodeColor = new Color(100, 181, 246); // Azul para reglas
        }

        // Dibujar hijos primero y guardar sus posiciones
        List<Integer> childYPositions = new ArrayList<>();
        int childCount = tree.getChildCount();

        for (int i = 0; i < childCount; i++) {
            int childStartY = currentY;
            drawParseTree(g2d, tree.getChild(i), x, depth + 1);
            childYPositions.add(childStartY);
        }

        // Dibujar conexiones
        g2d.setColor(new Color(120, 120, 140, 150));
        g2d.setStroke(new BasicStroke(1.5f));
        for (int childY : childYPositions) {
            int startX = nodeX + NODE_WIDTH;
            int startY = nodeY + NODE_HEIGHT / 2;
            int endX = nodeX + NODE_WIDTH + HORIZONTAL_GAP;
            int endY = childY + NODE_HEIGHT / 2;

            // Línea escalonada
            int midX = (startX + endX) / 2;
            g2d.drawLine(startX, startY, midX, startY);
            g2d.drawLine(midX, startY, midX, endY);
            g2d.drawLine(midX, endY, endX, endY);
        }

        // Dibujar el nodo
        drawNode(g2d, nodeX, nodeY, nodeName, nodeValue, nodeColor, isTerminal);

        return nodeY;
    }

    /**
     * Dibuja un nodo individual
     */
    private void drawNode(Graphics2D g2d, int x, int y, String label, String value, Color baseColor, boolean isTerminal) {
        // Sombra
        g2d.setColor(new Color(0, 0, 0, 20));
        for (int i = 0; i < 3; i++) {
            g2d.fillRoundRect(x + i + 1, y + i + 1, NODE_WIDTH, NODE_HEIGHT, 15, 15);
        }

        // Fondo con gradiente
        GradientPaint nodeGradient = new GradientPaint(
                x, y, brighten(baseColor, 0.15f),
                x, y + NODE_HEIGHT, baseColor
        );
        g2d.setPaint(nodeGradient);
        g2d.fillRoundRect(x, y, NODE_WIDTH, NODE_HEIGHT, 15, 15);

        // Borde
        if (isTerminal) {
            g2d.setColor(darken(baseColor, 0.3f));
            g2d.setStroke(new BasicStroke(2.5f));
        } else {
            g2d.setColor(darken(baseColor, 0.25f));
            g2d.setStroke(new BasicStroke(2f));
        }
        g2d.drawRoundRect(x, y, NODE_WIDTH, NODE_HEIGHT, 15, 15);

        // Texto del label
        g2d.setColor(new Color(20, 20, 30));
        Font labelFont = isTerminal ?
                new Font("Consolas", Font.BOLD, 11) :
                new Font("Segoe UI", Font.BOLD, 12);
        g2d.setFont(labelFont);
        FontMetrics fm = g2d.getFontMetrics();

        String displayLabel = label;
        if (displayLabel.length() > 22) {
            displayLabel = displayLabel.substring(0, 19) + "...";
        }

        int textWidth = fm.stringWidth(displayLabel);
        int textX = x + (NODE_WIDTH - textWidth) / 2;
        int textY = y + (NODE_HEIGHT - fm.getHeight()) / 2 + fm.getAscent() - 5;

        g2d.drawString(displayLabel, textX, textY);

        // Valor (para nodos terminales)
        if (!value.isEmpty()) {
            g2d.setFont(new Font("Consolas", Font.PLAIN, 10));
            g2d.setColor(new Color(60, 60, 80));
            fm = g2d.getFontMetrics();

            textWidth = fm.stringWidth(value);
            textX = x + (NODE_WIDTH - textWidth) / 2;
            textY = y + NODE_HEIGHT - 12;

            // Fondo para el valor
            int padding = 3;
            g2d.setColor(new Color(255, 255, 255, 140));
            g2d.fillRoundRect(textX - padding, textY - fm.getAscent(),
                    textWidth + padding * 2, fm.getHeight(), 6, 6);

            g2d.setColor(new Color(40, 40, 60));
            g2d.drawString(value, textX, textY);
        }
    }

    /**
     * Obtiene el nombre de la regla
     */
    private String getRuleName(ParserRuleContext ctx) {
        String className = ctx.getClass().getSimpleName();
        // Remover "Context" del final
        if (className.endsWith("Context")) {
            className = className.substring(0, className.length() - 7);
        }
        return className;
    }

    /**
     * Obtiene el nombre del token
     */
    private String getTokenName(Token token) {
        String tokenName = token.getText();
        int tokenType = token.getType();

        // Aquí puedes mapear los tipos de token a nombres más descriptivos
        // Por ahora usamos el texto del token como identificador
        switch (tokenType) {
            case -1: return "EOF";
            default:
                // Intenta obtener el nombre del token type
                return tokenName;
        }
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

    /**
     * Oscurece un color
     */
    private Color darken(Color color, float factor) {
        int r = Math.max(0, (int)(color.getRed() * (1 - factor)));
        int g = Math.max(0, (int)(color.getGreen() * (1 - factor)));
        int b = Math.max(0, (int)(color.getBlue() * (1 - factor)));
        return new Color(r, g, b);
    }
}