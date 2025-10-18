package ir;

import ast.*;
import ast.Aritmeticos.*;
import ast.Logicos.*;
import ast.Instrucciones.*;
import ast.Tortuga.*;

import java.lang.reflect.Field;
import java.util.List;
import java.util.ArrayList;

public class IRBuilder {
    private IR ir;

    public IRBuilder() {
        this.ir = new IR();
    }

    public IR build(List<ASTNode> program) {
        for (ASTNode node : program) {
            translateStatement(node, ir.globalInstructions);
        }
        return ir;
    }

    private void translateStatement(ASTNode node, List<Instr> instrs) {
        if (node instanceof VarDecl) {
            // Variable declaration doesn't emit IR
            return;
        } else if (node instanceof VarAssign) {
            translateVarAssign((VarAssign) node, instrs);
        } else if (node instanceof Inic) {
            translateInic((Inic) node, instrs);
        } else if (node instanceof Println) {
            translatePrintln((Println) node, instrs);
        } else if (node instanceof Si) {
            translateSi((Si) node, instrs);
        } else if (node instanceof Mientras) {
            translateMientras((Mientras) node, instrs);
        } else if (node instanceof HazMientras) {
            translateHazMientras((HazMientras) node, instrs);
        } else if (node instanceof Hasta) {
            translateHasta((Hasta) node, instrs);
        } else if (node instanceof Repite) {
            translateRepite((Repite) node, instrs);
        } else if (node instanceof Ejecuta) {
            translateEjecuta((Ejecuta) node, instrs);
        } else if (node instanceof Inc) {
            translateInc((Inc) node, instrs);
        } else if (node instanceof Funcion) {
            translateFuncion((Funcion) node);
        } else if (node instanceof LlamadaFuncion) {
            translateLlamadaFuncion((LlamadaFuncion) node, instrs);
        } else if (node instanceof Suma) {
            String temp = translateExpression(node, instrs);
            // Statement-level suma is evaluated but not assigned
        } else if (node instanceof Diferencia) {
            String temp = translateExpression(node, instrs);
        } else if (node instanceof Producto) {
            String temp = translateExpression(node, instrs);
        } else if (node instanceof Division) {
            String temp = translateExpression(node, instrs);
        } else if (node instanceof Potencia) {
            String temp = translateExpression(node, instrs);
        } else if (node instanceof Rand) {
            String temp = translateExpression(node, instrs);
        } else if (node instanceof Menor) {
            String temp = translateExpression(node, instrs);
        } else if (node instanceof Mayor) {
            String temp = translateExpression(node, instrs);
        } else if (node instanceof AndAST) {
            String temp = translateExpression(node, instrs);
        } else if (node instanceof OrAST) {
            String temp = translateExpression(node, instrs);
        } else if (node instanceof Iguales) {
            String temp = translateExpression(node, instrs);
        } else if (node instanceof Avanza) {
            translateTurtleCommand("turtle_avanza", (Avanza) node, instrs);
        } else if (node instanceof Retrocede) {
            translateTurtleCommand("turtle_retrocede", (Retrocede) node, instrs);
        } else if (node instanceof GiraDerecha) {
            translateTurtleCommand("turtle_giraderecha", (GiraDerecha) node, instrs);
        } else if (node instanceof GiraIzquierda) {
            translateTurtleCommand("turtle_giraizquierda", (GiraIzquierda) node, instrs);
        } else if (node instanceof OcultaTortuga) {
            instrs.add(new Call(null, "turtle_oculta", 0));
        } else if (node instanceof PonPos) {
            translatePonPos((PonPos) node, instrs);
        } else if (node instanceof PonX) {
            translateTurtleCommand("turtle_ponx", (PonX) node, instrs);
        } else if (node instanceof PonY) {
            translateTurtleCommand("turtle_pony", (PonY) node, instrs);
        } else if (node instanceof PonRumbo) {
            translateTurtleCommand("turtle_ponrumbo", (PonRumbo) node, instrs);
        } else if (node instanceof MostrarRumbo) {
            instrs.add(new Call(null, "turtle_rumbo", 0));
        } else if (node instanceof BajaLapiz) {
            instrs.add(new Call(null, "turtle_bajalapiz", 0));
        } else if (node instanceof SubeLapiz) {
            instrs.add(new Call(null, "turtle_subelapiz", 0));
        } else if (node instanceof ColorLapiz) {
            translateColorLapiz((ColorLapiz) node, instrs);
        } else if (node instanceof Centro) {
            instrs.add(new Call(null, "turtle_centro", 0));
        } else if (node instanceof Espera) {
            translateTurtleCommand("turtle_espera", (Espera) node, instrs);
        } else if (node instanceof Comment) {
            // Comments don't emit IR
        }
    }

    private String translateExpression(ASTNode node, List<Instr> instrs) {
        if (node instanceof Constant) {
            Constant c = (Constant) node;
            return String.valueOf(c.getValue());
        } else if (node instanceof VarRef) {
            VarRef v = (VarRef) node;
            return v.getName();
        } else if (node instanceof Addition) {
            return translateBinaryOp((Addition) node, Op.ADD, instrs);
        } else if (node instanceof Substraction) {
            return translateBinaryOp((Substraction) node, Op.SUB, instrs);
        } else if (node instanceof Multiplication) {
            return translateBinaryOp((Multiplication) node, Op.MUL, instrs);
        } else if (node instanceof Divide) {
            return translateBinaryOp((Divide) node, Op.DIV, instrs);
        } else if (node instanceof Suma) {
            return translateMultiOperand((Suma) node, Op.ADD, instrs);
        } else if (node instanceof Diferencia) {
            return translateMultiOperand((Diferencia) node, Op.SUB, instrs);
        } else if (node instanceof Producto) {
            return translateMultiOperand((Producto) node, Op.MUL, instrs);
        } else if (node instanceof Division) {
            return translateMultiOperand((Division) node, Op.DIV, instrs);
        } else if (node instanceof Potencia) {
            return translateMultiOperand((Potencia) node, Op.POW, instrs);
        } else if (node instanceof And) {
            return translateBinaryOp((And) node, Op.AND, instrs);
        } else if (node instanceof Or) {
            return translateBinaryOp((Or) node, Op.OR, instrs);
        } else if (node instanceof AndAST) {
            return translateMultiOperand((AndAST) node, Op.AND, instrs);
        } else if (node instanceof OrAST) {
            return translateMultiOperand((OrAST) node, Op.OR, instrs);
        } else if (node instanceof EqualThan) {
            return translateBinaryOp((EqualThan) node, Op.EQ, instrs);
        } else if (node instanceof NotEqual) {
            return translateBinaryOp((NotEqual) node, Op.NE, instrs);
        } else if (node instanceof LessThan) {
            return translateBinaryOp((LessThan) node, Op.LT, instrs);
        } else if (node instanceof LessEqualThan) {
            return translateBinaryOp((LessEqualThan) node, Op.LE, instrs);
        } else if (node instanceof GreaterThan) {
            return translateBinaryOp((GreaterThan) node, Op.GT, instrs);
        } else if (node instanceof GreaterEqualThan) {
            return translateBinaryOp((GreaterEqualThan) node, Op.GE, instrs);
        } else if (node instanceof Menor) {
            return translateMultiOperand((Menor) node, Op.LT, instrs);
        } else if (node instanceof Mayor) {
            return translateMultiOperand((Mayor) node, Op.GT, instrs);
        } else if (node instanceof Iguales) {
            return translateMultiOperand((Iguales) node, Op.EQ, instrs);
        } else if (node instanceof Rand) {
            Rand r = (Rand) node;
            ASTNode expr = getFieldValue(r, "expr");
            String arg = translateExpression(expr, instrs);
            instrs.add(new Param(arg));
            String temp = ir.newTemp();
            instrs.add(new Call(temp, "random", 1));
            return temp;
        } else {
            // Unknown expression type
            return "?";
        }
    }

    private String translateBinaryOp(ASTNode node, Op op, List<Instr> instrs) {
        ASTNode left = getFieldValue(node, "operand1");
        ASTNode right = getFieldValue(node, "operand2");

        if (left == null) left = getFieldValue(node, "left");
        if (right == null) right = getFieldValue(node, "right");

        String leftTemp = translateExpression(left, instrs);
        String rightTemp = translateExpression(right, instrs);
        String result = ir.newTemp();
        instrs.add(new BinOp(result, leftTemp, op, rightTemp));
        return result;
    }

    private String translateMultiOperand(ASTNode node, Op op, List<Instr> instrs) {
        List<ASTNode> operands = getFieldValue(node, "operandos");
        if (operands == null) operands = getFieldValue(node, "args");

        if (operands == null || operands.isEmpty()) {
            return "0";
        }

        String result = translateExpression(operands.get(0), instrs);
        for (int i = 1; i < operands.size(); i++) {
            String nextOperand = translateExpression(operands.get(i), instrs);
            String temp = ir.newTemp();
            instrs.add(new BinOp(temp, result, op, nextOperand));
            result = temp;
        }
        return result;
    }

    private void translateVarAssign(VarAssign node, List<Instr> instrs) {
        String value = translateExpression(node.getExpression(), instrs);
        instrs.add(new Assign(node.getName(), value));
    }

    private void translateInic(Inic node, List<Instr> instrs) {
        // Probar diferentes nombres de campos
        String name = getFieldValue(node, "varName");
        if (name == null) name = getFieldValue(node, "nombre");

        ASTNode expr = getFieldValue(node, "value");
        if (expr == null) expr = getFieldValue(node, "expr");

        String value = "0"; // valor por defecto
        if (expr != null) {
            value = translateExpression(expr, instrs);
        }

        // Si el valor es "?" significa que no se pudo traducir
        if ("?".equals(value) || value == null) {
            value = "0";
        }

        if (name != null) {
            instrs.add(new Assign(name, value));
        }
    }

    private void translateInc(Inc node, List<Instr> instrs) {
        String name = getFieldValue(node, "nombre");
        ASTNode valNode = getFieldValue(node, "valor");

        if (valNode == null) {
            // INC[N1] -> N1 = N1 + 1
            String temp = ir.newTemp();
            instrs.add(new BinOp(temp, name, Op.ADD, "1"));
            instrs.add(new Assign(name, temp));
        } else {
            // INC[N1 N2] -> N1 = N1 + N2
            String val = translateExpression(valNode, instrs);
            String temp = ir.newTemp();
            instrs.add(new BinOp(temp, name, Op.ADD, val));
            instrs.add(new Assign(name, temp));
        }
    }

    private void translatePrintln(Println node, List<Instr> instrs) {
        ASTNode data = getFieldValue(node, "data");
        String value = translateExpression(data, instrs);
        instrs.add(new Param(value));
        instrs.add(new Call(null, "println", 1));
    }

    private void translateSi(Si node, List<Instr> instrs) {
        ASTNode condition = getFieldValue(node, "condition");
        List<ASTNode> ifBody = getFieldValue(node, "ifBody");
        List<ASTNode> elseBody = getFieldValue(node, "elseBody");

        String condTemp = translateExpression(condition, instrs);
        String labelTrue = ir.newLabel();
        String labelFalse = ir.newLabel();
        String labelEnd = ir.newLabel();

        instrs.add(new IfGoto(condTemp, labelTrue, labelFalse));

        instrs.add(new Label(labelTrue));
        for (ASTNode stmt : ifBody) {
            translateStatement(stmt, instrs);
        }
        instrs.add(new Goto(labelEnd));

        instrs.add(new Label(labelFalse));
        if (elseBody != null && !elseBody.isEmpty()) {
            for (ASTNode stmt : elseBody) {
                translateStatement(stmt, instrs);
            }
        }

        instrs.add(new Label(labelEnd));
    }

    private void translateMientras(Mientras node, List<Instr> instrs) {
        ASTNode condition = getFieldValue(node, "condition");
        List<ASTNode> body = getFieldValue(node, "body");

        String labelStart = ir.newLabel();
        String labelBody = ir.newLabel();
        String labelEnd = ir.newLabel();

        instrs.add(new Label(labelStart));
        String condTemp = translateExpression(condition, instrs);
        instrs.add(new IfGoto(condTemp, labelBody, labelEnd));

        instrs.add(new Label(labelBody));
        for (ASTNode stmt : body) {
            translateStatement(stmt, instrs);
        }
        instrs.add(new Goto(labelStart));

        instrs.add(new Label(labelEnd));
    }

    private void translateHazMientras(HazMientras node, List<Instr> instrs) {
        List<ASTNode> body = getFieldValue(node, "body");
        ASTNode condition = getFieldValue(node, "condition");

        String labelStart = ir.newLabel();
        String labelEnd = ir.newLabel();

        instrs.add(new Label(labelStart));
        for (ASTNode stmt : body) {
            translateStatement(stmt, instrs);
        }

        String condTemp = translateExpression(condition, instrs);
        instrs.add(new IfGoto(condTemp, labelStart, labelEnd));
        instrs.add(new Label(labelEnd));
    }

    private void translateHasta(Hasta node, List<Instr> instrs) {
        ASTNode condition = getFieldValue(node, "expression");
        List<ASTNode> body = getFieldValue(node, "body");

        String labelStart = ir.newLabel();
        String labelBody = ir.newLabel();
        String labelEnd = ir.newLabel();

        instrs.add(new Label(labelStart));
        String condTemp = translateExpression(condition, instrs);

        // HASTA continúa mientras la condición sea FALSA
        String notTemp = ir.newTemp();
        instrs.add(new UnOp(notTemp, Op.NOT, condTemp));
        instrs.add(new IfGoto(notTemp, labelBody, labelEnd));

        instrs.add(new Label(labelBody));
        for (ASTNode stmt : body) {
            translateStatement(stmt, instrs);
        }
        instrs.add(new Goto(labelStart));

        instrs.add(new Label(labelEnd));
    }

    private void translateRepite(Repite node, List<Instr> instrs) {
        ASTNode count = getFieldValue(node, "count");
        List<ASTNode> body = getFieldValue(node, "ordenes");

        String countTemp = translateExpression(count, instrs);
        String indexVar = ir.newTemp();
        instrs.add(new Assign(indexVar, "0"));

        String labelStart = ir.newLabel();
        String labelBody = ir.newLabel();
        String labelEnd = ir.newLabel();

        instrs.add(new Label(labelStart));
        String condTemp = ir.newTemp();
        instrs.add(new BinOp(condTemp, indexVar, Op.LT, countTemp));
        instrs.add(new IfGoto(condTemp, labelBody, labelEnd));

        instrs.add(new Label(labelBody));
        for (ASTNode stmt : body) {
            translateStatement(stmt, instrs);
        }
        String incTemp = ir.newTemp();
        instrs.add(new BinOp(incTemp, indexVar, Op.ADD, "1"));
        instrs.add(new Assign(indexVar, incTemp));
        instrs.add(new Goto(labelStart));

        instrs.add(new Label(labelEnd));
    }

    private void translateEjecuta(Ejecuta node, List<Instr> instrs) {
        List<ASTNode> ordenes = getFieldValue(node, "ordenes");
        for (ASTNode stmt : ordenes) {
            translateStatement(stmt, instrs);
        }
    }

    private void translateFuncion(Funcion node) {
        String name = node.getNombre();
        List<String> params = node.getParametros();
        List<ASTNode> body = node.getCuerpo();

        Function func = new Function(name, params);
        BasicBlock entryBlock = new BasicBlock("entry");

        for (ASTNode stmt : body) {
            translateStatement(stmt, entryBlock.instructions);
        }

        entryBlock.instructions.add(new Ret(null));
        func.addBlock(entryBlock);
        ir.addFunction(func);
    }

    private void translateLlamadaFuncion(LlamadaFuncion node, List<Instr> instrs) {
        String name = getFieldValue(node, "nombre");
        List<ASTNode> args = getFieldValue(node, "argumentos");

        for (ASTNode arg : args) {
            String argTemp = translateExpression(arg, instrs);
            instrs.add(new Param(argTemp));
        }

        instrs.add(new Call(null, name, args.size()));
    }

    private void translateTurtleCommand(String cmdName, ASTNode node, List<Instr> instrs) {
        ASTNode expr = getFieldValue(node, "expr");
        if (expr != null) {
            String arg = translateExpression(expr, instrs);
            instrs.add(new Param(arg));
            instrs.add(new Call(null, cmdName, 1));
        } else {
            instrs.add(new Call(null, cmdName, 0));
        }
    }

    private void translatePonPos(PonPos node, List<Instr> instrs) {
        ASTNode x = getFieldValue(node, "x");
        ASTNode y = getFieldValue(node, "y");

        String xTemp = translateExpression(x, instrs);
        String yTemp = translateExpression(y, instrs);

        instrs.add(new Param(xTemp));
        instrs.add(new Param(yTemp));
        instrs.add(new Call(null, "turtle_ponpos", 2));
    }

    private void translateColorLapiz(ColorLapiz node, List<Instr> instrs) {
        String color = getFieldValue(node, "color");
        instrs.add(new Param("\"" + color + "\""));
        instrs.add(new Call(null, "turtle_colorlapiz", 1));
    }

    @SuppressWarnings("unchecked")
    private <T> T getFieldValue(Object obj, String fieldName) {
        try {
            Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return (T) field.get(obj);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return null;
        }
    }
}
