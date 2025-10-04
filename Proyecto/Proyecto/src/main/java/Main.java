import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import parser.*;

import java.io.IOException;
import java.io.StringReader;

public class Main {
    private static final String EXTENSION = "smp";

    public static void main(String[] args) throws IOException {

        String program = args.length > 1 ? args[1] : "test/test." + EXTENSION;

        System.out.println("Interpretando archivo " + program);

        FrontEndLexer lexer = new FrontEndLexer(new ANTLRFileStream(program));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        FrontEndParser parser = new FrontEndParser(tokens);

        FrontEndParser.ProgramContext tree = parser.program();

        FrontEndBaseVisitor visitor = new FrontEndBaseVisitor();
        visitor.visit(tree);

        System.out.println("\nInterpretacion terminada");
    }
}