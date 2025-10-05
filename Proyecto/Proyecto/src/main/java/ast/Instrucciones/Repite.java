package ast.Instrucciones;

import ast.ASTNode;
import java.util.Map;
import java.util.List;

public class Repite implements ASTNode {

    private ASTNode vecesExpr;
    private List<ASTNode> ordenes;

    public Repite(ASTNode vecesExpr, List<ASTNode> ordenes) {
        this.vecesExpr = vecesExpr;
        this.ordenes = ordenes;
    }

    @Override
    public Object execute(Map<String, Object> symbolTable) {

        Object value = vecesExpr.execute(symbolTable);

        int veces;
        if (value instanceof Integer) {
            veces = (Integer) value;
        } else {
            throw new RuntimeException("REPITE espera un número entero, pero recibió: " + value);
        }

        System.out.println("Repitiendo bloque " + veces + " veces:");

        // Ejecutar el bloque 'veces' cantidad de veces
        for (int i = 0; i < veces; i++) {
            System.out.println("--- Iteración " + (i + 1) + " ---");
            for (ASTNode orden : ordenes) {
                orden.execute(symbolTable);
            }
        }

        System.out.println("Fin de repeticiones");
        return null;
    }
}