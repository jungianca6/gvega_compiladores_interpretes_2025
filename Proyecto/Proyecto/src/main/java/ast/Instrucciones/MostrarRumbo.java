package ast.Instrucciones;

import ast.ASTNode;
import java.util.Map;

public class MostrarRumbo implements ASTNode {

    public MostrarRumbo() {
    }

    @Override
    public Object execute(Map<String, Object> symbolTable) {
        // Aquí se imprime el rumbo actual del avatar
        // Por ahora solo simulamos
        System.out.println("Mostrando rumbo actual del avatar");
        return null;
    }
}
