package optimizer;

import ir.*;
import java.util.*;

public class Peephole {
    private int optimizedCount = 0;

    public IR run(IR ir) {
        optimizedCount = 0;

        optimizeInstructions(ir.globalInstructions);

        for (Function func : ir.functions) {
            for (BasicBlock block : func.blocks) {
                optimizeInstructions(block.instructions);
            }
        }

        return ir;
    }

    private void optimizeInstructions(List<Instr> instrs) {
        for (int i = 0; i < instrs.size(); i++) {
            Instr instr = instrs.get(i);

            if (instr instanceof BinOp) {
                BinOp binOp = (BinOp) instr;
                Instr optimized = applyPeepholeRules(binOp);

                if (optimized != null && !optimized.equals(instr)) {
                    instrs.set(i, optimized);
                    optimizedCount++;
                }
            }
        }
    }

    private Instr applyPeepholeRules(BinOp binOp) {
        // x = y + 0  =>  x = y
        if (binOp.op == Op.ADD && "0".equals(binOp.right)) {
            return new Assign(binOp.dest, binOp.left);
        }

        // x = 0 + y  =>  x = y
        if (binOp.op == Op.ADD && "0".equals(binOp.left)) {
            return new Assign(binOp.dest, binOp.right);
        }

        // x = y - 0  =>  x = y
        if (binOp.op == Op.SUB && "0".equals(binOp.right)) {
            return new Assign(binOp.dest, binOp.left);
        }

        // x = y * 1  =>  x = y
        if (binOp.op == Op.MUL && "1".equals(binOp.right)) {
            return new Assign(binOp.dest, binOp.left);
        }

        // x = 1 * y  =>  x = y
        if (binOp.op == Op.MUL && "1".equals(binOp.left)) {
            return new Assign(binOp.dest, binOp.right);
        }

        // x = y * 0  =>  x = 0
        if (binOp.op == Op.MUL && "0".equals(binOp.right)) {
            return new Assign(binOp.dest, "0");
        }

        // x = 0 * y  =>  x = 0
        if (binOp.op == Op.MUL && "0".equals(binOp.left)) {
            return new Assign(binOp.dest, "0");
        }

        // x = y / 1  =>  x = y
        if (binOp.op == Op.DIV && "1".equals(binOp.right)) {
            return new Assign(binOp.dest, binOp.left);
        }

        // x = y ** 1  =>  x = y
        if (binOp.op == Op.POW && "1".equals(binOp.right)) {
            return new Assign(binOp.dest, binOp.left);
        }

        // x = y ** 0  =>  x = 1
        if (binOp.op == Op.POW && "0".equals(binOp.right)) {
            return new Assign(binOp.dest, "1");
        }

        // x = true && y  =>  x = y
        if (binOp.op == Op.AND && "true".equals(binOp.left)) {
            return new Assign(binOp.dest, binOp.right);
        }

        // x = y && true  =>  x = y
        if (binOp.op == Op.AND && "true".equals(binOp.right)) {
            return new Assign(binOp.dest, binOp.left);
        }

        // x = false && y  =>  x = false
        if (binOp.op == Op.AND && "false".equals(binOp.left)) {
            return new Assign(binOp.dest, "false");
        }

        // x = y && false  =>  x = false
        if (binOp.op == Op.AND && "false".equals(binOp.right)) {
            return new Assign(binOp.dest, "false");
        }

        // x = false || y  =>  x = y
        if (binOp.op == Op.OR && "false".equals(binOp.left)) {
            return new Assign(binOp.dest, binOp.right);
        }

        // x = y || false  =>  x = y
        if (binOp.op == Op.OR && "false".equals(binOp.right)) {
            return new Assign(binOp.dest, binOp.left);
        }

        // x = true || y  =>  x = true
        if (binOp.op == Op.OR && "true".equals(binOp.left)) {
            return new Assign(binOp.dest, "true");
        }

        // x = y || true  =>  x = true
        if (binOp.op == Op.OR && "true".equals(binOp.right)) {
            return new Assign(binOp.dest, "true");
        }

        return binOp;
    }

    public int getOptimizedCount() {
        return optimizedCount;
    }
}

