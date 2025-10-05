package ast.Instrucciones;

import ast.ASTNode;

import java.util.Map;

public class VarDecl implements ASTNode {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public VarDecl(String name) {
        super();
        this.name = name;
    }

    @Override
    public Object execute(Map<String, Object> symbolTable) {
        symbolTable.put(name, new Object());
        return null;
    }
}