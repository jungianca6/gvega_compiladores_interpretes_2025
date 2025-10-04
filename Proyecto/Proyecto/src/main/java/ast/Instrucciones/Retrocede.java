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

        int pasos = 0;
        if (value instanceof Integer) {
            pasos = (Integer) value;
        } else {
            throw new RuntimeException("RETROCEDE espera un número entero, pero recibió: " + value);
        }

        // Lógica de mover la tortuga hacia atrás
        // Por ejemplo: Turtle.getInstance().moveBackward(pasos);
        System.out.println("Moviendo avatar hacia atrás " + pasos + " unidades");

        return null; // RETROCEDE no produce valor
    }
}
