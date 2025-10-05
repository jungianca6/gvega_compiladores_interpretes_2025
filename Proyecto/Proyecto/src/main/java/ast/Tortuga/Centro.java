package ast.Tortuga;

import ast.ASTNode;
import java.util.Map;
public class Centro implements ASTNode {

    public Centro() {
        // No necesita parámetros
    }

    @Override
    public Object execute(Map<String, Object> symbolTable) {
        Turtle t = (Turtle) symbolTable.get("turtle");
        if (t == null) {
            throw new RuntimeException("No se inicializó la tortuga");
        }

        System.out.println("Tortuga movida al centro");
        t.moverCentro();
        return null;
    }
}
