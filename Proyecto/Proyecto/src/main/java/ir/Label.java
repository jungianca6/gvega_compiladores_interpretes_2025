package ir;

public class Label extends Instr {
    public String name;

    public Label(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name + ":";
    }

    @Override
    public Instr clone() {
        return new Label(name);
    }
}

