package ast.Instrucciones;

import ast.ASTNode;

import java.util.Map;

public class VarRef implements ASTNode {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public VarRef(String name) {
        super();
        this.name = name;
    }

    @Override
    public Object execute(Map<String, Object> symbolTable) {

        return symbolTable.get(name);
    }
}