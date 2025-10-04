package ast.Instrucciones;

import ast.ASTNode;
import java.util.Map;

public class OcultaTortuga implements ASTNode {

    public OcultaTortuga() {
        // No necesita parámetros
    }

    @Override
    public Object execute(Map<String, Object> symbolTable) {
        Turtle t = (Turtle) symbolTable.get("turtle");
        if (t == null) {
            throw new RuntimeException("No se inicializó la tortuga");
        }

        // COLOCAR EN POSICIÓN INICIAL Y OCULTAR
        t.resetToInitialPosition();

        System.out.println("Tortuga colocada en posición inicial (0, 0) y ocultada");

        return null;
    }
}