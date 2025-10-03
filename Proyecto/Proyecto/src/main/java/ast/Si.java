package ast;
import java.util.List;
import java.util.Map;

public class Si implements ASTNode {
    private ASTNode condition;
    private List<ASTNode> ifBody;      // Instrucciones si se cumple la condición
    private List<ASTNode> elseBody;    // Instrucciones si NO se cumple la condición (opcional)

    public Si(ASTNode condition, List<ASTNode> ifBody, List<ASTNode> elseBody) {
        super();
        this.condition = condition;
        this.ifBody = ifBody;
        this.elseBody = elseBody;
    }

    @Override
    public Object execute(Map<String, Object> symbolTable) {
        // Evaluar la condición
        Object conditionResult = condition.execute(symbolTable);

        // Verificar que la condición sea booleana
        if (!(conditionResult instanceof Boolean)) {
            throw new RuntimeException("La condición en SI debe evaluar a un valor booleano");
        }

        boolean conditionValue = (Boolean) conditionResult;

        if (conditionValue) {
            // Ejecutar el cuerpo del SI (primera lista de instrucciones)
            for (ASTNode instruction : ifBody) {
                instruction.execute(symbolTable);
            }
        } else if (!elseBody.isEmpty()) {
            // Ejecutar el cuerpo del "sino" (segunda lista de instrucciones)
            for (ASTNode instruction : elseBody) {
                instruction.execute(symbolTable);
            }
        }

        return null;
    }
}