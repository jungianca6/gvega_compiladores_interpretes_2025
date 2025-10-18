package runtime;

import java.util.ArrayList;
import java.util.List;

public class TurtleRuntime {
    private int x, y;
    private int angle;
    private boolean lapizAbajo;
    private String color;
    private List<DrawCommand> commands;

    public TurtleRuntime() {
        this.x = 0;
        this.y = 0;
        this.angle = 0;
        this.lapizAbajo = false;
        this.color = "negro";
        this.commands = new ArrayList<>();
    }

    public void avanza(int pasos) {
        int oldX = x, oldY = y;
        x += (int)(pasos * Math.cos(Math.toRadians(angle)));
        y -= (int)(pasos * Math.sin(Math.toRadians(angle)));

        if (lapizAbajo) {
            commands.add(new DrawCommand("line", oldX, oldY, x, y, color));
            System.out.println("Dibujando línea desde (" + oldX + "," + oldY +
                             ") hasta (" + x + "," + y + ") con color " + color);
        } else {
            System.out.println("Moviendo sin dibujar a (" + x + ", " + y + ")");
        }
    }

    public void retrocede(int pasos) {
        avanza(-pasos);
    }

    public void giraDerecha(int grados) {
        angle = (angle - grados + 360) % 360;
        System.out.println("Girando derecha " + grados + "°, rumbo actual: " + angle + "°");
    }

    public void giraIzquierda(int grados) {
        angle = (angle + grados) % 360;
        System.out.println("Girando izquierda " + grados + "°, rumbo actual: " + angle + "°");
    }

    public void oculta() {
        System.out.println("Tortuga ocultada");
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
    }

    public void subeLapiz() {
        this.lapizAbajo = false;
        System.out.println("Lápiz subido - dejando de dibujar");
    }

    public void setColor(String color) {
        this.color = color;
        System.out.println("Color establecido: " + color);
    }

    public void centro() {
        this.x = 400;
        this.y = 300;
        System.out.println("Tortuga en el centro: (400, 300)");
    }

    public void espera(int ticks) {
        try {
            int ms = (ticks * 1000 / 60);
            Thread.sleep(ms);
            System.out.println("Esperando " + ticks + " ticks (" + ms + " ms)");
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

