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
    private final List<String> globalInstructions = new ArrayList<>();
    private final Map<String, List<String>> functions = new HashMap<>();

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
        globalInstructions.clear();
        functions.clear();

        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            List<String> currentFunc = null;

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                // Skip comments and empty lines
                if (line.isEmpty() || line.startsWith(";")) {
                    continue;
                }

                // Handle labels
                if (line.startsWith("label func_")) {
                    String labelName = line.substring(6).trim();
                    currentFunc = new ArrayList<>();
                    functions.put(labelName, currentFunc);
                    continue;
                }

                if (currentFunc != null) {
                    // Estamos dentro de una función
                    currentFunc.add(line);
                } else {
                    // Parte global
                    globalInstructions.add(line);
                }
            }
        }
    }

    public void execute() {
        pc = 0;

        while (pc < globalInstructions.size()) {
            String instr = globalInstructions.get(pc);
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
        if (result != null || isBuiltin(funcName)) {
            if (result != null)
                stack.push(result);
            return;
        }

        String labelName = "func_" + funcName;
        List<String> funcBody = functions.get(labelName);
        if (funcBody == null) {
            System.err.println("Runtime error: función '" + funcName + "' no encontrada.");
            return;
        }

        // Frame local: guarda el entorno anterior y usa uno nuevo
        Map<String, Object> prevMemory = memory;
        memory = new HashMap<>();

        List<String> paramNames = extractParamNames(labelName);

        for (int i = 0; i < numArgs && i < paramNames.size(); i++) {
            memory.put(paramNames.get(i), args.get(i));
        }

        for (String line : funcBody) {
            String op = line.split("\\s+", 2)[0];
            if ("RET".equals(op)) {
                break;
            }
            executeInstruction(line);
        }

        // Restaura entorno anterior
        memory = prevMemory;
    }

    private boolean isBuiltin(String name) {
        switch (name) {
            case "println": case "random":
            case "turtle_avanza": case "turtle_retrocede":
            case "turtle_giraderecha": case "turtle_giraizquierda":
            case "turtle_oculta": case "turtle_ponpos": case "turtle_ponx":
            case "turtle_pony": case "turtle_ponrumbo": case "turtle_rumbo":
            case "turtle_bajalapiz": case "turtle_subelapiz":
            case "turtle_colorlapiz": case "turtle_centro": case "turtle_espera":
                return true;
            default:
                return false;
        }
    }

    private List<String> extractParamNames(String funcLabel) {
        List<String> funcBody = functions.get(funcLabel);
        if (funcBody == null) return Collections.emptyList();

        Set<String> assigned = new HashSet<>();
        Set<String> used = new LinkedHashSet<>(); // preserva orden de aparición

        for (String line : funcBody) {
            // Tokeniza separando por comas y espacios
            String[] parts = line.split("[,\\s]+");
            if (parts.length == 0) continue;

            String op = parts[0];

            switch (op) {
                case "MOV": {
                    // MOV dest, src
                    // parts: [0]=MOV, [1]=dest, [2]=src
                    if (parts.length >= 2 && isIdentifier(parts[1])) {
                        assigned.add(parts[1]); // dest es local
                    }
                    if (parts.length >= 3 && isIdentifier(parts[2])) {
                        used.add(parts[2]); // src puede ser param u otra var
                    }
                    break;
                }

                case "BIN": {
                    // BIN dest, left, OP, right
                    // parts: [0]=BIN, [1]=dest, [2]=left, [3]=OP, [4]=right
                    if (parts.length >= 2 && isIdentifier(parts[1])) {
                        assigned.add(parts[1]); // dest (t0 normalmente) no es param
                    }
                    if (parts.length >= 3 && isIdentifier(parts[2])) {
                        used.add(parts[2]); // left
                    }
                    // parts[3] es el opcode (ADD/SUB/POW/...), ignorar
                    if (parts.length >= 5 && isIdentifier(parts[4])) {
                        used.add(parts[4]); // right (aquí entra 'a')
                    }
                    break;
                }

                case "UN": {
                    // UN dest, OP, operand
                    // parts: [0]=UN, [1]=dest, [2]=OP, [3]=operand
                    if (parts.length >= 2 && isIdentifier(parts[1])) {
                        assigned.add(parts[1]); // dest
                    }
                    if (parts.length >= 4 && isIdentifier(parts[3])) {
                        used.add(parts[3]); // operand
                    }
                    break;
                }

                case "PUSH": {
                    // PUSH value
                    // parts: [0]=PUSH, [1]=value
                    if (parts.length >= 2 && isIdentifier(parts[1])) {
                        used.add(parts[1]);
                    }
                    break;
                }

                // Otros opcodes que no afectan: CALL, JMP, JMPT, JMPTF, RET...
                default:
                    break;
            }
        }

        // Parámetros = usados pero no asignados dentro de la función
        used.removeAll(assigned);
        return new ArrayList<>(used);
    }

    // Helpers:
    private static final Set<String> OP_TOKENS = new HashSet<>(
            Arrays.asList("ADD","SUB","MUL","DIV","POW","EQ","NE","LT","LE","GT","GE","AND","OR","NOT")
    );

    private boolean isIdentifier(String s) {
        // identificador simple tipo [a-zA-Z_][a-zA-Z0-9_]*
        if (s == null) return false;
        if (!s.matches("[a-zA-Z_][a-zA-Z0-9_]*")) return false;
        // excluir opcodes (POW, ADD, etc.)
        if (OP_TOKENS.contains(s)) return false;
        // excluir temporales tipo t0, t1, t23...
        if (s.startsWith("t") && s.substring(1).matches("\\d+")) return false;
        return true;
    }

    private void executePop(String dest) {
        Object value = stack.pop();
        memory.put(dest.trim(), value);
    }

    private void executeRet(String value) {
        Object retVal = null;
        if (!value.isEmpty()) {
            retVal = getValue(value.trim());
            stack.push(retVal);
        }

        // Recuperar entorno previo y dirección de retorno
        if (!stack.isEmpty()) {
            Object oldMemObj = stack.pop();
            if (oldMemObj instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> oldMemory = (Map<String, Object>) oldMemObj;
                memory = oldMemory;
            }
        }

        if (!stack.isEmpty()) {
            Object retAddrObj = stack.pop();
            if (retAddrObj instanceof Integer) {
                pc = (Integer) retAddrObj;
            }
        }

        if (retVal != null)
            stack.push(retVal);
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
                viewer.display();
            }

        } catch (IOException e) {
            System.err.println("Error loading object file: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Runtime error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

