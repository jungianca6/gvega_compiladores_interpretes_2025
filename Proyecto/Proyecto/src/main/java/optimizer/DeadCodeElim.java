package optimizer;

import ir.*;
import java.util.*;

public class DeadCodeElim {
    private int eliminatedCount = 0;

    public IR run(IR ir) {
        eliminatedCount = 0;

        eliminateDeadCode(ir.globalInstructions);

        for (Function func : ir.functions) {
            for (BasicBlock block : func.blocks) {
                eliminateDeadCode(block.instructions);
            }
        }

        return ir;
    }

    private void eliminateDeadCode(List<Instr> instrs) {
        Set<String> used = new HashSet<>();

        // Backward pass: mark variables that are used
        for (int i = instrs.size() - 1; i >= 0; i--) {
            Instr instr = instrs.get(i);

            if (instr instanceof BinOp) {
                BinOp binOp = (BinOp) instr;
                markUsed(binOp.left, used);
                markUsed(binOp.right, used);
            } else if (instr instanceof UnOp) {
                UnOp unOp = (UnOp) instr;
                markUsed(unOp.operand, used);
            } else if (instr instanceof Assign) {
                Assign assign = (Assign) instr;
                markUsed(assign.source, used);
            } else if (instr instanceof IfGoto) {
                IfGoto ifGoto = (IfGoto) instr;
                markUsed(ifGoto.condition, used);
            } else if (instr instanceof Param) {
                Param param = (Param) instr;
                markUsed(param.value, used);
            } else if (instr instanceof Ret) {
                Ret ret = (Ret) instr;
                if (ret.value != null) {
                    markUsed(ret.value, used);
                }
            }
        }

        // Forward pass: remove assignments to unused variables
        Iterator<Instr> iterator = instrs.iterator();
        while (iterator.hasNext()) {
            Instr instr = iterator.next();

            if (instr instanceof Assign) {
                Assign assign = (Assign) instr;
                if (assign.dest != null && !used.contains(assign.dest) && assign.dest.startsWith("t")) {
                    iterator.remove();
                    eliminatedCount++;
                }
            } else if (instr instanceof BinOp) {
                BinOp binOp = (BinOp) instr;
                if (binOp.dest != null && !used.contains(binOp.dest) && binOp.dest.startsWith("t")) {
                    iterator.remove();
                    eliminatedCount++;
                }
            } else if (instr instanceof UnOp) {
                UnOp unOp = (UnOp) instr;
                if (unOp.dest != null && !used.contains(unOp.dest) && unOp.dest.startsWith("t")) {
                    iterator.remove();
                    eliminatedCount++;
                }
            }
        }
    }

    private void markUsed(String var, Set<String> used) {
        if (var != null && !isConstant(var)) {
            used.add(var);
        }
    }

    private boolean isConstant(String var) {
        if (var.matches("\\d+")) return true;
        if ("true".equals(var) || "false".equals(var)) return true;
        if (var.startsWith("\"")) return true;
        return false;
    }

    public int getEliminatedCount() {
        return eliminatedCount;
    }
}
