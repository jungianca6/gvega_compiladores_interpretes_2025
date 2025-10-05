import java.util.*;

public class SymbolTable {
    private Deque<Map<String, Symbol>> scopes;
    private int currentScopeLevel;

    public SymbolTable() {
        scopes = new ArrayDeque<>();
        currentScopeLevel = 0;
        enterScope(); // Scope global
    }

    public void enterScope() {
        scopes.push(new HashMap<>());
        currentScopeLevel++;
    }

    public void exitScope() {
        if (!scopes.isEmpty()) {
            scopes.pop();
            currentScopeLevel--;
        }
    }

    public boolean insert(String name, String type, int line) {
        if (scopes.isEmpty()) {
            return false;
        }

        Map<String, Symbol> currentScope = scopes.peek();
        if (currentScope.containsKey(name)) {
            return false; // Variable ya declarada en este ámbito
        }

        currentScope.put(name, new Symbol(name, type, line));
        return true;
    }

    public Symbol lookup(String name) {
        for (Map<String, Symbol> scope : scopes) {
            if (scope.containsKey(name)) {
                return scope.get(name);
            }
        }
        return null; // Variable no encontrada
    }

    public Symbol lookupCurrentScope(String name) {
        if (scopes.isEmpty()) {
            return null;
        }
        return scopes.peek().get(name);
    }

    public int getCurrentScopeLevel() {
        return currentScopeLevel;
    }
}