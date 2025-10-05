import java.util.ArrayList;
import java.util.List;

public class ErrorReporter {
    private List<String> errors;

    public ErrorReporter() {
        errors = new ArrayList<>();
    }

    public void reportError(int line, String message) {
        String error = "Error en línea " + line + ": " + message;
        errors.add(error);
        System.err.println(error);
    }

    public void reportError(String message) {
        String error = "Error: " + message;
        errors.add(error);
        System.err.println(error);
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public List<String> getErrors() {
        return errors;
    }

    public void printErrors() {
        if (errors.isEmpty()) {
            System.out.println("No se encontraron errores semánticos.");
        } else {
            System.out.println("\n=== ERRORES SEMÁNTICOS ENCONTRADOS ===");
            for (String error : errors) {
                System.out.println(error);
            }
        }
    }
}