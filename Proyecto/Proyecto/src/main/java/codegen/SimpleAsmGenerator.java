package codegen;

import ir.*;
import java.util.*;

public class SimpleAsmGenerator {
    private List<String> asmCode;

    public SimpleAsmGenerator() {
        this.asmCode = new ArrayList<>();
    }

    public List<String> generate(IR ir) {
        asmCode.clear();

        // Generate global instructions
        if (!ir.globalInstructions.isEmpty()) {
            asmCode.add("; GLOBAL SECTION");
            for (Instr instr : ir.globalInstructions) {
                generateInstruction(instr);
            }
            asmCode.add("");
        }

        // Generate functions
        for (Function func : ir.functions) {
            asmCode.add("; FUNCTION " + func.name);
            asmCode.add("label func_" + func.name);

            for (BasicBlock block : func.blocks) {
                if (!block.label.equals("entry")) {
                    asmCode.add("label " + block.label);
                }
                for (Instr instr : block.instructions) {
                    generateInstruction(instr);
                }
            }
            asmCode.add("");
        }

        return asmCode;
    }

    private void generateInstruction(Instr instr) {
        if (instr instanceof Assign) {
            Assign assign = (Assign) instr;
            asmCode.add("MOV " + assign.dest + ", " + assign.source);
        } else if (instr instanceof BinOp) {
            BinOp binOp = (BinOp) instr;
            String op = opToAsm(binOp.op);
            asmCode.add("BIN " + binOp.dest + ", " + binOp.left + ", " + op + ", " + binOp.right);
        } else if (instr instanceof UnOp) {
            UnOp unOp = (UnOp) instr;
            String op = opToAsm(unOp.op);
            asmCode.add("UN " + unOp.dest + ", " + op + ", " + unOp.operand);
        } else if (instr instanceof Label) {
            Label label = (Label) instr;
            asmCode.add("label " + label.name);
        } else if (instr instanceof Goto) {
            Goto gotoInstr = (Goto) instr;
            asmCode.add("JMP " + gotoInstr.label);
        } else if (instr instanceof IfGoto) {
            IfGoto ifGoto = (IfGoto) instr;
            if (ifGoto.labelFalse != null) {
                asmCode.add("JMPTF " + ifGoto.condition + ", " + ifGoto.labelTrue + ", " + ifGoto.labelFalse);
            } else {
                asmCode.add("JMPT " + ifGoto.condition + ", " + ifGoto.labelTrue);
            }
        } else if (instr instanceof Param) {
            Param param = (Param) instr;
            asmCode.add("PUSH " + param.value);
        } else if (instr instanceof Call) {
            Call call = (Call) instr;
            if (call.dest != null) {
                asmCode.add("CALL " + call.funcName + ", " + call.numArgs);
                asmCode.add("POP " + call.dest);
            } else {
                asmCode.add("CALL " + call.funcName + ", " + call.numArgs);
            }
        } else if (instr instanceof Ret) {
            Ret ret = (Ret) instr;
            if (ret.value != null) {
                asmCode.add("RET " + ret.value);
            } else {
                asmCode.add("RET");
            }
        }
    }

    private String opToAsm(Op op) {
        switch (op) {
            case ADD: return "ADD";
            case SUB: return "SUB";
            case MUL: return "MUL";
            case DIV: return "DIV";
            case POW: return "POW";
            case AND: return "AND";
            case OR: return "OR";
            case NOT: return "NOT";
            case EQ: return "EQ";
            case NE: return "NE";
            case LT: return "LT";
            case LE: return "LE";
            case GT: return "GT";
            case GE: return "GE";
            default: return "UNKNOWN";
        }
    }

    public String getAsmAsString() {
        StringBuilder sb = new StringBuilder();
        for (String line : asmCode) {
            sb.append(line).append("\n");
        }
        return sb.toString();
    }
}

