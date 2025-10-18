package ir;

public class Param extends Instr {
    public String value;

    public Param(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "param " + value;
    }

    @Override
    public Instr clone() {
        return new Param(value);
    }
}

