package ir;

public class UnOp extends Instr {
    public String dest;
    public Op op;
    public String operand;

    public UnOp(String dest, Op op, String operand) {
        this.dest = dest;
        this.op = op;
        this.operand = operand;
    }

    @Override
    public String toString() {
        return dest + " = " + (op == Op.NOT ? "!" : "?") + operand;
    }

    @Override
    public Instr clone() {
        return new UnOp(dest, op, operand);
    }
}

