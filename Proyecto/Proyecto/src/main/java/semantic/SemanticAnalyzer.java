package semantic;

import java.util.*;
import java.util.stream.Collectors;
import ast.Instrucciones.*;
import ast.Aritmeticos.*;
import ast.Logicos.*;

public class SemanticAnalyzer {
    private List<String> errors = new ArrayList<>();
    private Map<String, Symbol> symbols = new HashMap<>();
    private boolean firstLineHasComment = false;
    private boolean atLeastOneVariable = false;
    private Set<String> declaredFunctions = new HashSet<>();
    private Set<String> calledFunctions = new HashSet<>();

    // Tipos soportados en el lenguaje
    public enum ValueType {
        NUMBER, BOOLEAN, STRING, TURTLE, UNDEFINED, ANY
    }

    // Clase Symbol para análisis semántico
    public static class Symbol {
        public String name;
        public ValueType type;
        public Object value;
        public boolean initialized;

        public Symbol(String name, ValueType type, Object value) {
            this.name = name;
            this.type = type;
            this.value = value;
            this.initialized = (value != null);
        }
    }

    // ========== MÉTODOS PÚBLICOS PRINCIPALES ==========

    public void analyzeProgram() {  // ← QUITA el parámetro List<Object> instructions
        // Verificar restricciones del programa
        ensureProgramConstraints();

        // Verificar funciones llamadas pero no declaradas
        checkUndefinedFunctions();
    }

    public void markFirstLineHasComment() {
        this.firstLineHasComment = true;
    }

    public void declareFunction(String functionName) {
        declaredFunctions.add(functionName);
    }

    public void callFunction(String functionName) {
        calledFunctions.add(functionName);
    }

    public void declareOrAssign(String name, ValueType type, Object value, boolean isHazAssignment) {
        if (!isValidVarName(name)) {
            addError("Error léxico: identificador inválido '" + name + "'.");
            return;
        }

        Symbol existingSymbol = symbols.get(name);
        if (existingSymbol == null) {
            // ✅ NUEVA declaración con HAZ - establecer el tipo definitivo
            symbols.put(name, new Symbol(name, type, value));
            atLeastOneVariable = true;
            System.out.println("DEBUG: Variable '" + name + "' creada con tipo: " + type);
        } else {
            // ✅ ASIGNACIÓN a variable existente - verificar compatibilidad de tipos
            if (existingSymbol.type != type) {
                addError("Error semántico: intento de asignar " + type +
                        " a variable '" + name + "' de tipo " + existingSymbol.type + ".");
            } else {
                // Tipos compatibles - actualizar valor
                existingSymbol.value = value;
                existingSymbol.initialized = true;
            }
        }
    }

    public ValueType getVariableType(String name) {
        Symbol symbol = symbols.get(name);
        return symbol != null ? symbol.type : ValueType.UNDEFINED;
    }

    public boolean variableExists(String name) {
        return symbols.containsKey(name);
    }

    public boolean isVariableInitialized(String name) {
        Symbol symbol = symbols.get(name);
        return symbol != null && symbol.initialized;
    }

    // ========== VERIFICACIONES SEMÁNTICAS ==========

    private void ensureProgramConstraints() {
        if (!firstLineHasComment) {
            addError("Error: debe existir al menos un comentario en la primera línea del programa.");
        }
        if (!atLeastOneVariable) {
            addError("Error: el programa debe definir al menos una variable con 'Haz' o 'INIC'.");
        }
    }

    private void checkUndefinedFunctions() {
        for (String calledFunc : calledFunctions) {
            if (!declaredFunctions.contains(calledFunc)) {
                addError("Error semántico: función '" + calledFunc + "' no está definida.");
            }
        }
    }

    // ========== VERIFICACIÓN DE TIPOS ==========

    public ValueType inferExpressionType(Object expressionNode) {
        if (expressionNode == null) {
            return ValueType.UNDEFINED;
        }

        // Si es un nodo AST Constant, inspeccionar su valor
        if (expressionNode instanceof ast.Instrucciones.Constant) {
            try {
                java.lang.reflect.Method getValueMethod = expressionNode.getClass().getMethod("getValue");
                Object value = getValueMethod.invoke(expressionNode);

                if (value instanceof Integer) return ValueType.NUMBER;
                if (value instanceof Boolean) return ValueType.BOOLEAN;
                if (value instanceof String) return ValueType.STRING;

            } catch (Exception e) {
                // Si no puede obtener el valor, usar lógica de respaldo
                System.out.println("DEBUG: No se pudo obtener valor de Constant, usando respaldo");
            }
        }

        // Lógica de respaldo basada en el tipo de nodo AST
        String className = expressionNode.getClass().getSimpleName();
        switch (className) {
            case "Constant":
                // Por contexto, asumir NUMBER (es lo más común)
                return ValueType.NUMBER;
            case "VarRef":
                // Para referencias a variables, obtener el tipo de la tabla
                //ast.Instrucciones.VarRef= (ast.Instrucciones.VarRef) expressionNode;
                ast.Instrucciones.VarRef varRef = (ast.Instrucciones.VarRef) expressionNode;
                return getVariableType(ast.Instrucciones.VarRef.getName());
            default:
                return ValueType.ANY;
        }
    }

    public void updateVariableType(String name, ValueType newType) {
        Symbol symbol = symbols.get(name);
        if (symbol != null) {
            symbol.type = newType;
            System.out.println("DEBUG: Variable '" + name + "' actualizada a tipo: " + newType);
        }
    }
    public boolean areTypesCompatible(ValueType type1, ValueType type2) {
        if (type1 == ValueType.ANY || type2 == ValueType.ANY) return true;
        if (type1 == ValueType.UNDEFINED || type2 == ValueType.UNDEFINED) return false;
        return type1 == type2;
    }

    // ========== VALIDACIÓN DE IDENTIFICADORES ==========

    public boolean isValidVarName(String id) {
        if (id == null || id.length() == 0 || id.length() > 10) return false;
        if (!Character.isLowerCase(id.charAt(0))) return false;
        for (char c : id.toCharArray()) {
            if (!(Character.isLetterOrDigit(c) || c == '_' || c == '&' || c == '@')) return false;
        }
        return true;
    }

    // ========== MANEJO DE ERRORES ==========

    public void addError(String error) {
        errors.add(error);
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public List<String> getErrors() {
        return new ArrayList<>(errors);
    }

    public void throwIfErrors() {
        if (hasErrors()) {
            throw new RuntimeException("Errores semánticos:\n" +
                    errors.stream().collect(Collectors.joining("\n")));
        }
    }

    // ========== INFORMACIÓN DE DEPURACIÓN ==========

    public void printSymbolTable() {
        System.out.println("=== TABLA DE SÍMBOLOS ===");
        for (Symbol symbol : symbols.values()) {
            System.out.println(symbol.name + " : " + symbol.type +
                    " = " + symbol.value + " (inicializada: " + symbol.initialized + ")");
        }
    }

    public int getSymbolCount() {
        return symbols.size();
    }

    public void printDebugInfo() {
        System.out.println("=== DEBUG SEMÁNTICO ===");
        System.out.println("firstLineHasComment: " + firstLineHasComment);
        System.out.println("atLeastOneVariable: " + atLeastOneVariable);
        System.out.println("Variables en tabla: " + symbols.size());
        symbols.forEach((name, symbol) -> {
            System.out.println("  " + name + " : " + symbol.type);
        });
    }
}