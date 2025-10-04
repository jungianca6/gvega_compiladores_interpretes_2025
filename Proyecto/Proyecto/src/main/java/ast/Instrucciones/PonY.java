package ast.Instrucciones;

import ast.ASTNode;
import java.util.Map;

public class PonY implements ASTNode {

    private ASTNode expr; // expresión que determina la coordenada Y

    public PonY(ASTNode expr) {
        this.expr = expr;
    }

    @Override
    public Object execute(Map<String, Object> symbolTable) {
        // Evaluar la expresión para obtener la coordenada Y
        Object value = expr.execute(symbolTable);

        int y;
        if (value instanceof Integer) {
            y = (Integer) value;
        } else {
            throw new RuntimeException("PonY espera un número entero, pero recibió: " + value);
        }

        Turtle t = (Turtle) symbolTable.get("turtle");
        if (t == null) {
            throw new RuntimeException("No se inicializó la tortuga");
        }

        t.setX(y);

        System.out.println("Moviendo tortuga a la coordenada Y = " + y);

        return null; // no produce valor
    }
}
