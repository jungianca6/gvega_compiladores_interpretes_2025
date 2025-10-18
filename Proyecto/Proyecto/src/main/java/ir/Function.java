package ir;

import java.util.ArrayList;
import java.util.List;

public class Function {
    public String name;
    public List<String> params;
    public List<BasicBlock> blocks;

    public Function(String name, List<String> params) {
        this.name = name;
        this.params = params != null ? params : new ArrayList<>();
        this.blocks = new ArrayList<>();
    }

    public void addBlock(BasicBlock block) {
        blocks.add(block);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("function ").append(name).append("(");
        for (int i = 0; i < params.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(params.get(i));
        }
        sb.append(")\n");
        for (BasicBlock block : blocks) {
            sb.append(block.toString());
        }
        return sb.toString();
    }
}

