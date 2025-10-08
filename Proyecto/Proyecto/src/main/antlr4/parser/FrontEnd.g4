grammar FrontEnd;

@parser::header {
    import java.util.Map;
    import java.util.HashMap;
    import java.util.List;
    import java.util.ArrayList;
    import java.util.Random;
    import java.util.*;
    import ast.*;
    import ast.Aritmeticos.*;
    import ast.Logicos.*;
    import ast.Instrucciones.*;
    import ast.Tortuga.*;
    import semantic.*;
}

// Tabla de símbolos global al parser
@parser::members {
    Map<String, Object> symbolTable = new HashMap<String, Object>();
    {
        Turtle turtle = new Turtle(0, 0, 0);
        symbolTable.put("turtle", turtle);
    }

    // ANALIZADOR SEMÁNTICO
    private semantic.SemanticAnalyzer semanticAnalyzer;

    public void setSemanticAnalyzer(semantic.SemanticAnalyzer analyzer) {
        this.semanticAnalyzer = analyzer;
    }

    // Métodos de ayuda para análisis semántico
    private void semanticDeclareOrAssign(String name, Object value, boolean isHazAssignment) {
        if (semanticAnalyzer != null) {
            SemanticAnalyzer.ValueType type = semanticAnalyzer.inferExpressionType(value);
            semanticAnalyzer.declareOrAssign(name, type, value, isHazAssignment);
        }
    }

    private void semanticCheckVariable(String name) {
        if (semanticAnalyzer != null && !semanticAnalyzer.variableExists(name)) {
            semanticAnalyzer.addError("Variable no declarada: '" + name + "'");
        }
    }

    // MÉTODO PARA VERIFICAR COMENTARIOS EN PRIMERA LÍNEA
    private void checkFirstLineForComment(Token startToken) {
        if (semanticAnalyzer != null && startToken != null) {
            // Esta verificación se hará en el Visitor, pero podemos hacer una básica aquí
            // El visitor hará la verificación completa
        }
    }
}

program
    :
        {
            checkFirstLineForComment(_input.LT(1)); // Verificar primer token
            List<ASTNode> body = new ArrayList<ASTNode>();
        }
        (i=instrucciones { body.add($i.node); })*
        {
            for (ASTNode n : body) {
                n.execute(symbolTable);
            }
        }
    ;


funcion returns [ASTNode node]
    : PARA name=ID SQUARE_PAR_OPEN
        {
            List<String> parametros = new ArrayList<String>();
            List<ASTNode> cuerpo = new ArrayList<ASTNode>();
        }
        (p1=ID { parametros.add($p1.text); } (p2=ID { parametros.add($p2.text); })*)?
      SQUARE_PAR_CLOSE
        (i=instrucciones { cuerpo.add($i.node); } | comment { /* ignorar comentarios */ })*
      FIN
        {
            $node = new Funcion($name.text, parametros, cuerpo);
        }
    ;

//Instrucciones
instrucciones returns [ASTNode node]
    : funcion         { $node = $funcion.node; }
    | llamadaFuncion  { $node = $llamadaFuncion.node; }
    | println         { $node = $println.node; }
    | comment         { $node = $comment.node; }
    | conditional     { $node = $conditional.node; }
    | var_decl        { $node = $var_decl.node; }
    | var_assign      { $node = $var_assign.node; }
    | inic            { $node = $inic.node; }
    | inc             { $node = $inc.node; }
    | suma_expr       { $node = $suma_expr.node; }
    | resta_expr      { $node = $resta_expr.node; }
    | mult_expr       { $node = $mult_expr.node; }
    | div_expr        { $node = $div_expr.node; }
    | pot_expr        { $node = $pot_expr.node; }
    | mientras        { $node = $mientras.node; }
    | haz_mientras    { $node = $haz_mientras.node; }
    | random          { $node = $random.node; }
    | menor           { $node = $menor.node; }
    | mayor           { $node = $mayor.node; }
    | and             { $node = $and.node; }
    | or              { $node = $or.node; }
    | iguales         { $node = $iguales.node; }
    | hasta           { $node = $hasta.node; }
    | ejecuta         { $node = $ejecuta.node; }
    | repite          { $node = $repite.node; }
    // ---------------- Tortuga -----------------
    | avanza          { $node = $avanza.node; }
    | retrocede       { $node = $retrocede.node; }
    | giraDerecha     { $node = $giraDerecha.node; }
    | giraIzquierda   { $node = $giraIzquierda.node; }
    | ocultaTortuga   { $node = $ocultaTortuga.node; }
    | ponPos          { $node = $ponPos.node; }
    | ponX            { $node = $ponX.node; }
    | ponY            { $node = $ponY.node; }
    | ponRumbo        { $node = $ponRumbo.node; }
    | mostrarRumbo    { $node = $mostrarRumbo.node; }
    | bajalapiz       { $node = $bajalapiz.node; }
    | subelapiz       { $node = $subelapiz.node; }
    | colorlapiz      { $node = $colorlapiz.node; }
    | centro          { $node = $centro.node; }
    | espera          { $node = $espera.node; }
    ;

llamadaFuncion returns [ASTNode node]
    :
        {
            List<ASTNode> argumentos = new ArrayList<ASTNode>();
        }
        name=ID SQUARE_PAR_OPEN
        (a1=expression { argumentos.add($a1.node); } (a2=expression { argumentos.add($a2.node); })*)?
      SQUARE_PAR_CLOSE SEMICOLON
        {
            $node = new LlamadaFuncion($name.text, argumentos);
        }
    ;

// ---------------- Reglas de Tortuga -----------------//

avanza returns [ASTNode node]
    : (AVANZA) e=expression SEMICOLON
      { $node = new Avanza($e.node); }
    ;

retrocede returns [ASTNode node]
    : (RE) e=expression SEMICOLON
      { $node = new Retrocede($e.node); }
    ;

giraDerecha returns [ASTNode node]
    : (GD) e=expression SEMICOLON
      { $node = new GiraDerecha($e.node); }
    ;

giraIzquierda returns [ASTNode node]
    : (GI) e=expression SEMICOLON
      { $node = new GiraIzquierda($e.node); }
    ;

ocultaTortuga returns [ASTNode node]
    : (OT) SEMICOLON
      { $node = new OcultaTortuga(); }
    ;

ponPos returns [ASTNode node]
    : (PONPOS) SQUARE_PAR_OPEN x=expression y=expression SQUARE_PAR_CLOSE SEMICOLON
      { $node = new PonPos($x.node, $y.node); }
    ;

ponX returns [ASTNode node]
    : PONX e=expression SEMICOLON
      { $node = new PonX($e.node); }
    ;

ponY returns [ASTNode node]
    : PONY e=expression SEMICOLON
      { $node = new PonY($e.node); }
    ;

ponRumbo returns [ASTNode node]
    : PONRUMBO e=expression SEMICOLON
      { $node = new PonRumbo($e.node); }
    ;

mostrarRumbo returns [ASTNode node]
    : RUMBO SEMICOLON
      { $node = new MostrarRumbo(); }
    ;

bajalapiz returns [ASTNode node]
    : BAJALAPIZ SEMICOLON
      { $node = new BajaLapiz(); }
    ;

subelapiz returns [ASTNode node]
    : SUBELAPIZ SEMICOLON
      { $node = new SubeLapiz(); }
    ;

colorlapiz returns [ASTNode node]
    : COLOR color=COLORES SEMICOLON
      { $node = new ColorLapiz($color.text); }
    ;

centro returns [ASTNode node]
    : CENTRO SEMICOLON
      { $node = new Centro(); }
    ;

espera returns [ASTNode node]
    : ESPERA e=expression SEMICOLON
      { $node = new Espera($e.node); }
    ;


// ---------------- Funcion -----------------//
println returns [ASTNode node]
    : PRINTLN expression SEMICOLON
        { $node = new Println($expression.node); }
    ;

comment returns [ASTNode node]:
	(COMMENT
	|
	LINE_COMMENT){
		$node = new Comment();
	};


inic returns [ASTNode node]
    : INIC ID ASSIGN e=expression SEMICOLON
      {
        semanticDeclareOrAssign($ID.text, $e.node,true);
        $node = new Inic($ID.text, $e.node);
      }
    ;

inc returns [ASTNode node]
    : INC SQUARE_PAR_OPEN id=ID SQUARE_PAR_CLOSE SEMICOLON
      { $node = new Inc($id.text, null); }     // caso N1 solo
    | INC SQUARE_PAR_OPEN id=ID val=expression SQUARE_PAR_CLOSE SEMICOLON
      { $node = new Inc($id.text, $val.node); } // caso N1 + N2
    ;


// regla auxiliar para manejar opcional
incOptional returns [ASTNode node]
    : e=expression { $node = $e.node; }
    ;


conditional returns [ASTNode node]
    : SI PAR_OPEN expression PAR_CLOSE
        {
            List<ASTNode> ifBody = new ArrayList<ASTNode>();
            List<ASTNode> elseBody = new ArrayList<ASTNode>();
        }
        SQUARE_PAR_OPEN (s1=instrucciones { ifBody.add($s1.node); })* SQUARE_PAR_CLOSE
        (
            SQUARE_PAR_OPEN (s2=instrucciones { elseBody.add($s2.node); })* SQUARE_PAR_CLOSE
        )?
        {
            $node = new Si($expression.node, ifBody, elseBody);
        }
    ;

var_decl returns [ASTNode node]
    : VAR ID SEMICOLON
        {
            semanticDeclareOrAssign($ID.text, null, false);
            $node = new VarDecl($ID.text);
        }
    ;


 var_assign returns [ASTNode node]
     : HAZ ID expression SEMICOLON
         {
            semanticDeclareOrAssign($ID.text, $expression.node, true);
            $node = new VarAssign($ID.text, $expression.node);
         }
     ;

ejecuta returns [ASTNode node]
    : EJECUTA SQUARE_PAR_OPEN
        {
            List<ASTNode> ordenes = new ArrayList<ASTNode>();
        }
        (i=instrucciones { ordenes.add($i.node); })*
      SQUARE_PAR_CLOSE SEMICOLON
        {
            $node = new Ejecuta(ordenes);
        }
    ;

repite returns [ASTNode node]
    : REPITE e=expression SQUARE_PAR_OPEN
        {
            List<ASTNode> ordenes = new ArrayList<ASTNode>();
        }
        (i=instrucciones { ordenes.add($i.node); })*
      SQUARE_PAR_CLOSE SEMICOLON
        {
            $node = new Repite($e.node, ordenes);
        }
    ;

mientras returns [ASTNode node]
    : MIENTRAS PAR_OPEN condition=expression PAR_CLOSE
      SQUARE_PAR_OPEN
        {
            List<ASTNode> body = new ArrayList<ASTNode>();
        }
        (i=instrucciones { body.add($i.node); })*
      SQUARE_PAR_CLOSE
        {
            $node = new Mientras($condition.node, body);
        }
    ;

haz_mientras returns [ASTNode node]
  : HAZ_MIENTRAS SQUARE_PAR_OPEN
      { List<ASTNode> body = new ArrayList<ASTNode>(); }
      (i=instrucciones { body.add($i.node); })*
    SQUARE_PAR_CLOSE
    SQUARE_PAR_OPEN condition=expression SQUARE_PAR_CLOSE
    {
      $node = new HazMientras(body, $condition.node);
    }
  ;



hasta returns [ASTNode node]
    : HASTA PAR_OPEN expression PAR_CLOSE
      SQUARE_PAR_OPEN
        {
            List<ASTNode> body = new ArrayList<ASTNode>();
        }
        (i=instrucciones { body.add($i.node); })*
      SQUARE_PAR_CLOSE
        {
            $node = new Hasta($expression.node, body);
        }
    ;



// ---------------- Aritméticas -----------------//
suma_expr  returns [ASTNode node]
    : SUMA
        {
            List<ASTNode> args = new ArrayList<ASTNode>();
        }
        e=expression { args.add($e.node); } (e=expression { args.add($e.node); })*
      SEMICOLON
        {
            $node = new Suma(args);   // instancia tu AST Suma con lista de operandos
        }
    ;

resta_expr  returns [ASTNode node]
    : RESTA
        {
            List<ASTNode> args = new ArrayList<ASTNode>();
        }
        e=expression { args.add($e.node); } (e=expression { args.add($e.node); })*
      SEMICOLON
        {
            $node = new Diferencia(args);
        }
    ;

mult_expr  returns [ASTNode node]
    : PROD
        {
            List<ASTNode> args = new ArrayList<ASTNode>();
        }
        e=expression { args.add($e.node); } (e=expression { args.add($e.node); })*
      SEMICOLON
        {
            $node = new Producto(args);
        }
    ;


div_expr  returns [ASTNode node]
    : DIVISION
        {
            List<ASTNode> args = new ArrayList<ASTNode>();
        }
        e=expression { args.add($e.node); } (e=expression { args.add($e.node); })*
      SEMICOLON
        {
            $node = new Division(args);
        }
    ;


pot_expr  returns [ASTNode node]
    : POTENCIA
        {
            List<ASTNode> args = new ArrayList<ASTNode>();
        }
        e=expression { args.add($e.node); } (e=expression { args.add($e.node); })*
      SEMICOLON
        {
            $node = new Potencia(args);
        }
    ;

random  returns [ASTNode node]
    : AZAR e=expression
      SEMICOLON
        {
            $node = new Rand($e.node);
        }
    ;

// ---------------- Lógicas -----------------//
menor  returns [ASTNode node]
    : MENOR
        {
            List<ASTNode> args = new ArrayList<ASTNode>();
        }
        e=expression { args.add($e.node); } (e=expression { args.add($e.node); })*
      SEMICOLON
        {
            $node = new Menor(args);
        }
    ;

mayor  returns [ASTNode node]
    : MAYOR
        {
            List<ASTNode> args = new ArrayList<ASTNode>();
        }
        e=expression { args.add($e.node); } (e=expression { args.add($e.node); })*
      SEMICOLON
        {
            $node = new Mayor(args);
        }
    ;

and  returns [ASTNode node]
    : Y
        {
            List<ASTNode> args = new ArrayList<ASTNode>();
        }
        e=expression { args.add($e.node); } (e=expression { args.add($e.node); })*
      SEMICOLON
        {
            $node = new AndAST(args);
        }
    ;

or  returns [ASTNode node]
    : O
        {
            List<ASTNode> args = new ArrayList<ASTNode>();
        }
        e=expression { args.add($e.node); } (e=expression { args.add($e.node); })*
      SEMICOLON
        {
            $node = new OrAST(args);
        }
    ;

iguales  returns [ASTNode node]
    : IGUALES
        {
            List<ASTNode> args = new ArrayList<ASTNode>();
        }
        e=expression { args.add($e.node); } (e=expression { args.add($e.node); })*
      SEMICOLON
        {
            $node = new Iguales(args);
        }
    ;

expression returns [ASTNode node]
    : t1=factor { $node = $t1.node; }
      (PLUS t2=factor { $node = new Addition($node, $t2.node); })*
      (MINUS t3=factor { $node = new Substraction($node, $t3.node); })*
      (GT t4=factor { $node = new GreaterThan($node, $t4.node); })*
      (LT t5=factor { $node = new LessThan($node, $t5.node); })*
      (EQ t6=factor { $node = new EqualThan($node, $t6.node); })*
      (GEQ t7=factor { $node = new GreaterEqualThan($node, $t7.node); })*
      (LEQ t8=factor { $node = new LessEqualThan($node, $t8.node); })*
      (NEQ t9=factor { $node = new NotEqual($node, $t9.node); })*
      (AND t9=factor { $node = new And($node, $t9.node); })*
      (OR t9=factor  { $node = new Or($node, $t9.node); })*
    ;

factor returns [ASTNode node]
    : t1=term { $node = $t1.node; }
      (MULT t2=term { $node = new Multiplication($node, $t2.node); })*
      (DIV t3=term { $node = new Divide($node, $t3.node); })*
    ;

term returns [ASTNode node]
    : NUMBER  { $node = new Constant(Integer.parseInt($NUMBER.text)); }
    | BOOLEAN { $node = new Constant(Boolean.parseBoolean($BOOLEAN.text)); }
    | ID      { $node = new VarRef($ID.text); }
    | PAR_OPEN e=expression PAR_CLOSE { $node = $e.node; }
    ;



// ---------- TOKENS BASICOS ----------

PROGRAM: 'program';
PARA: 'PARA';
FIN: 'FIN';
VAR: 'var';
HAZ: 'haz';
PRINTLN: 'println';
EJECUTA: 'Ejecuta';
REPITE: 'Repite';
INIC: 'INIC';
INC: 'INC';
ASSIGN: '=';

// ---------- TOKENS CONTROL FLOW ----------
SI: 'SI';
MIENTRAS: 'MIENTRAS';
HAZ_MIENTRAS : 'HAZ.MIENTRAS';
HASTA: 'HAZ.HASTA';



// ---------- TOKENS ARITMETICOS ----------

ARITMETICAS: SUMA | RESTA | PROD | DIVISION | POTENCIA | AZAR ;
SUMA: 'suma';
RESTA: 'diferencia';
PROD: 'producto';
DIVISION: 'division';
POTENCIA: 'potencia';
AZAR: 'azar';

PLUS: '+';
MINUS: '-';
MULT: '*';
DIV: '/';


// ---------- TOKENS LOGICOS----------

MENOR: 'menorque?';
MAYOR: 'mayorque?';
Y: 'Y';
O: 'O';
IGUALES: 'iguales?';
BOOLEAN: 'true' | 'false';

AND: '&&';
OR: '||';
NOT: '!';

GT: '>';
LT: '<';
GEQ: '>=';
LEQ: '<=';
EQ: '==';
NEQ: '!=';



// ---------- TOKENS TORTUGA ----------
AVANZA: 'AVANZA' | 'AV';
RE: 'RETROCEDE' | 'RE';
GD: 'GIRADERECHA' | 'GD';
GI: 'GIRAIzquierda' | 'GI';
OT: 'OCULTATORTUGA' | 'OT';
PONPOS: 'PONPOS' | 'PONXY';
PONX: 'PONX';
PONY: 'PONY';
PONRUMBO: 'PONRUMBO';
RUMBO: 'Muestra RUMBO';
BAJALAPIZ: 'BajaLapiz'|'BL';
SUBELAPIZ: 'SubeLapiz'|'SB';
COLOR: 'PonCL' | 'PonColorLapiz';
CENTRO: 'centro';
ESPERA: 'espera';
COLORES: 'azul' | 'negro' | 'rojo';


// ---------- TOKENS ESTRUCTURA ----------

BRACKET_OPEN: '{';
BRACKET_CLOSE: '}';

PAR_OPEN: '(';
PAR_CLOSE: ')';


SQUARE_PAR_OPEN: '[';
SQUARE_PAR_CLOSE: ']';

SEMICOLON: ';';

// ---------- TOKENS IDENTIFICADORES Y VALORES ----------

ID: [a-zA-Z_][a-zA-Z0-9_]*;

NUMBER: [0-9]+;

// ---------- TOKENS COMENTARIOS ----------
COMMENT
: '/*' .*? '*/'
;
LINE_COMMENT
: '//' ~[\r\n]*
;

WS: [ \t\n\r]+ -> skip;

//(ARITMETICAS)* (e=expression)+