package runtime;

import java.io.*;
import java.util.*;

public class RuntimePlayer {
    private Map<String, Object> memory;
    private Stack<Object> stack;
    private TurtleRuntime turtle;
    private Map<String, Integer> labels;
    private List<String> instructions;
    private int pc; // Program counter

    public RuntimePlayer() {
        this.memory = new HashMap<>();
        this.stack = new Stack<>();
        this.turtle = new TurtleRuntime();
        this.labels = new HashMap<>();
        this.instructions = new ArrayList<>();
        this.pc = 0;
    }

    public void loadFromFile(String path) throws IOException {
        instructions.clear();
        labels.clear();

        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            int lineNum = 0;

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                // Skip comments and empty lines
                if (line.isEmpty() || line.startsWith(";")) {
                    continue;
                }

                // Handle labels
                if (line.startsWith("label ")) {
                    String labelName = line.substring(6).trim();
                    labels.put(labelName, lineNum);
                } else {
                    instructions.add(line);
                    lineNum++;
                }
            }
        }
    }

    public void execute() {
        pc = 0;

        while (pc < instructions.size()) {
            String instr = instructions.get(pc);
            executeInstruction(instr);
            pc++;
        }
    }

    private void executeInstruction(String instr) {
        String[] parts = instr.split("\\s+", 2);
        String opcode = parts[0];
        String operands = parts.length > 1 ? parts[1] : "";

        switch (opcode) {
            case "MOV":
                executeMov(operands);
                break;
            case "BIN":
                executeBin(operands);
                break;
            case "UN":
                executeUn(operands);
                break;
            case "JMP":
                executeJmp(operands);
                break;
            case "JMPT":
                executeJmpt(operands);
                break;
            case "JMPTF":
                executeJmptf(operands);
                break;
            case "PUSH":
                executePush(operands);
                break;
            case "CALL":
                executeCall(operands);
                break;
            case "POP":
                executePop(operands);
                break;
            case "RET":
                executeRet(operands);
                break;
            default:
                // Unknown instruction
                break;
        }
    }

    private void executeMov(String operands) {
        String[] parts = operands.split(",\\s*");
        String dest = parts[0].trim();
        String src = parts[1].trim();

        Object value = getValue(src);
        memory.put(dest, value);
    }

    private void executeBin(String operands) {
        // BIN dest, left, OP, right
        String[] parts = operands.split(",\\s*");
        String dest = parts[0].trim();
        String left = parts[1].trim();
        String op = parts[2].trim();
        String right = parts[3].trim();

        Object leftVal = getValue(left);
        Object rightVal = getValue(right);
        Object result = evaluateBinOp(leftVal, op, rightVal);

        memory.put(dest, result);
    }

    private void executeUn(String operands) {
        // UN dest, OP, operand
        String[] parts = operands.split(",\\s*");
        String dest = parts[0].trim();
        String op = parts[1].trim();
        String operand = parts[2].trim();

        Object operandVal = getValue(operand);
        Object result = evaluateUnOp(op, operandVal);

        memory.put(dest, result);
    }

    private void executeJmp(String label) {
        Integer target = labels.get(label.trim());
        if (target != null) {
            pc = target - 1; // -1 because pc++ happens after
        }
    }

    private void executeJmpt(String operands) {
        String[] parts = operands.split(",\\s*");
        String condition = parts[0].trim();
        String label = parts[1].trim();

        Object condVal = getValue(condition);
        if (toBoolean(condVal)) {
            executeJmp(label);
        }
    }

    private void executeJmptf(String operands) {
        String[] parts = operands.split(",\\s*");
        String condition = parts[0].trim();
        String labelTrue = parts[1].trim();
        String labelFalse = parts[2].trim();

        Object condVal = getValue(condition);
        if (toBoolean(condVal)) {
            executeJmp(labelTrue);
        } else {
            executeJmp(labelFalse);
        }
    }

    private void executePush(String value) {
        Object val = getValue(value.trim());
        stack.push(val);
    }

    private void executeCall(String operands) {
        String[] parts = operands.split(",\\s*");
        String funcName = parts[0].trim();
        int numArgs = Integer.parseInt(parts[1].trim());

        // Pop arguments
        List<Object> args = new ArrayList<>();
        for (int i = 0; i < numArgs; i++) {
            args.add(0, stack.pop());
        }

        // Execute built-in functions
        Object result = executeBuiltinFunction(funcName, args);
        if (result != null) {
            stack.push(result);
        }
    }

    private void executePop(String dest) {
        Object value = stack.pop();
        memory.put(dest.trim(), value);
    }

    private void executeRet(String value) {
        if (!value.isEmpty()) {
            Object val = getValue(value.trim());
            stack.push(val);
        }
        // For now, just continue
    }

    private Object getValue(String operand) {
        // Check if it's in memory
        if (memory.containsKey(operand)) {
            return memory.get(operand);
        }

        // Try to parse as integer
        try {
            return Integer.parseInt(operand);
        } catch (NumberFormatException e) {
            // Not an integer
        }

        // Check for boolean
        if ("true".equals(operand)) return true;
        if ("false".equals(operand)) return false;

        // Check for string literal
        if (operand.startsWith("\"") && operand.endsWith("\"")) {
            return operand.substring(1, operand.length() - 1);
        }

        return 0; // Default
    }

    private Object evaluateBinOp(Object left, String op, Object right) {
        if (left instanceof Integer && right instanceof Integer) {
            int l = (Integer) left;
            int r = (Integer) right;

            switch (op) {
                case "ADD": return l + r;
                case "SUB": return l - r;
                case "MUL": return l * r;
                case "DIV": return r != 0 ? l / r : 0;
                case "POW": return (int) Math.pow(l, r);
                case "EQ": return l == r;
                case "NE": return l != r;
                case "LT": return l < r;
                case "LE": return l <= r;
                case "GT": return l > r;
                case "GE": return l >= r;
            }
        } else if (left instanceof Boolean && right instanceof Boolean) {
            boolean l = (Boolean) left;
            boolean r = (Boolean) right;

            switch (op) {
                case "AND": return l && r;
                case "OR": return l || r;
                case "EQ": return l == r;
                case "NE": return l != r;
            }
        }

        return 0;
    }

    private Object evaluateUnOp(String op, Object operand) {
        if ("NOT".equals(op) && operand instanceof Boolean) {
            return !(Boolean) operand;
        }
        return operand;
    }

    private boolean toBoolean(Object value) {
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        if (value instanceof Integer) {
            return ((Integer) value) != 0;
        }
        return false;
    }

    private Object executeBuiltinFunction(String funcName, List<Object> args) {
        switch (funcName) {
            case "println":
                if (!args.isEmpty()) {
                    System.out.println(args.get(0));
                }
                return null;

            case "random":
                if (!args.isEmpty()) {
                    int max = toInt(args.get(0));
                    return new Random().nextInt(max);
                }
                return 0;

            case "turtle_avanza":
                if (!args.isEmpty()) {
                    turtle.avanza(toInt(args.get(0)));
                }
                return null;

            case "turtle_retrocede":
                if (!args.isEmpty()) {
                    turtle.retrocede(toInt(args.get(0)));
                }
                return null;

            case "turtle_giraderecha":
                if (!args.isEmpty()) {
                    turtle.giraDerecha(toInt(args.get(0)));
                }
                return null;

            case "turtle_giraizquierda":
                if (!args.isEmpty()) {
                    turtle.giraIzquierda(toInt(args.get(0)));
                }
                return null;

            case "turtle_oculta":
                turtle.oculta();
                return null;

            case "turtle_ponpos":
                if (args.size() >= 2) {
                    turtle.ponPos(toInt(args.get(0)), toInt(args.get(1)));
                }
                return null;

            case "turtle_ponx":
                if (!args.isEmpty()) {
                    turtle.ponX(toInt(args.get(0)));
                }
                return null;

            case "turtle_pony":
                if (!args.isEmpty()) {
                    turtle.ponY(toInt(args.get(0)));
                }
                return null;

            case "turtle_ponrumbo":
                if (!args.isEmpty()) {
                    turtle.ponRumbo(toInt(args.get(0)));
                }
                return null;

            case "turtle_rumbo":
                System.out.println("Rumbo: " + turtle.getRumbo());
                return null;

            case "turtle_bajalapiz":
                turtle.bajaLapiz();
                return null;

            case "turtle_subelapiz":
                turtle.subeLapiz();
                return null;

            case "turtle_colorlapiz":
                if (!args.isEmpty()) {
                    turtle.setColor(args.get(0).toString());
                }
                return null;

            case "turtle_centro":
                turtle.centro();
                return null;

            case "turtle_espera":
                if (!args.isEmpty()) {
                    turtle.espera(toInt(args.get(0)));
                }
                return null;

            default:
                return null;
        }
    }

    private int toInt(Object value) {
        if (value instanceof Integer) {
            return (Integer) value;
        }
        return 0;
    }

    public TurtleRuntime getTurtle() {
        return turtle;
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java runtime.RuntimePlayer <object-file>");
            return;
        }

        try {
            RuntimePlayer player = new RuntimePlayer();
            player.loadFromFile(args[0]);

            System.out.println("=== EXECUTING COMPILED PROGRAM ===");
            player.execute();
            System.out.println("\n=== EXECUTION COMPLETE ===");

            // Show turtle viewer if there were drawing commands
            if (player.getTurtle().hasDrawn()) {
                TurtleViewer viewer = new TurtleViewer(player.getTurtle());
                viewer.show();
            }

        } catch (IOException e) {
            System.err.println("Error loading object file: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Runtime error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

