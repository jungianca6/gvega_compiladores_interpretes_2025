package ast;

import java.util.Map;

public class FibSum implements ASTNode {
    private ASTNode operand1;
    private ASTNode operand2;

    public FibSum(ASTNode operand1, ASTNode operand2) {
        this.operand1 = operand1;
        this.operand2 = operand2;
    }

    @Override
    public Object execute(Map<String, Object> symbolTable) {
        int x = (int) operand1.execute(symbolTable);
        int y = (int) operand2.execute(symbolTable);

        int sum = 0;
        int a = 1, b = 1;
        for (int i = 1; i <= x; i++) {
            sum += a;
            int temp = a + b;
            a = b;
            b = temp;
        }

        return sum * y;
    }
}
