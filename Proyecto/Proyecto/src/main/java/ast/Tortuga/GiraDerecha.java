package ast.Tortuga;

import ast.ASTNode;
import java.util.Map;

public class GiraDerecha implements ASTNode {

    private ASTNode expr; // expresión que determina el ángulo de giro

    public GiraDerecha(ASTNode expr) {
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
            throw new RuntimeException("GIRA.DERECHA espera un número entero (grados), pero recibió: " + value);
        }

        Turtle t = (Turtle) symbolTable.get("turtle");
        if (t == null) {
            throw new RuntimeException("No se inicializó la tortuga");
        }
        System.out.println("Girando avatar a la derecha " + angulo + " grados");
        t.turnRight(angulo);

        return null; // no produce valor
    }
}
