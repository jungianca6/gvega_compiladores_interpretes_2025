package ir;

import java.util.ArrayList;
import java.util.List;

public class IR {
    public List<Function> functions;
    public List<Instr> globalInstructions;
    private int tempCounter;
    private int labelCounter;

    public IR() {
        this.functions = new ArrayList<>();
        this.globalInstructions = new ArrayList<>();
        this.tempCounter = 0;
        this.labelCounter = 0;
    }

    public String newTemp() {
        return "t" + (tempCounter++);
    }

    public String newLabel() {
        return "L" + (labelCounter++);
    }

    public void addFunction(Function func) {
        functions.add(func);
    }

    public void addGlobalInstr(Instr instr) {
        globalInstructions.add(instr);
    }

    public int getTotalInstructions() {
        int count = globalInstructions.size();
        for (Function f : functions) {
            for (BasicBlock bb : f.blocks) {
                count += bb.instructions.size();
            }
        }
        return count;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        // Global instructions
        if (!globalInstructions.isEmpty()) {
            sb.append("=== GLOBAL ===\n");
            for (Instr i : globalInstructions) {
                sb.append(i.toString()).append("\n");
            }
            sb.append("\n");
        }

        // Functions
        for (Function f : functions) {
            sb.append(f.toString()).append("\n");
        }

        return sb.toString();
    }
}

