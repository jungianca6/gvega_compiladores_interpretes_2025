package ast.Instrucciones;

import java.util.Map;
import java.util.List;
import ast.ASTNode;

public class Mientras implements ASTNode {
    private ASTNode condition;
    private List<ASTNode> body;

    public Mientras(ASTNode condition, List<ASTNode> body) {
        this.condition = condition;
        this.body = body;
    }

    @Override
    public Object execute(Map<String, Object> symbolTable) {
        Object result = null;
        // Evaluar condición antes de ejecutar
        while (true) {
            Object condVal = condition.execute(symbolTable);
            if (!(condVal instanceof Boolean)) {
                throw new RuntimeException("Condición del MIENTRAS debe ser booleana.");
            }
            if (!((Boolean) condVal)) {
                break; // Termina el bucle
            }
            for (ASTNode instr : body) {
                result = instr.execute(symbolTable);
            }
        }
        return result;
    }
}
