package semantic;

import java.util.*;
import java.util.stream.Collectors;

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

    public void declareOrAssign(String name, ValueType type, Object value) {
        if (!isValidVarName(name)) {
            addError("Error léxico: identificador inválido '" + name + "'.");
            return;
        }

        Symbol existingSymbol = symbols.get(name);
        if (existingSymbol == null) {
            // Nueva declaración
            symbols.put(name, new Symbol(name, type, value));
            atLeastOneVariable = true;
        } else {
            // Asignación a variable existente - verificar tipos
            if (existingSymbol.type != type && type != ValueType.UNDEFINED) {
                addError("Error semántico: intento de asignar " + type +
                        " a variable '" + name + "' de tipo " + existingSymbol.type + ".");
            } else {
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
        // Aquí necesitarías analizar el nodo AST para inferir el tipo
        // Por ahora retornamos un tipo básico
        if (expressionNode instanceof Integer) {
            return ValueType.NUMBER;
        } else if (expressionNode instanceof Boolean) {
            return ValueType.BOOLEAN;
        } else if (expressionNode instanceof String) {
            return ValueType.STRING;
        }
        return ValueType.ANY;
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