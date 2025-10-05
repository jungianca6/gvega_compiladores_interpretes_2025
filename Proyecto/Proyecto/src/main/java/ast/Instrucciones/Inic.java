package ast.Instrucciones;

import ast.ASTNode;
import java.util.Map;

public class Inic implements ASTNode {
    private String varName;
    private ASTNode value;  // la expresión cuyo resultado se asignará

    public ASTNode getValue() {
        return value;
    }

    public void setValue(ASTNode value) {
        this.value = value;
    }

    public String getVarName() {
        return varName;
    }

    public void setVarName(String varName) {
        this.varName = varName;
    }

    public Inic(String varName, ASTNode value) {
        this.varName = varName;
        this.value = value;
    }

    @Override
    public Object execute(Map<String, Object> symbolTable) {
        // Evaluar la expresión
        Object result = value.execute(symbolTable);

        // Guardar o actualizar la variable en la tabla de símbolos
        symbolTable.put(varName, result);

        return result;
    }
}
