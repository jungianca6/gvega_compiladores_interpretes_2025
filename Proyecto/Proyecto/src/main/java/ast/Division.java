package ast;

import java.util.List;
import java.util.Map;

public class Division implements ASTNode {
    private List<ASTNode> operands;

    public Division(List<ASTNode> operands) {
        super();
        this.operands = operands;
    }

    @Override
    public Object execute(Map<String, Object> symbolTable) {
        // Validar que solo hay 2 operandos
        if (operands.size() != 2) {
            throw new RuntimeException("División solo puede operar con exactamente 2 números");
        }

        // Obtener los valores de los operandos
        Object value1 = operands.get(0).execute(symbolTable);
        Object value2 = operands.get(1).execute(symbolTable);

        // Validar que ambos son enteros
        if (!(value1 instanceof Integer) || !(value2 instanceof Integer)) {
            throw new RuntimeException("División solo puede operar con números enteros");
        }

        int dividend = (int) value1;
        int divisor = (int) value2;

        // Validar división por cero
        if (divisor == 0) {
            throw new RuntimeException("Error: División por cero no permitida");
        }

        int result = dividend / divisor;
        System.out.println(result);
        return result;
    }
}