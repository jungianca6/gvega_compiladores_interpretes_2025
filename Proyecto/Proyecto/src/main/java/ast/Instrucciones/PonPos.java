package ast.Instrucciones;

import ast.ASTNode;
import java.util.Map;

public class PonPos implements ASTNode {

    private ASTNode xExpr; // expresión para coordenada X
    private ASTNode yExpr; // expresión para coordenada Y

    public PonPos(ASTNode xExpr, ASTNode yExpr) {
        this.xExpr = xExpr;
        this.yExpr = yExpr;
    }

    @Override
    public Object execute(Map<String, Object> symbolTable) {
        // Evaluar las expresiones de X e Y
        Object xValue = xExpr.execute(symbolTable);
        Object yValue = yExpr.execute(symbolTable);

        int x = 0, y = 0;
        if (xValue instanceof Integer) {
            x = (Integer) xValue;
        } else {
            throw new RuntimeException("PonPos espera un número entero para X, pero recibió: " + xValue);
        }

        if (yValue instanceof Integer) {
            y = (Integer) yValue;
        } else {
            throw new RuntimeException("PonPos espera un número entero para Y, pero recibió: " + yValue);
        }

        // Mover la tortuga a la posición (x, y) sin dibujar
        // Por ejemplo: Turtle.getInstance().moveTo(x, y, false);
        System.out.println("Moviendo tortuga a posición (" + x + ", " + y + ") sin dibujar");

        return null; // no produce valor
    }
}
