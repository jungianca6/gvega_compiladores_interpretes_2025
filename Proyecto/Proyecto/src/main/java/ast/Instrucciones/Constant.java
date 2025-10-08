package ast.Instrucciones;

import ast.ASTNode;

import java.util.Map;

public class Constant implements ASTNode {
    private Object value;

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Constant(Object value) {
        super();
        this.value = value;
    }
    @Override
    public Object execute(Map<String, Object> symbolTable) {
        return value;
    }
}