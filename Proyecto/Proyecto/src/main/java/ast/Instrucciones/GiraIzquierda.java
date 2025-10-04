package ast.Instrucciones;

import ast.ASTNode;
import java.util.Map;

public class GiraIzquierda implements ASTNode {

    private ASTNode expr; // expresión que determina el ángulo de giro

    public GiraIzquierda(ASTNode expr) {
        this.expr = expr;
    }

    @Override
    public Object execute(Map<String, Object> symbolTable) {
        // Evaluar la expresión para obtener el ángulo a girar
        Object value = expr.execute(symbolTable);

        int angulo = 0;
        if (value instanceof Integer) {
            angulo = (Integer) value;
        } else {
            throw new RuntimeException("GIRA.IZQUIERDA espera un número entero (grados), pero recibió: " + value);
        }

        // Lógica de girar la tortuga a la izquierda
        // Por ejemplo: Turtle.getInstance().turnLeft(angulo);
        System.out.println("Girando avatar a la izquierda " + angulo + " grados");

        return null; // no produce valor
    }
}
