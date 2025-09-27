package ast;
import java.util.Map;
import java.util.List;

public class Suma implements ASTNode {
    private List<ASTNode> operands;

    public Suma(List<ASTNode> operands) {
        super();
        this.operands = operands;
    }

    @Override
    public Object execute(Map<String, Object> symbolTable) {
        int result = 0;
        for (ASTNode operand : operands) {
            Object value = operand.execute(symbolTable);
            if (value instanceof Integer) {
                result += (int) value;
            } else {
                throw new RuntimeException("Suma solo puede operar con números enteros");
            }
        }
        System.out.println(result);
        return result;
    }
}