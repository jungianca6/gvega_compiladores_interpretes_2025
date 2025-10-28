package ir;

import java.util.ArrayList;
import java.util.List;

public class BasicBlock {
    public String label;
    public List<Instr> instructions;

    public BasicBlock(String label) {
        this.label = label;
        this.instructions = new ArrayList<>();
    }

    public void addInstr(Instr instr) {
        instructions.add(instr);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(label).append(":\n");
        for (Instr i : instructions) {
            sb.append("  ").append(i.toString()).append("\n");
        }
        return sb.toString();
    }
}
