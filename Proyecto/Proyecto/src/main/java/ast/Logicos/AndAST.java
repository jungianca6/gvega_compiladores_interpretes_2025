package ast.Logicos;

import ast.ASTNode;

import java.util.List;
import java.util.Map;

public class AndAST implements ASTNode {
    private List<ASTNode> operands;

    public AndAST(List<ASTNode> operands) {
        super();
        this.operands = operands;
    }

    @Override
    public Object execute(Map<String, Object> symbolTable) {
        // Validar que solo hay 2 operandos
        if (operands.size() != 2) {
            throw new RuntimeException("Solo se pueden 2 números");
        }

        // Obtener los valores de los operandos
        Object cond1 = operands.get(0).execute(symbolTable);
        Object cond2 = operands.get(1).execute(symbolTable);

        // Validar que ambos son enteros
        if (!(cond1 instanceof Boolean) || !(cond2 instanceof Boolean)) {
            throw new RuntimeException("Potencia solo puede operar con números enteros");
        }

        boolean result;

        result = (boolean) cond1 && (boolean)  cond2;

        System.out.println(result);
        return result;
    }
}