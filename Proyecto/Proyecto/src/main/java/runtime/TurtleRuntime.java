package runtime;

import java.util.ArrayList;
import java.util.List;

public class TurtleRuntime {
    private int x, y;
    private int angle;
    private boolean lapizAbajo;
    private String color;
    private List<DrawCommand> commands;
    private ArduinoController arduino;

    public TurtleRuntime() {
        this.x = 0;
        this.y = 0;
        this.angle = 0;
        this.lapizAbajo = false;
        this.color = "negro";
        this.commands = new ArrayList<>();
        this.arduino = null; // Se inicializa solo si se conecta
    }

    /**
     * Conecta con el Arduino en el puerto especificado
     */
    public boolean conectarArduino(String host) {
        if (arduino == null) arduino = new ArduinoController();
        boolean ok = arduino.connectWiFi(host, 5000);
        if (ok) arduino.iniciar();
        return ok;
    }

    /**
     * Desconecta del Arduino
     */
    public void desconectarArduino() {
        if (arduino != null && arduino.isConnected()) {
            arduino.finalizar();
            arduino.disconnect();
        }
    }

    /**
     * Obtiene el resumen de comandos enviados al Arduino
     */
    public void mostrarResumenArduino() {
        if (arduino != null) {
            arduino.printSummary();
        }
    }

    public void avanza(int pasos) {
        int oldX = x, oldY = y;
        // El cálculo de Y es NEGATIVO porque en coordenadas de pantalla,
        // la dirección positiva es hacia abajo. Asumimos que 0° es Este.
        x += (int)(pasos * Math.cos(Math.toRadians(angle)));
        y -= (int)(pasos * Math.sin(Math.toRadians(angle))); // Invertir Y para simular coordenadas cartesianas

        if (lapizAbajo) {
            commands.add(new DrawCommand("line", oldX, oldY, x, y, color));
            System.out.println("Dibujando línea desde (" + oldX + "," + oldY +
                    ") hasta (" + x + "," + y + ") con color " + color);
        } else {
            System.out.println("Moviendo sin dibujar a (" + x + ", " + y + ")");
        }

        // Enviar al Arduino si está conectado
        if (arduino != null && arduino.isConnected()) {
            arduino.avanzar(pasos);
        }
    }

    public void retrocede(int pasos) {
        avanza(-pasos);
        // Enviar al Arduino si está conectado
        if (arduino != null && arduino.isConnected()) {
            arduino.retroceder(pasos);
        }
    }

    public void giraDerecha(int grados) {
        // Restar grados para girar a la derecha
        angle = (angle - grados + 360) % 360;
        System.out.println("Girando derecha " + grados + "°, rumbo actual: " + angle + "°");

        // Enviar al Arduino si está conectado
        if (arduino != null && arduino.isConnected()) {
            arduino.girarDerecha(grados);
        }
    }

    public void giraIzquierda(int grados) {
        // Sumar grados para girar a la izquierda
        angle = (angle + grados) % 360;
        System.out.println("Girando izquierda " + grados + "°, rumbo actual: " + angle + "°");

        // Enviar al Arduino si está conectado
        if (arduino != null && arduino.isConnected()) {
            arduino.girarIzquierda(grados);
        }
    }

    public void oculta() {
        // No hay funcionalidad visual de ocultar/mostrar en este motor de runtime,
        // solo se registra la acción.
        System.out.println("Tortuga ocultada (solo registro de comando)");
    }

    public void ponPos(int newX, int newY) {
        this.x = newX;
        this.y = newY;
        System.out.println("Posición establecida: (" + x + ", " + y + ")");
    }

    public void ponX(int newX) {
        this.x = newX;
        System.out.println("X establecida: " + x);
    }

    public void ponY(int newY) {
        this.y = newY;
        System.out.println("Y establecida: " + y);
    }

    public void ponRumbo(int newAngle) {
        this.angle = newAngle % 360;
        System.out.println("Rumbo establecido: " + angle + "°");
    }

    public int getRumbo() {
        return angle;
    }

    public void bajaLapiz() {
        this.lapizAbajo = true;
        System.out.println("Lápiz bajado - comenzando a dibujar");

        // Enviar al Arduino si está conectado
        if (arduino != null && arduino.isConnected()) {
            arduino.bajarLapiz();
        }
    }

    public void subeLapiz() {
        this.lapizAbajo = false;
        System.out.println("Lápiz subido - dejando de dibujar");

        // Enviar al Arduino si está conectado
        if (arduino != null && arduino.isConnected()) {
            arduino.subirLapiz();
        }
    }

    public void setColor(String color) {
        this.color = color;
        System.out.println("Color establecido: " + color);

        // Enviar al Arduino si está conectado
        if (arduino != null && arduino.isConnected()) {
            arduino.cambiarColor(color);
        }
    }

    /**
     * Mueve la tortuga al centro del sistema de coordenadas del modelo (0, 0).
     * El TurtleViewer se encarga de centrar esta posición en la pantalla.
     */
    public void centro() {
        this.x = 0; // Se cambia a 0, ya que el modelo usa (0,0) como centro
        this.y = 0; // Se cambia a 0
        this.angle = 0; // Restablecer el ángulo a 0 (Este)
        System.out.println("Tortuga en el centro: (0, 0). Rumbo restablecido a 0°.");

        // Enviar al Arduino si está conectado
        if (arduino != null && arduino.isConnected()) {
            arduino.centro();
        }
    }

    public void espera(int ticks) {
        try {
            int ms = (ticks * 1000 / 60);
            Thread.sleep(ms);
            System.out.println("Esperando " + ticks + " ticks (" + ms + " ms)");

            // Enviar al Arduino si está conectado
            if (arduino != null && arduino.isConnected()) {
                arduino.esperar(ms);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public boolean hasDrawn() {
        return !commands.isEmpty();
    }

    public List<DrawCommand> getCommands() {
        return commands;
    }

    /**
     * Clase interna para registrar comandos de dibujo.
     */
    public static class DrawCommand {
        public String type;
        public int x1, y1, x2, y2;
        public String color;

        public DrawCommand(String type, int x1, int y1, int x2, int y2, String color) {
            this.type = type;
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
            this.color = color;
        }
    }
}
