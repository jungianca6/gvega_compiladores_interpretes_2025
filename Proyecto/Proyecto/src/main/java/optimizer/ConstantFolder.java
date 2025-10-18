package optimizer;

import ir.*;
import java.util.*;

public class ConstantFolder {
    private int foldedCount = 0;

    public IR run(IR ir) {
        foldedCount = 0;

        // Fold global instructions
        Map<String, Object> constants = new HashMap<>();
        foldInstructions(ir.globalInstructions, constants);

        // Fold function instructions
        for (Function func : ir.functions) {
            Map<String, Object> localConstants = new HashMap<>(constants);
            for (BasicBlock block : func.blocks) {
                foldInstructions(block.instructions, localConstants);
            }
        }

        return ir;
    }

    private void foldInstructions(List<Instr> instrs, Map<String, Object> constants) {
        for (int i = 0; i < instrs.size(); i++) {
            Instr instr = instrs.get(i);

            if (instr instanceof Assign) {
                Assign assign = (Assign) instr;
                Object value = tryEvaluate(assign.source, constants);
                if (value != null) {
                    constants.put(assign.dest, value);
                }
            } else if (instr instanceof BinOp) {
                BinOp binOp = (BinOp) instr;
                Object left = tryEvaluate(binOp.left, constants);
                Object right = tryEvaluate(binOp.right, constants);

                if (left != null && right != null) {
                    Object result = evaluateBinOp(left, binOp.op, right);
                    if (result != null) {
                        instrs.set(i, new Assign(binOp.dest, String.valueOf(result)));
                        constants.put(binOp.dest, result);
                        foldedCount++;
                    }
                }
            } else if (instr instanceof UnOp) {
                UnOp unOp = (UnOp) instr;
                Object operand = tryEvaluate(unOp.operand, constants);

                if (operand != null) {
                    Object result = evaluateUnOp(unOp.op, operand);
                    if (result != null) {
                        instrs.set(i, new Assign(unOp.dest, String.valueOf(result)));
                        constants.put(unOp.dest, result);
                        foldedCount++;
                    }
                }
            }
        }
    }

    private Object tryEvaluate(String operand, Map<String, Object> constants) {
        // Check if it's a constant value
        if (constants.containsKey(operand)) {
            return constants.get(operand);
        }

        // Try to parse as integer
        try {
            return Integer.parseInt(operand);
        } catch (NumberFormatException e) {
            // Not a number
        }

        // Try to parse as boolean
        if ("true".equals(operand)) return true;
        if ("false".equals(operand)) return false;

        return null;
    }

    private Object evaluateBinOp(Object left, Op op, Object right) {
        try {
            if (left instanceof Integer && right instanceof Integer) {
                int l = (Integer) left;
                int r = (Integer) right;

                switch (op) {
                    case ADD: return l + r;
                    case SUB: return l - r;
                    case MUL: return l * r;
                    case DIV: return r != 0 ? l / r : null;
                    case POW: return (int) Math.pow(l, r);
                    case EQ: return l == r;
                    case NE: return l != r;
                    case LT: return l < r;
                    case LE: return l <= r;
                    case GT: return l > r;
                    case GE: return l >= r;
                }
            } else if (left instanceof Boolean && right instanceof Boolean) {
                boolean l = (Boolean) left;
                boolean r = (Boolean) right;

                switch (op) {
                    case AND: return l && r;
                    case OR: return l || r;
                    case EQ: return l == r;
                    case NE: return l != r;
                }
            }
        } catch (Exception e) {
            // Evaluation failed
        }
        return null;
    }

    private Object evaluateUnOp(Op op, Object operand) {
        try {
            if (op == Op.NOT && operand instanceof Boolean) {
                return !(Boolean) operand;
            }
        } catch (Exception e) {
            // Evaluation failed
        }
        return null;
    }

    public int getFoldedCount() {
        return foldedCount;
    }
}

