package ast;

import java.util.List;
import java.util.Map;

public class Potencia implements ASTNode {
    private List<ASTNode> operands;

    public Potencia(List<ASTNode> operands) {
        super();
        this.operands = operands;
    }

    @Override
    public Object execute(Map<String, Object> symbolTable) {
        // Validar que solo hay 2 operandos
        if (operands.size() != 2) {
            throw new RuntimeException("Potencia solo puede operar con exactamente 2 números");
        }

        // Obtener los valores de los operandos
        Object value1 = operands.get(0).execute(symbolTable);
        Object value2 = operands.get(1).execute(symbolTable);

        // Validar que ambos son enteros
        if (!(value1 instanceof Integer) || !(value2 instanceof Integer)) {
            throw new RuntimeException("Potencia solo puede operar con números enteros");
        }

        int base = (int) value1;
        int exponente = (int) value2;
        int result = 1;

        // Multiplica la base por sí misma tantas veces como indique el exponente
        for (int i = 0; i < exponente; i++) {
            result = result * base;
        }

        System.out.println(result);
        return result;
    }
}