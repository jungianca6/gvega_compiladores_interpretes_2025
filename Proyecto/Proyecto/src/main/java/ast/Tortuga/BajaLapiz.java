package ast.Tortuga;

import ast.ASTNode;
import java.util.Map;


public class BajaLapiz implements ASTNode {

    public BajaLapiz() {
        // No necesita parámetros
    }

    @Override
    public Object execute(Map<String, Object> symbolTable) {
        Turtle t = (Turtle) symbolTable.get("turtle");
        if (t == null) {
            throw new RuntimeException("No se inicializó la tortuga");
        }

        System.out.println("Lápiz bajado - ahora dibuja al moverse");
        t.bajarLapiz();

        return null;
    }
}