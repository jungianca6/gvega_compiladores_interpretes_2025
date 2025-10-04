package ast.Instrucciones;

import ast.ASTNode;
import java.util.Map;

public class Avanza implements ASTNode {

    private ASTNode expr; // la expresión que determina la distancia

    public Avanza(ASTNode expr) {
        this.expr = expr;
    }

    @Override
    public Object execute(Map<String, Object> symbolTable) {
        Object value = expr.execute(symbolTable);

        if (!(value instanceof Integer)) {
            throw new RuntimeException("AVANZA espera un número entero, pero recibió: " + value);
        }

        int pasos = (Integer) value;

        // Llamar al motor de tortuga
        Turtle t = (Turtle) symbolTable.get("turtle");
        if (t == null) {
            throw new RuntimeException("No se inicializó la tortuga");
        }

        t.moveForward(pasos);

        System.out.println("Moviendo avatar hacia adelante " + pasos + " unidades");

        return null;
    }
}
