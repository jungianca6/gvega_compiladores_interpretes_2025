import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import parser.*;
import semantic.*;
import ir.*;
import optimizer.*;
import codegen.*;
import backend.*;
import ast.ASTNode;

import java.io.IOException;
import java.util.List;

public class Compiler {

    public static void compile(String inputFile, String outputDir) throws IOException {
        System.out.println("\n=== INICIANDO COMPILACIÓN ===");
        System.out.println("Archivo de entrada: " + inputFile);

        // 1. ANÁLISIS LÉXICO
        System.out.println("\n[1/7] Análisis Léxico...");
        FrontEndLexer lexer = new FrontEndLexer(new ANTLRFileStream(inputFile));
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        // 2. ANÁLISIS SINTÁCTICO
        System.out.println("[2/7] Análisis Sintáctico...");
        FrontEndParser parser = new FrontEndParser(tokens);

        // Enable program body capture mode
        parser.captureProgramBody = true;

        FrontEndParser.ProgramContext tree = parser.program();

        if (parser.getNumberOfSyntaxErrors() > 0) {
            System.out.println("❌ Errores sintácticos encontrados. Compilación abortada.");
            return;
        }

        // 3. ANÁLISIS SEMÁNTICO
        System.out.println("[3/7] Análisis Semántico...");
        SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer();
        parser.setSemanticAnalyzer(semanticAnalyzer);

        SemanticAnalyzerVisitor semanticVisitor = new SemanticAnalyzerVisitor(semanticAnalyzer, tokens);
        semanticVisitor.visit(tree);

        if (semanticAnalyzer.hasErrors()) {
            System.out.println("❌ Errores semánticos encontrados:");
            for (String error : semanticAnalyzer.getErrors()) {
                System.out.println("   - " + error);
            }
            System.out.println("Compilación abortada.");
            return;
        }

        System.out.println("✅ Análisis semántico completado sin errores");

        // Get captured AST
        List<ASTNode> programBody = parser.lastProgramBody;
        if (programBody == null || programBody.isEmpty()) {
            System.out.println("❌ No se capturó el cuerpo del programa.");
            return;
        }

        // 4. GENERACIÓN DE IR (TAC)
        System.out.println("[4/7] Generando Representación Intermedia (TAC)...");
        IRBuilder irBuilder = new IRBuilder();
        IR ir = irBuilder.build(programBody);

        int instrCountBefore = ir.getTotalInstructions();
        System.out.println("✅ IR generado: " + instrCountBefore + " instrucciones");

        // Save IR to file
        ObjectWriter writer = new ObjectWriter();
        writer.writeIR(ir.toString(), outputDir + "/out.ir");
        System.out.println("   → Guardado en: " + outputDir + "/out.ir");

        // 5. OPTIMIZACIÓN
        System.out.println("[5/7] Aplicando optimizaciones...");

        // Pass 1: Constant Folding
        ConstantFolder folder = new ConstantFolder();
        ir = folder.run(ir);
        int instrAfterFolding = ir.getTotalInstructions();
        System.out.println("   Pasada 1 - Constant Folding:");
        System.out.println("     • Constantes plegadas: " + folder.getFoldedCount());
        System.out.println("     • Instrucciones: " + instrAfterFolding);

        // Pass 2: Peephole Optimization
        Peephole peephole = new Peephole();
        ir = peephole.run(ir);
        int instrAfterPeephole = ir.getTotalInstructions();
        System.out.println("   Pasada 2 - Peephole:");
        System.out.println("     • Patrones optimizados: " + peephole.getOptimizedCount());
        System.out.println("     • Instrucciones: " + instrAfterPeephole);

        // Pass 3: Dead Code Elimination
        DeadCodeElim dce = new DeadCodeElim();
        ir = dce.run(ir);
        int instrAfterDCE = ir.getTotalInstructions();
        System.out.println("   Pasada 3 - Dead Code Elimination:");
        System.out.println("     • Código muerto eliminado: " + dce.getEliminatedCount());
        System.out.println("     • Instrucciones finales: " + instrAfterDCE);

        int totalReduction = instrCountBefore - instrAfterDCE;
        double reductionPercent = instrCountBefore > 0 ?
            (totalReduction * 100.0 / instrCountBefore) : 0;

        System.out.println("\n   📊 Resumen de optimización:");
        System.out.println("     • Instrucciones originales: " + instrCountBefore);
        System.out.println("     • Instrucciones optimizadas: " + instrAfterDCE);
        System.out.println("     • Reducción: " + totalReduction + " instrucciones (" +
                          String.format("%.1f%%", reductionPercent) + ")");

        // 6. GENERACIÓN DE CÓDIGO ASM
        System.out.println("\n[6/7] Generando código ensamblador...");
        SimpleAsmGenerator asmGen = new SimpleAsmGenerator();
        List<String> asmCode = asmGen.generate(ir);

        writer.writeAsm(asmCode, outputDir + "/out.asm");
        System.out.println("✅ Código ASM generado: " + asmCode.size() + " líneas");
        System.out.println("   → Guardado en: " + outputDir + "/out.asm");

        // 7. GENERACIÓN DE ARCHIVO OBJETO
        System.out.println("[7/7] Generando archivo objeto...");
        writer.writeObject(asmCode, outputDir + "/out.lobj");
        System.out.println("✅ Archivo objeto generado");
        System.out.println("   → Guardado en: " + outputDir + "/out.lobj");

        System.out.println("\n=== COMPILACIÓN EXITOSA ===");
        System.out.println("\nPara ejecutar el programa compilado:");
        System.out.println("  java -cp target/classes runtime.RuntimePlayer " + outputDir + "/out.lobj");
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Uso: java Compiler <archivo-fuente> [directorio-salida]");
            System.out.println("Ejemplo: java Compiler test/ejemplo.smp target");
            return;
        }

        String inputFile = args[0];
        String outputDir = args.length > 1 ? args[1] : "target";

        try {
            compile(inputFile, outputDir);
        } catch (IOException e) {
            System.err.println("Error de I/O: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error durante la compilación: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

