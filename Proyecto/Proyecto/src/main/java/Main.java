import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import parser.*;
import semantic.*;

import java.io.IOException;


public class Main {
    private static final String EXTENSION = "smp";

    public static void main(String[] args) throws IOException {

        String program = args.length > 1 ? args[1] : "./Proyecto/Proyecto/test/test." + EXTENSION;

        System.out.println("Interpretando archivo " + program);

        // 1. ANÁLISIS LÉXICO

        FrontEndLexer lexer = new FrontEndLexer(new ANTLRFileStream(program));
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        // 2. ANÁLISIS SINTÁCTICO
        FrontEndParser parser = new FrontEndParser(tokens);
        FrontEndParser.ProgramContext tree = parser.program();

        // 3. ANÁLISIS SEMÁNTICO (NUEVO)
        System.out.println("\n=== ANÁLISIS SEMÁNTICO ===");
        SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer();

        try {
            // CONFIGURAR el analyzer en el parser
            parser.setSemanticAnalyzer(semanticAnalyzer);

            // REALIZAR análisis semántico con el token stream
            SemanticAnalyzerVisitor semanticVisitor = new SemanticAnalyzerVisitor(semanticAnalyzer, tokens);
            semanticVisitor.visit(tree);

            if (semanticAnalyzer.hasErrors()) {
                System.out.println("❌ Se encontraron errores semánticos:");
                for (String error : semanticAnalyzer.getErrors()) {
                    System.out.println("   - " + error);
                }
                System.out.println("⏹️  Ejecución detenida debido a errores semánticos");
                semanticAnalyzer.printDebugInfo();
                return;

            } else {
                System.out.println("✅ Análisis semántico pasado sin errores");
                System.out.println("✅ Variables declaradas: " + semanticAnalyzer.getSymbolCount());
                System.out.println("✅ Comentario en primera línea: ✓");
                System.out.println("✅ Al menos una variable: " + (semanticAnalyzer.getSymbolCount() > 0 ? "✓" : "✗"));
            }

        } catch (RuntimeException e) {
            System.out.println("❌ Error durante análisis semántico: " + e.getMessage());

            return;
        }

        semanticAnalyzer.printDebugInfo();
        // 4. EJECUCIÓN (solo si pasa el análisis semántico)
        System.out.println("\n=== EJECUCIÓN ===");
        FrontEndBaseVisitor visitor = new FrontEndBaseVisitor();
        visitor.visit(tree);

        System.out.println("\nInterpretacion terminada");

    }
}