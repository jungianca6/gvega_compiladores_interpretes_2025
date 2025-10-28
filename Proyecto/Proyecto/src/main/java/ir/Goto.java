package ir;

public class Goto extends Instr {
    public String label;

    public Goto(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return "goto " + label;
    }

    @Override
    public Instr clone() {
        return new Goto(label);
    }
}

