package ast.Instrucciones;

import ast.ASTNode;
import java.util.Map;

public class OcultaTortuga implements ASTNode {

    public OcultaTortuga() {
        // No necesita parámetros
    }

    @Override
    public Object execute(Map<String, Object> symbolTable) {
        // Lógica para colocar la tortuga en la posición inicial (superior izquierda)
        // Por ejemplo: Turtle.getInstance().resetPosition();
        System.out.println("Colocando la tortuga en la posición inicial y ocultándola");

        return null; // no produce valor
    }
}
