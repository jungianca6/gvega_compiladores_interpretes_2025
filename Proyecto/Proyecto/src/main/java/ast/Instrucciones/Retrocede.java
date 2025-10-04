package ast.Instrucciones;

import ast.ASTNode;
import java.util.Map;

public class Retrocede implements ASTNode {

    private ASTNode expr; // expresión que determina la cantidad de pasos hacia atrás

    public Retrocede(ASTNode expr) {
        this.expr = expr;
    }

    @Override
    public Object execute(Map<String, Object> symbolTable) {
        // Evaluar la expresión para obtener la cantidad de unidades a mover
        Object value = expr.execute(symbolTable);

        if (!(value instanceof Integer)) {
            throw new RuntimeException("RETROCEDE espera un número entero, pero recibió: " + value);
        }

        int pasos = (Integer) value;


        Turtle t = (Turtle) symbolTable.get("turtle");
        if (t == null) {
            throw new RuntimeException("No se inicializó la tortuga");
        }

        t.moveBackward(pasos);

        System.out.println("Moviendo avatar hacia atrás " + pasos + " unidades");

        return null;
    }
}
