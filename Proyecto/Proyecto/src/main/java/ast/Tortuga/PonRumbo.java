package ast.Tortuga;

import ast.ASTNode;
import java.util.Map;

public class PonRumbo implements ASTNode {

    private ASTNode expr; // expresión que determina el ángulo

    public PonRumbo(ASTNode expr) {
        this.expr = expr;
    }

    @Override
    public Object execute(Map<String, Object> symbolTable) {
        // Evaluar la expresión para obtener el ángulo
        Object value = expr.execute(symbolTable);

        int angulo;
        if (value instanceof Integer) {
            angulo = (Integer) value;
        } else {
            throw new RuntimeException("PonRumbo espera un número entero, pero recibió: " + value);
        }

        Turtle t = (Turtle) symbolTable.get("turtle");
        if (t == null) {
            throw new RuntimeException("No se inicializó la tortuga");
        }

        System.out.println("Colocando tortuga en dirección " + angulo + " grados");
        t.setAngle(angulo);

        return null; // no produce valor
    }
}
