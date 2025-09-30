package ast;

import java.util.List;
import java.util.Map;

public class Producto implements ASTNode {
    private List<ASTNode> operands;

    public Producto(List<ASTNode> operands) {
        super();
        this.operands = operands;
    }

    @Override
    public Object execute(Map<String, Object> symbolTable) {
        if (operands.isEmpty()) {
            return 0;
        }
        int result = 0;
        boolean first = true;

        for (ASTNode operand : operands) {
            Object value = operand.execute(symbolTable);
            if (value instanceof Integer) {
                if (first) {
                    result = (int) value;  // Primer operando se asigna directamente
                    first = false;
                } else {
                    result *= (int) value;  // Los siguientes se multiplican
                }
            } else {
                throw new RuntimeException("Producto solo puede operar con números enteros");
            }
        }
        System.out.println(result);
        return result;
    }
}