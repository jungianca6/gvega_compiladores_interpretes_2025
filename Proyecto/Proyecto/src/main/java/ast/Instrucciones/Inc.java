package ast.Instrucciones;

import ast.ASTNode;
import java.util.Map;

public class Inc implements ASTNode {
    private String varName;  // N1
    private ASTNode increment; // N2 (opcional)

    public Inc(String varName, ASTNode increment) {
        this.varName = varName;
        this.increment = increment; // si es null, se incrementa en 1
    }

    @Override
    public Object execute(Map<String, Object> symbolTable) {
        if (!symbolTable.containsKey(varName)) {
            throw new RuntimeException("La variable '" + varName + "' no existe.");
        }

        Object current = symbolTable.get(varName);

        if (!(current instanceof Integer)) {
            throw new RuntimeException("La variable '" + varName + "' no es numérica y no se puede incrementar.");
        }

        int incValue = 1; // valor por defecto
        if (increment != null) {
            Object val = increment.execute(symbolTable);
            if (!(val instanceof Integer)) {
                throw new RuntimeException("El valor de incremento debe ser numérico.");
            }
            incValue = (Integer) val;
        }

        int result = (Integer) current + incValue;
        symbolTable.put(varName, result);

        return result;
    }
}
