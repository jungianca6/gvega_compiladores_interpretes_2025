package semantic;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import parser.FrontEndParser;
import parser.*;

import java.util.List;

public class SemanticAnalyzerVisitor extends FrontEndBaseVisitor<Void> {
    private SemanticAnalyzer analyzer;
    private CommonTokenStream tokens;

    public SemanticAnalyzerVisitor(SemanticAnalyzer analyzer, CommonTokenStream tokens) {
        this.analyzer = analyzer;
        this.tokens = tokens;
    }

    @Override
    public Void visitProgram(FrontEndParser.ProgramContext ctx) {
        // Verificar comentario en primera línea
        checkFirstLineComment();

        // Visitar todas las instrucciones
        visitChildren(ctx);

        // Realizar análisis final
        analyzer.analyzeProgram();
        return null;
    }

    private void checkFirstLineComment() {
        // Obtener TODOS los tokens del stream
        List<Token> allTokens = tokens.getTokens();

        if (allTokens.isEmpty()) return;

        // Buscar el primer token que no sea WS (espacios en blanco)
        Token firstMeaningfulToken = null;
        for (Token token : allTokens) {
            if (token.getChannel() != Token.HIDDEN_CHANNEL &&
                    token.getType() != FrontEndLexer.WS) {
                firstMeaningfulToken = token;
                break;
            }
        }

        if (firstMeaningfulToken == null) return;

        int firstLine = firstMeaningfulToken.getLine();

        // Buscar comentarios en la primera línea
        for (Token token : allTokens) {
            if (token.getLine() == firstLine) {
                int tokenType = token.getType();
                if (tokenType == FrontEndLexer.LINE_COMMENT ||
                        tokenType == FrontEndLexer.COMMENT) {
                    analyzer.markFirstLineHasComment();
                    return;
                }
            }
        }
    }

    @Override
    public Void visitVar_decl(FrontEndParser.Var_declContext ctx) {
        String varName = ctx.ID().getText();
        analyzer.declareOrAssign(varName, SemanticAnalyzer.ValueType.UNDEFINED,
                null,false);
        return visitChildren(ctx);
    }

    @Override
    public Void visitInic(FrontEndParser.InicContext ctx) {
        String varName = ctx.ID().getText();
        // Inferir tipo de la expresión (simplificado - asumimos NUMBER por ahora)
        analyzer.declareOrAssign(varName, SemanticAnalyzer.ValueType.NUMBER,
                null,true);
        return visitChildren(ctx);
    }

    @Override
    public Void visitVar_assign(FrontEndParser.Var_assignContext ctx) {
        String varName = ctx.ID().getText();

        if (!analyzer.variableExists(varName)) {
            analyzer.addError("Variable no declarada: '" + varName + "'");
        } else {
            // ✅ IMPORTANTE: Actualizar el tipo basado en la expresión asignada
            SemanticAnalyzer.ValueType expressionType = analyzer.inferExpressionType(ctx.expression().node);
            SemanticAnalyzer.ValueType currentType = analyzer.getVariableType(varName);

            // Verificar compatibilidad de tipos
            if (currentType != SemanticAnalyzer.ValueType.UNDEFINED &&
                    currentType != expressionType) {
                analyzer.addError("Error semántico: intento de asignar " + expressionType +
                        " a variable '" + varName + "' de tipo " + currentType + ".");
            } else if (currentType == SemanticAnalyzer.ValueType.UNDEFINED) {
                // Si la variable era UNDEFINED, actualizar al nuevo tipo
                analyzer.updateVariableType(varName, expressionType);
            }
        }

        return visitChildren(ctx);
    }

    @Override
    public Void visitFuncion(FrontEndParser.FuncionContext ctx) {
        String funcName = ctx.name.getText();
        analyzer.declareFunction(funcName);
        // ✅ IMPORTANTE: Visitar el cuerpo de la función para analizar sus variables
        if (ctx.instrucciones() != null) {
            for (FrontEndParser.InstruccionesContext instr : ctx.instrucciones()) {
                visit(instr);
            }
        }
        return null;
    }

    @Override
    public Void visitLlamadaFuncion(FrontEndParser.LlamadaFuncionContext ctx) {
        String funcName = ctx.name.getText();
        analyzer.callFunction(funcName);
        return visitChildren(ctx);
    }

    @Override
    public Void visitComment(FrontEndParser.CommentContext ctx) {
        // Los comentarios ya se manejan en checkFirstLineComment
        return null;
    }

    @Override
    public Void visitInc(FrontEndParser.IncContext ctx) {
        String varName = ctx.id.getText();

        // (1) Verificar que la variable exista
        if (!analyzer.variableExists(varName)) {
            analyzer.addError("Error semántico: variable '" + varName + "' usada en inc no está declarada.");
            return null;
        }

        // Obtener tipo actual de la variable
        SemanticAnalyzer.ValueType varType = analyzer.getVariableType(varName);

        // (2) Debe ser numérica
        if (varType != SemanticAnalyzer.ValueType.NUMBER &&
                varType != SemanticAnalyzer.ValueType.UNDEFINED) {

            analyzer.addError("Error semántico: la variable '" + varName +
                    "' usada en inc debe ser numérica.");
            return null;
        }

        // (3) Si la variable era UNDEFINED, actualizarla a NUMBER
        if (varType == SemanticAnalyzer.ValueType.UNDEFINED) {
            analyzer.updateVariableType(varName, SemanticAnalyzer.ValueType.NUMBER);
        }

        // (4) Si existe segundo parámetro, verificar su tipo
        if (ctx.val != null) {
            SemanticAnalyzer.ValueType valType = analyzer.inferExpressionType(ctx.val.node);

            if (valType == SemanticAnalyzer.ValueType.UNDEFINED) {
                analyzer.addError("Error semántico: no se puede usar una expresión sin tipo definido "
                        + "como incremento en inc[" + varName + " N2].");
            } else if (valType != SemanticAnalyzer.ValueType.NUMBER) {
                analyzer.addError("Error semántico: el valor en inc[" + varName +
                        " N2] debe ser numérico.");
            }
        }

        return visitChildren(ctx);
    }
}