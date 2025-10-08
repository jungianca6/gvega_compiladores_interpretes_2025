package ast.Instrucciones;

import ast.ASTNode;

import java.util.Map;

public class VarRef implements ASTNode {
    private static String name;

    public static String getName() {
        return name;
    }

    public void setName(String name) {
        VarRef.name = name;
    }

    public VarRef(String name) {
        super();
        VarRef.name = name;
    }

    @Override
    public Object execute(Map<String, Object> symbolTable) {

        return symbolTable.get(name);
    }
}