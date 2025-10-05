package ast.Instrucciones;

import ast.ASTNode;

import java.util.List;
import java.util.Map;

public class Hasta implements ASTNode {
    private ASTNode condition;
    private List<ASTNode> body;

    public Hasta(ASTNode condition, List<ASTNode> body) {
        this.condition = condition;
        this.body = body;
    }

    public ASTNode getCondition() {
        return condition;
    }

    public List<ASTNode> getBody() {
        return body;
    }

    @Override
    public Object execute(Map<String, Object> symbolTable) {
        // Execute the loop while condition is true
        while (true) {
            Object conditionResult = condition.execute(symbolTable);
            if (conditionResult instanceof Boolean && !(Boolean) conditionResult) {
                break;
            }

            // Execute all instructions in the body
            for (ASTNode instruction : body) {
                instruction.execute(symbolTable);
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "Hasta{" +
                "condition=" + condition +
                ", body=" + body +
                '}';
    }
}
