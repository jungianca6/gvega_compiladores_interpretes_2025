package ir;

public class Ret extends Instr {
    public String value;

    public Ret(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value != null ? "return " + value : "return";
    }

    @Override
    public Instr clone() {
        return new Ret(value);
    }
}

