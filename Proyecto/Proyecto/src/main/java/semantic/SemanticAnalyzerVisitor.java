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
        analyzer.declareOrAssign(varName, SemanticAnalyzer.ValueType.UNDEFINED, null);
        return visitChildren(ctx);
    }

    @Override
    public Void visitInic(FrontEndParser.InicContext ctx) {
        String varName = ctx.ID().getText();
        // Inferir tipo de la expresión (simplificado - asumimos NUMBER por ahora)
        analyzer.declareOrAssign(varName, SemanticAnalyzer.ValueType.NUMBER, null);
        return visitChildren(ctx);
    }

    @Override
    public Void visitVar_assign(FrontEndParser.Var_assignContext ctx) {
        String varName = ctx.ID().getText();
        if (!analyzer.variableExists(varName)) {
            analyzer.addError("Variable no declarada: '" + varName + "'");
        }
        return visitChildren(ctx);
    }

    @Override
    public Void visitFuncion(FrontEndParser.FuncionContext ctx) {
        String funcName = ctx.name.getText();
        analyzer.declareFunction(funcName);
        return visitChildren(ctx);
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
}