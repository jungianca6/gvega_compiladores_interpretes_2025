package ir;

public class BinOp extends Instr {
    public String dest;
    public String left;
    public Op op;
    public String right;

    public BinOp(String dest, String left, Op op, String right) {
        this.dest = dest;
        this.left = left;
        this.op = op;
        this.right = right;
    }

    @Override
    public String toString() {
        return dest + " = " + left + " " + opToString(op) + " " + right;
    }

    private String opToString(Op op) {
        switch (op) {
            case ADD: return "+";
            case SUB: return "-";
            case MUL: return "*";
            case DIV: return "/";
            case POW: return "**";
            case AND: return "&&";
            case OR: return "||";
            case EQ: return "==";
            case NE: return "!=";
            case LT: return "<";
            case LE: return "<=";
            case GT: return ">";
            case GE: return ">=";
            default: return "?";
        }
    }

    @Override
    public Instr clone() {
        return new BinOp(dest, left, op, right);
    }
}

