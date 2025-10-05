package ast.Tortuga;

import ast.ASTNode;
import java.util.Map;


public class Espera implements ASTNode {

    private ASTNode expr; // expresión que determina el tiempo de espera

    public Espera(ASTNode expr) {
        this.expr = expr;
    }

    @Override
    public Object execute(Map<String, Object> symbolTable) {
        // Evaluar la expresión para obtener el tiempo de espera
        Object value = expr.execute(symbolTable);

        int tiempo;
        if (value instanceof Integer) {
            tiempo = (Integer) value;
        } else {
            throw new RuntimeException("ESPERA espera un número entero, pero recibió: " + value);
        }

        Turtle t = (Turtle) symbolTable.get("turtle");
        if (t == null) {
            throw new RuntimeException("No se inicializó la tortuga");
        }

        System.out.println("Esperando " + (tiempo/60.0) + " segundos");
        t.esperar(tiempo);
        return null;
    }
}