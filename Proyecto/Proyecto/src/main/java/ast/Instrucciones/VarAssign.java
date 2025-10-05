package ast.Instrucciones;

import ast.ASTNode;

import java.util.Map;

public class VarAssign implements ASTNode {
    private String name;
    private ASTNode expression;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ASTNode getExpression() {
        return expression;
    }

    public void setExpression(ASTNode expression) {
        this.expression = expression;
    }

    public VarAssign(String name, ASTNode expression) {
        super();
        this.name = name;
        this.expression = expression;
    }

    @Override
    public Object execute(Map<String, Object> symbolTable) {
        symbolTable.put(name, expression.execute(symbolTable));
        return null;
    }
}