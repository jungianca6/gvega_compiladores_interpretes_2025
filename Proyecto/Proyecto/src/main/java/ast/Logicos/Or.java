package ast.Logicos;

import ast.ASTNode;

import java.util.Map;

public class Or implements ASTNode {
    private ASTNode left;
    private ASTNode right;

    public Or(ASTNode left, ASTNode right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public Object execute(Map<String, Object> symbolTable) {
        return (boolean)left.execute(symbolTable) || (boolean)right.execute(symbolTable);
    }
}
