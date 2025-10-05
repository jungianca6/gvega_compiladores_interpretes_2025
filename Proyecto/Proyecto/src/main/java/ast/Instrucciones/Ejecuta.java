package ast.Instrucciones;

import ast.ASTNode;
import java.util.Map;
import java.util.List;

public class Ejecuta implements ASTNode {

    private List<ASTNode> ordenes;

    public Ejecuta(List<ASTNode> ordenes) {
        this.ordenes = ordenes;
    }

    @Override
    public Object execute(Map<String, Object> symbolTable) {
        System.out.println("Ejecutando bloque de órdenes:");

        // Ejecutar todas las instrucciones en el bloque
        for (ASTNode orden : ordenes) {
            orden.execute(symbolTable);
        }

        System.out.println("Fin del bloque de órdenes");
        return null;
    }
}