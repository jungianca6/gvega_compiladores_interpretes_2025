package ast;

import java.util.Map;

public class Pow2Add implements ASTNode {
    private ASTNode operand1;
    private ASTNode operand2;

    public Pow2Add(ASTNode operand1, ASTNode operand2) {
        this.operand1 = operand1;
        this.operand2 = operand2;
    }

    @Override
    public Object execute(Map<String, Object> symbolTable) {
        int x = (int) operand1.execute(symbolTable);
        int y = (int) operand2.execute(symbolTable);
        return x * x + y; // x^2 + y
    }
}
