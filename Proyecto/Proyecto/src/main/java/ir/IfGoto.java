package ir;

public class IfGoto extends Instr {
    public String condition;
    public String labelTrue;
    public String labelFalse;

    public IfGoto(String condition, String labelTrue, String labelFalse) {
        this.condition = condition;
        this.labelTrue = labelTrue;
        this.labelFalse = labelFalse;
    }

    @Override
    public String toString() {
        if (labelFalse != null) {
            return "if " + condition + " goto " + labelTrue + " else goto " + labelFalse;
        }
        return "if " + condition + " goto " + labelTrue;
    }

    @Override
    public Instr clone() {
        return new IfGoto(condition, labelTrue, labelFalse);
    }
}

