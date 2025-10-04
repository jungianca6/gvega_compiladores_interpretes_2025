package ast.Tortuga;

import ast.ASTNode;
import java.util.Map;


public class SubeLapiz implements ASTNode {

    public SubeLapiz() {
        // No necesita parámetros
    }

    @Override
    public Object execute(Map<String, Object> symbolTable) {
        Turtle t = (Turtle) symbolTable.get("turtle");
        if (t == null) {
            throw new RuntimeException("No se inicializó la tortuga");
        }

        t.subirLapiz();
        System.out.println("Lápiz subido - ya no dibuja al moverse");
        return null;
    }
}