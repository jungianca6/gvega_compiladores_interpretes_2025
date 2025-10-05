package ast.Instrucciones;

import ast.ASTNode;
import java.util.Map;

public class Comment implements ASTNode {

    public Comment() {
        super();
    }

    @Override
    public Object execute(Map<String, Object> symbolTable) {
        return null;
    }

}