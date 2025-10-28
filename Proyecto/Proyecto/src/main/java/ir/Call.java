package ir;

public class Call extends Instr {
    public String dest;
    public String funcName;
    public int numArgs;

    public Call(String dest, String funcName, int numArgs) {
        this.dest = dest;
        this.funcName = funcName;
        this.numArgs = numArgs;
    }

    @Override
    public String toString() {
        if (dest != null) {
            return dest + " = call " + funcName + ", " + numArgs;
        }
        return "call " + funcName + ", " + numArgs;
    }

    @Override
    public Instr clone() {
        return new Call(dest, funcName, numArgs);
    }
}
