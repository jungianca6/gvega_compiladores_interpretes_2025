package ast;

import java.util.List;
import java.util.Map;

public class Menor implements ASTNode {
    private List<ASTNode> operands;

    public Menor(List<ASTNode> operands) {
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
        Object num1 = operands.get(0).execute(symbolTable);
        Object num2 = operands.get(1).execute(symbolTable);

        // Validar que ambos son enteros
        if (!(num1 instanceof Integer) || !(num2 instanceof Integer)) {
            throw new RuntimeException("Potencia solo puede operar con números enteros");
        }

        boolean result;

        result = (int) num1 < (int)  num2;

        System.out.println(result);
        return result;
    }
}