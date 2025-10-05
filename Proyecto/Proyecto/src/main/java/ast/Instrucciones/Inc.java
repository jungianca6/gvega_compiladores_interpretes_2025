package ast.Instrucciones;

import ast.ASTNode;
import java.util.Map;

public class Inc implements ASTNode {
    private String varName;
    private ASTNode times; // Puede ser null si solo es "INC [N1]"

    public Inc(String varName, ASTNode times) {
        this.varName = varName;
        this.times = times;
    }

    public ASTNode getTimes() {
        return times;
    }

    public void setTimes(ASTNode times) {
        this.times = times;
    }

    public String getVarName() {
        return varName;
    }

    public void setVarName(String varName) {
        this.varName = varName;
    }

    @Override
    public Object execute(Map<String, Object> symbolTable) {
        // Verificar que la variable exista y sea numérica
        Object currentValue = symbolTable.get(varName);
        if (currentValue == null) {
            throw new RuntimeException("La variable '" + varName + "' no existe.");
        }
        if (!(currentValue instanceof Number)) {
            throw new RuntimeException("La variable '" + varName + "' no es numérica.");
        }

        int increment = 1; // valor por defecto
        if (times != null) {
            Object t = times.execute(symbolTable);
            if (t instanceof Number) {
                increment = ((Number) t).intValue();
            } else {
                throw new RuntimeException("El valor de incremento debe ser numérico.");
            }
        }

        int newValue = ((Number) currentValue).intValue() + increment;
        symbolTable.put(varName, newValue);
        return newValue;
    }
}
