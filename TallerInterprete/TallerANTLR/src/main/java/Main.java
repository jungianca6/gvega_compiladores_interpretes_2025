
import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import ast.*;
import parser.*;

import java.util.*;
import java.io.IOException;
import java.io.StringReader;

public class Main {
    private static final String EXTENSION = "smp";

    public static void main(String[] args) throws IOException {

        String program = args.length > 1 ? args[1] : "C:./TallerInterprete/TallerANTLR/test/test." + EXTENSION;

        System.out.println("Interpretando archivo " + program);

        SimpleLexer lexer = new SimpleLexer(new ANTLRFileStream(program));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        SimpleParser parser = new SimpleParser(tokens);

        SimpleParser.ProgramContext tree = parser.program();

        SimpleBaseVisitor visitor = new SimpleBaseVisitor();
        visitor.visit(tree);

        System.out.println("\nInterpretacion terminada");
    }
}