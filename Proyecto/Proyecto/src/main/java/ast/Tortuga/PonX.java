package ast.Tortuga;

import ast.ASTNode;
import java.util.Map;

public class PonX implements ASTNode {

    private ASTNode expr; // expresión que determina la coordenada X

    public PonX(ASTNode expr) {
        this.expr = expr;
    }

    @Override
    public Object execute(Map<String, Object> symbolTable) {
        // Evaluar la expresión para obtener la coordenada X
        Object value = expr.execute(symbolTable);

        int x;
        if (value instanceof Integer) {
            x = (Integer) value;
        } else {
            throw new RuntimeException("PonX espera un número entero, pero recibió: " + value);
        }

        Turtle t = (Turtle) symbolTable.get("turtle");
        if (t == null) {
            throw new RuntimeException("No se inicializó la tortuga");
        }

        t.setX(x);
        System.out.println("Moviendo tortuga a la coordenada X = " + x);

        return null; // no produce valor
    }
}
