package ir;

public class Assign extends Instr {
    public String dest;
    public String source;

    public Assign(String dest, String source) {
        this.dest = dest;
        this.source = source;
    }

    @Override
    public String toString() {
        return dest + " = " + source;
    }

    @Override
    public Instr clone() {
        return new Assign(dest, source);
    }
}

