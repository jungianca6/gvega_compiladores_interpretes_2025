grammar Simple;

@parser::header {
    import java.util.Map;
    import java.util.HashMap;
    import java.util.List;
    import java.util.ArrayList;
    import ast.*;
}

// Tabla de símbolos global al parser
@parser::members {
    Map<String, Object> symbolTable = new HashMap<String, Object>();
}

program
    : PROGRAM ID BRACKET_OPEN
        {
            List<ASTNode> body = new ArrayList<ASTNode>();
        }
        (s=sentence { body.add($s.node); })*
      BRACKET_CLOSE
        {
            for (ASTNode n : body) {
                n.execute(symbolTable);
            }
        }
    ;

sentence returns [ASTNode node]
    : println     { $node = $println.node; }
    | conditional { $node = $conditional.node; }
    | var_decl    { $node = $var_decl.node; }
    | var_assign  { $node = $var_assign.node; }
    ;

println returns [ASTNode node]
    : PRINTLN expression SEMICOLON
        { $node = new Println($expression.node); }
    ;

conditional returns [ASTNode node]
    : IF PAR_OPEN expression PAR_CLOSE
        {
            List<ASTNode> body = new ArrayList<ASTNode>();
        }
        BRACKET_OPEN (s1=sentence { body.add($s1.node); })* BRACKET_CLOSE
        ELSE
        {
            List<ASTNode> elseBody = new ArrayList<ASTNode>();
        }
        BRACKET_OPEN (s2=sentence { elseBody.add($s2.node); })* BRACKET_CLOSE
        {
            $node = new If($expression.node, body, elseBody);
        }
    ;

var_decl returns [ASTNode node]
    : VAR ID SEMICOLON
        { $node = new VarDecl($ID.text); }
    ;

var_assign returns [ASTNode node]
    : ID ASSIGN expression SEMICOLON
        { $node = new VarAssign($ID.text, $expression.node); }
    ;

expression returns [ASTNode node]
    : t1=factor { $node = $t1.node; }
      (PLUS t2=factor { $node = new Addition($node, $t2.node); })*
      (AND t3=factor { $node = new And($node, $t3.node); })*
      (POW2ADD t2=factor { $node = new Pow2Add($node, $t2.node); })*
      (FIBSUM t2=factor { $node = new FibSum($node, $t2.node); })*
    ;

logic_expr returns [ASTNode node]
    : t1=factor { $node = $t1.node; }
      (AND t2=factor { $node = new And($node, $t2.node); })*
    ;

arithExpr returns [ASTNode node]
    : t1=factor { $node = $t1.node; }
      (PLUS t2=factor { $node = new Addition($node, $t2.node); })*
    ;

factor returns [ASTNode node]
    : t1=term { $node = $t1.node; }
      (MULT t2=term { $node = new Multiplication($node, $t2.node); })*
    ;

term returns [ASTNode node]
    : NUMBER  { $node = new Constant(Integer.parseInt($NUMBER.text)); }
    | BOOLEAN { $node = new Constant(Boolean.parseBoolean($BOOLEAN.text)); }
    | ID      { $node = new VarRef($ID.text); }
    | PAR_OPEN e=expression PAR_CLOSE { $node = $e.node; }
    ;

// ---------- TOKENS ----------

PROGRAM: 'program';
VAR: 'var';
PRINTLN: 'println';
IF: 'if';
ELSE: 'else';

PLUS: '+';
MINUS: '-';
MULT: '*';
DIV: '/';
POW2ADD: '**+';
FIBSUM: 'fibsum';


AND: '&&';
OR: '||';
NOT: '!';

GT: '>';
LT: '<';
GEQ: '>=';
LEQ: '<=';
EQ: '==';
NEQ: '!=';

ASSIGN: '=';

BRACKET_OPEN: '{';
BRACKET_CLOSE: '}';

PAR_OPEN: '(';
PAR_CLOSE: ')';

SEMICOLON: ';';

BOOLEAN: 'true' | 'false';

ID: [a-zA-Z_][a-zA-Z0-9_]*;

NUMBER: [0-9]+;

WS: [ \t\n\r]+ -> skip;
