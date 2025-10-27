package runtime;

import java.io.*;
import java.util.*;

public class RuntimePlayer {
    private Map<String, Object> memory;
    private Stack<Object> stack;
    private TurtleRuntime turtle;
    private Map<String, Integer> labels;
    private List<String> instructions;
    private Map<String, Integer> functions; // nombre → índice de inicio
    private int pc; // Program counter

    public RuntimePlayer() {
        this.memory = new HashMap<>();
        this.stack = new Stack<>();
        this.turtle = new TurtleRuntime();
        this.labels = new HashMap<>();
        this.instructions = new ArrayList<>();
        this.functions = new HashMap<>();
        this.pc = 0;
    }

    // ===========================================================
    // Carga de archivo .lobj
    // ===========================================================
    public void loadFromFile(String path) throws IOException {
        instructions.clear();
        labels.clear();
        functions.clear();

        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            int lineNum = 0;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith(";")) continue;

                if (line.startsWith("label func_")) {
                    String funcName = line.trim().substring(6).trim();
                    functions.put(funcName, lineNum);
                } else if (line.startsWith("label ")) {
                    String label = line.substring(6).trim();
                    labels.put(label, lineNum);
                }

                instructions.add(line);
                lineNum++;
            }
        }
    }

    // ===========================================================
    // Ejecución principal
    // ===========================================================
    public void execute() {
        pc = 0;

        while (pc < instructions.size()) {
            String instr = instructions.get(pc);

            // Si llegamos a una función, terminamos la ejecución global
            if (instr.startsWith("label func_")) {
                break;
            }

            executeInstruction(instr);
            pc++;
        }
    }

    // ===========================================================
    // Ejecución de una instrucción
    // ===========================================================
    private void executeInstruction(String instr) {
        String[] parts = instr.split("\\s+", 2);
        String opcode = parts[0];
        String operands = parts.length > 1 ? parts[1] : "";

        switch (opcode) {
            case "MOV": executeMov(operands); break;
            case "BIN": executeBin(operands); break;
            case "UN": executeUn(operands); break;
            case "JMP": executeJmp(operands); break;
            case "JMPT": executeJmpt(operands); break;
            case "JMPTF": executeJmptf(operands); break;
            case "PUSH": executePush(operands); break;
            case "CALL": executeCall(operands); break;
            case "POP": executePop(operands); break;
            case "RET": executeRet(operands); break;
            default: break;
        }
    }

    // ===========================================================
    // MOV, BIN, UN
    // ===========================================================
    private void executeMov(String operands) {
        String[] parts = operands.split(",\\s*");
        String dest = parts[0].trim();
        String src = parts[1].trim();
        Object value = getValue(src);
        memory.put(dest, value);
    }

    private void executeBin(String operands) {
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
        String[] parts = operands.split(",\\s*");
        String dest = parts[0].trim();
        String op = parts[1].trim();
        String operand = parts[2].trim();

        Object operandVal = getValue(operand);
        Object result = evaluateUnOp(op, operandVal);
        memory.put(dest, result);
    }

    // ===========================================================
    // Control de flujo
    // ===========================================================
    private void executeJmp(String label) {
        Integer target = labels.get(label.trim());
        if (target != null) pc = target - 1;
    }

    private void executeJmpt(String operands) {
        String[] parts = operands.split(",\\s*");
        String cond = parts[0].trim();
        String label = parts[1].trim();

        if (toBoolean(getValue(cond))) executeJmp(label);
    }

    private void executeJmptf(String operands) {
        String[] parts = operands.split(",\\s*");
        String cond = parts[0].trim();
        String labelTrue = parts[1].trim();
        String labelFalse = parts[2].trim();

        boolean val = toBoolean(getValue(cond));
        executeJmp(val ? labelTrue : labelFalse);
    }

    // ===========================================================
    // Pila y llamadas
    // ===========================================================
    private void executePush(String value) {
        stack.push(getValue(value.trim()));
    }

    private void executePop(String dest) {
        memory.put(dest.trim(), stack.pop());
    }

    private void executeCall(String operands) {
        String[] parts = operands.split(",\\s*");
        String funcName = parts[0].trim();
        int numArgs = Integer.parseInt(parts[1].trim());

        // Obtener argumentos
        List<Object> args = new ArrayList<>();
        for (int i = 0; i < numArgs; i++) args.add(0, stack.pop());

        // Ver si es builtin
        Object result = executeBuiltinFunction(funcName, args);
        if (result != null || isBuiltin(funcName)) {
            if (result != null) stack.push(result);
            return;
        }

        // Es función definida
        Integer start = functions.get("func_" + funcName);
        if (start == null) {
            System.err.println("Runtime error: función '" + funcName + "' no encontrada.");
            return;
        }

        // Guardar contexto y dirección de retorno
        stack.push(new HashMap<>(memory));
        stack.push(pc);

        // Nuevo entorno de variables
        memory = new HashMap<>();
        List<String> paramNames = extractParamNames("func_" + funcName);
        System.out.println("PARAMS DETECTADOS para " + funcName + ": " + paramNames);

        for (int i = 0; i < numArgs && i < paramNames.size(); i++) {
            memory.put(paramNames.get(i), args.get(i));
        }

        // Saltar al inicio de la función
        pc = start;
    }

    private void executeRet(String value) {
        Object retVal = null;
        if (!value.isEmpty()) retVal = getValue(value.trim());

        if (!stack.isEmpty() && stack.peek() instanceof Integer) {
            pc = (Integer) stack.pop();
        }
        if (!stack.isEmpty() && stack.peek() instanceof Map) {
            memory = (Map<String, Object>) stack.pop();
        }

        if (retVal != null) stack.push(retVal);
    }

    // ===========================================================
    // Evaluadores
    // ===========================================================
    private Object getValue(String operand) {
        if (memory.containsKey(operand)) return memory.get(operand);

        try { return Integer.parseInt(operand); } catch (NumberFormatException ignored) {}
        if ("true".equals(operand)) return true;
        if ("false".equals(operand)) return false;
        if (operand.startsWith("\"") && operand.endsWith("\""))
            return operand.substring(1, operand.length() - 1);

        return 0;
    }

    private Object evaluateBinOp(Object l, String op, Object r) {
        if (l instanceof Integer && r instanceof Integer) {
            int a = (Integer) l, b = (Integer) r;
            switch (op) {
                case "ADD": return a + b;
                case "SUB": return a - b;
                case "MUL": return a * b;
                case "DIV": return b != 0 ? a / b : 0;
                case "POW": return (int) Math.pow(a, b);
                case "EQ": return a == b;
                case "NE": return a != b;
                case "LT": return a < b;
                case "LE": return a <= b;
                case "GT": return a > b;
                case "GE": return a >= b;
            }
        }
        return 0;
    }

    private Object evaluateUnOp(String op, Object operand) {
        if ("NOT".equals(op)) {
            if (operand instanceof Boolean) {
                return !(Boolean) operand;
            }
            if (operand instanceof Integer) {
                return ((Integer) operand == 0) ? 1 : 0; // invierte 0↔1
            }
        }
        return operand;
    }

    private boolean toBoolean(Object v) {
        if (v instanceof Boolean) return (Boolean) v;
        if (v instanceof Integer) return (Integer) v != 0;
        return false;
    }

    // ===========================================================
    // Funciones integradas y parámetros
    // ===========================================================
    private boolean isBuiltin(String name) {
        return Set.of(
                "println","random",
                "turtle_avanza","turtle_retrocede","turtle_giraderecha","turtle_giraizquierda",
                "turtle_oculta","turtle_ponpos","turtle_ponx","turtle_pony","turtle_ponrumbo",
                "turtle_rumbo","turtle_bajalapiz","turtle_subelapiz","turtle_colorlapiz",
                "turtle_centro","turtle_espera"
        ).contains(name);
    }

    private List<String> extractParamNames(String funcLabel) {
        // Buscar el cuerpo de la función dentro de instructions
        Integer start = functions.get(funcLabel);
        if (start == null) return Collections.emptyList();

        Set<String> assigned = new HashSet<>();
        Set<String> used = new LinkedHashSet<>();

        for (int i = start + 1; i < instructions.size(); i++) {
            String line = instructions.get(i).trim();
            if (line.startsWith("label func_") && i != start) break;
            if (line.isEmpty() || line.startsWith(";")) continue;
            if (line.equals("RET")) break;

            String[] parts = line.split("[,\\s]+");
            if (parts.length == 0) continue;
            String op = parts[0];

            switch (op) {
                case "MOV":
                    if (parts.length >= 2) assigned.add(parts[1]);
                    if (parts.length >= 3 && !isLiteral(parts[2])) used.add(parts[2]);
                    break;
                case "BIN":
                    if (parts.length >= 2) assigned.add(parts[1]);
                    if (parts.length >= 3 && !isLiteral(parts[2])) used.add(parts[2]);
                    if (parts.length >= 5 && !isLiteral(parts[4])) used.add(parts[4]);
                    break;
                case "PUSH":
                    if (parts.length >= 2 && !isLiteral(parts[1])) used.add(parts[1]);
                    break;
            }
        }

        used.removeAll(assigned);
        return new ArrayList<>(used);
    }

    private boolean isLiteral(String token) {
        if (token == null) return true;
        token = token.trim();
        if (token.isEmpty()) return true;
        if (token.matches("-?\\d+")) return true;           // número
        if (token.startsWith("\"") && token.endsWith("\"")) return true; // cadena
        if ("true".equals(token) || "false".equals(token)) return true;  // booleanos
        return false;
    }

    // ===========================================================
    // Built-in turtle y utilidades
    // ===========================================================
    private Object executeBuiltinFunction(String f, List<Object> args) {
        switch (f) {
            case "println":
                if (!args.isEmpty()) System.out.println(args.get(0));
                return null;
            case "random":
                if (!args.isEmpty()) return new Random().nextInt(toInt(args.get(0)));
                return 0;
            case "turtle_avanza":
                if (!args.isEmpty()) turtle.avanza(toInt(args.get(0)));
                return null;
            case "turtle_retrocede":
                if (!args.isEmpty()) turtle.retrocede(toInt(args.get(0)));
                return null;
            case "turtle_giraderecha":
                if (!args.isEmpty()) turtle.giraDerecha(toInt(args.get(0)));
                return null;
            case "turtle_giraizquierda":
                if (!args.isEmpty()) turtle.giraIzquierda(toInt(args.get(0)));
                return null;
            case "turtle_bajalapiz": turtle.bajaLapiz(); return null;
            case "turtle_subelapiz": turtle.subeLapiz(); return null;
            case "turtle_colorlapiz":
                if (!args.isEmpty()) turtle.setColor(args.get(0).toString());
                return null;
            case "turtle_centro": turtle.centro(); return null;
            case "turtle_espera":
                if (!args.isEmpty()) turtle.espera(toInt(args.get(0)));
                return null;
            default: return null;
        }
    }

    private int toInt(Object v) {
        if (v instanceof Integer) return (Integer) v;
        return 0;
    }

    public TurtleRuntime getTurtle() { return turtle; }

    // ===========================================================
    // MAIN
    // ===========================================================
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

            if (player.getTurtle().hasDrawn()) {
                TurtleViewer viewer = new TurtleViewer(player.getTurtle());

                // Muestra la ventana (opcional)
                viewer.display();

                // ** MODIFICACIÓN PARA GENERAR NOMBRE DE ARCHIVO ÚNICO **
                // Genera un timestamp para asegurar un nombre único
                long timestamp = System.currentTimeMillis();

                // Crea el path con la marca de tiempo (timestamp)
                String imagePath = "IMGResultados/Dibujo" + timestamp + ".png";
                boolean success = viewer.saveImage(imagePath);

                if (success) {
                    System.out.println("IMAGEN GUARDADA EXITOSAMENTE:");
                    System.out.println("   → Ruta: " + new File(imagePath).getAbsolutePath());
                } else {
                    System.err.println("ERROR al guardar la imagen. Revisa si tienes permisos de escritura.");
                }
            } else {
                System.out.println("El programa no generó comandos de dibujo de tortuga.");
            }

        } catch (IOException e) {
            System.err.println("Error loading object file: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Runtime error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
