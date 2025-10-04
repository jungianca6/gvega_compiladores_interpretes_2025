package ast.Instrucciones;

import ast.ASTNode;
import java.util.Map;
import java.util.List;

public class Avanza implements ASTNode {

    private ASTNode expr; // la expresión que determina la distancia

    public Avanza(ASTNode expr) {
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
            throw new RuntimeException("AVANZA espera un número entero, pero recibió: " + value);
        }

        // Aquí llamamos a la lógica de mover la tortuga
        // Por ejemplo: Turtle.moveForward(pasos);
        System.out.println("Moviendo avatar hacia adelante " + pasos + " unidades");

        // Retornar null porque AVANZA no produce valor
        return null;
    }
}
