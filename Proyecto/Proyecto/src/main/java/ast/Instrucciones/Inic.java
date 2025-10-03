package ast.Instrucciones;

import ast.ASTNode;
import java.util.Map;

public class Inic implements ASTNode {
    private String varName;
    private ASTNode value;  // la expresión cuyo resultado se asignará

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
