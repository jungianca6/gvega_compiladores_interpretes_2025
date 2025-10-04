package ast.Tortuga;

import ast.ASTNode;
import java.util.Map;

public class MostrarRumbo implements ASTNode {

    public MostrarRumbo() {
    }

    @Override
    public Object execute(Map<String, Object> symbolTable) {
        Turtle t = (Turtle) symbolTable.get("turtle");
        if (t == null) {
            throw new RuntimeException("No se inicializó la tortuga");
        }

        int rumbo = t.getAngle();
        System.out.println("Rumbo actual: " + rumbo + " grados");

        return null;
    }
}
