package ast.Instrucciones;

import ast.ASTNode;
import java.util.Map;
import java.util.List;

public class HazMientras implements ASTNode {
    private final List<ASTNode> body;
    private final ASTNode condition;

    public HazMientras(List<ASTNode> body, ASTNode condition) {
        this.body = body;
        this.condition = condition;
    }

    @Override
    public Object execute(Map<String, Object> symbolTable) {
        if (condition == null) {
            throw new RuntimeException("HAZ.MIENTRAS: falta la condicion.");
        }

        Object last = null;
        do {
            for (ASTNode instr : body) {
                last = instr.execute(symbolTable);
            }

            Object condVal = condition.execute(symbolTable);
            if (!(condVal instanceof Boolean)) {
                throw new RuntimeException("HAZ.MIENTRAS: la condicion debe ser booleana.");
            }
            if (!((Boolean) condVal)) break;
            // si es true, se repite (do-while)
        } while (true);

        return last;
    }
}
