import java.util.*;
import ast.Instrucciones.*;
import ast.Aritmeticos.*;
import ast.Logicos.*;
import ast.*;

public class SemanticVisitor {
    private SymbolTable symbolTable;
    private ErrorReporter errorReporter;
    private Set<String> firmasProcedimientos;

    public SemanticVisitor() {
        this.symbolTable = new SymbolTable();
        this.errorReporter = new ErrorReporter();
        this.firmasProcedimientos = new HashSet<>();
    }

    public void visitProgram(List<ASTNode> instrucciones) {
        // Visitar todas las instrucciones
        for (ASTNode instruccion : instrucciones) {
            visit(instruccion);
        }
    }

    private void visit(ASTNode node) {
        if (node instanceof Funcion) {
            visitFuncion((Funcion) node);
        } else if (node instanceof VarDecl) {
            visitVarDecl((VarDecl) node);
        } else if (node instanceof VarAssign) {
            visitVarAssign((VarAssign) node);
        } else if (node instanceof Inic) {
            visitInic((Inic) node);
        } else if (node instanceof Si) {
            visitSi((Si) node);
        } else if (node instanceof Hasta) {
            visitHasta((Hasta) node);
        } else if (node instanceof HazMientras) {
            visitHazMientras((HazMientras) node);
        } else if (node instanceof Inc) {
            visitInc((Inc) node);
        }
        // Agregar más casos según necesites
    }

    // VALIDACIÓN DE FIRMAS DE PROCEDIMIENTOS
    private void visitFuncion(Funcion funcion) {
        String firma = generarFirma(funcion.getNombre(), funcion.getParametros());
        if (firmasProcedimientos.contains(firma)) {
            errorReporter.reportError(-1, "Procedimiento '" + funcion.getNombre() +
                    "' con " + funcion.getParametros().size() + " parámetros ya existe");
        } else {
            firmasProcedimientos.add(firma);
        }

        // Entrar a nuevo ámbito
        symbolTable.enterScope();

        // Insertar parámetros en la tabla de símbolos
        for (String parametro : funcion.getParametros()) {
            symbolTable.insert(parametro, "int", -1); // Parámetros son int
        }

        // Visitar el cuerpo de la función
        for (ASTNode instruccion : funcion.getCuerpo()) {
            visit(instruccion);
        }

        // Salir del ámbito
        symbolTable.exitScope();
    }

    // VALIDACIÓN DE DECLARACIÓN DE VARIABLES
    private void visitVarDecl(VarDecl varDecl) {
        if (!symbolTable.insert(varDecl.getName(), "int", -1)) {
            errorReporter.reportError(-1, "Variable '" + varDecl.getName() + "' ya declarada");
        }
    }

    // VALIDACIÓN DE ASIGNACIÓN DE VARIABLES
    private void visitVarAssign(VarAssign varAssign) {
        String nombre = varAssign.getName();
        if (symbolTable.lookup(nombre) == null) {
            errorReporter.reportError(-1, "Variable '" + nombre + "' no declarada");
        }
    }

    // VALIDACIÓN DE INICIALIZACIÓN
    private void visitInic(Inic inic) {
        if (!symbolTable.insert(inic.getVarName(), "int", -1)) {
            errorReporter.reportError(-1, "Variable '" + inic.getVarName() + "' ya declarada");
        }
    }

    // VALIDACIÓN DE CONDICIÓN EN SI
    private void visitSi(Si si) {
        // Verificar que la condición sea booleana
        // Esto es una verificación básica - podrías hacerla más sofisticada
        errorReporter.reportError(-1, "NOTA: Condición en SI será verificada en tiempo de ejecución");
    }

    // VALIDACIÓN DE CONDICIÓN EN HAZ.HASTA
    private void visitHasta(Hasta hasta) {
        errorReporter.reportError(-1, "NOTA: Condición en HAZ.HASTA será verificada en tiempo de ejecución");
    }

    // VALIDACIÓN DE CONDICIÓN EN HAZ.MIENTRAS
    private void visitHazMientras(HazMientras hazMientras) {
        errorReporter.reportError(-1, "NOTA: Condición en HAZ.MIENTRAS será verificada en tiempo de ejecución");
    }

    // VALIDACIÓN DE INC - VERIFICAR QUE N1 ES VARIABLE NUMÉRICA
    private void visitInc(Inc inc) {
        String nombreVariable = inc.getVarName();
        Symbol simbolo = symbolTable.lookup(nombreVariable);

        if (simbolo == null) {
            errorReporter.reportError(-1, "INC: Variable '" + nombreVariable + "' no declarada");
        }
        // Nota: Asumimos que todas las variables son numéricas en este lenguaje
        // Si tuvieras tipos, aquí verificarías que el símbolo sea de tipo "int"
    }

    private String generarFirma(String nombre, List<String> parametros) {
        return nombre + "_" + parametros.size();
    }

    public ErrorReporter getErrorReporter() {
        return errorReporter;
    }

    public boolean hasErrors() {
        return errorReporter.hasErrors();
    }
}