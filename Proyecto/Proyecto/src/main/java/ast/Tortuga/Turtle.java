package ast.Tortuga;

import java.util.Set;

public class Turtle {
    private int x, y;
    private int angle; // 0 = derecha, 90 = arriba, etc.
    private boolean visible;
    private boolean lapizAbajo; // true = dibujando, false = no dibuja
    private String colorLapiz; // "negro", "azul", "rojo"

    public Turtle(int startX, int startY, int startAngle) {
        this.x = startX;
        this.y = startY;
        this.angle = startAngle;
        this.visible = true;
        this.lapizAbajo = false;
        this.colorLapiz = "negro";
    }

    public void moveForward(int pasos) {
        int oldX = x, oldY = y;
        x += (int)(pasos * Math.cos(Math.toRadians(angle)));
        y -= (int)(pasos * Math.sin(Math.toRadians(angle)));
        if (lapizAbajo) {
            System.out.println("Dibujando línea desde (" + oldX + "," + oldY +
                    ") hasta (" + x + "," + y + ") con color " + colorLapiz);
        } else {
            System.out.println("Moviendo sin dibujar a (" + x + ", " + y + ")");
        }
    }

    public void moveBackward(int pasos) {
        moveForward(-pasos);
    }

    public void turnLeft(int grados) {
        angle = (angle + grados) % 360;
    }

    public void turnRight(int grados) {
        angle = (angle - grados + 360) % 360;
    }


    public void setPosition(int newX, int newY) {
        this.x = newX;
        this.y = newY;
    }

    // Métodos para el lápiz
    public void bajarLapiz() {
        this.lapizAbajo = true;
    }

    public void subirLapiz() {
        this.lapizAbajo = false;
    }

    private static final Set<String> COLORES_VALIDOS = Set.of(
            "negro", "azul", "rojo", "verde", "amarillo",
            "naranja", "morado", "cyan", "rosa", "celeste", "gris"
    );

    public void setColorLapiz(String color) {
        if (!COLORES_VALIDOS.contains(color)) {
            throw new RuntimeException("Color no válido. Colores permitidos: " + COLORES_VALIDOS);
        }
        this.colorLapiz = color;
    }
    public void moverCentro() {
        this.x = 400;
        this.y = 300;
    }

    public void esperar(int n) {
        try {
            int ms = (n * 1000 / 60); // n/60 segundos
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }



    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getAngle() {
        return angle;
    }

    public void setAngle(int newAngle) {
        this.angle = newAngle % 360;
    }

    public void resetToInitialPosition() {
        this.x = 0;       // esquina superior izquierda
        this.y = 0;       // esquina superior izquierda
        this.angle = 0;   // dirección inicial (derecha)
        this.visible = false; // oculta la tortuga
    }

    public void hide() {
        visible = false;
    }

    public void show() {
        visible = true;
    }
}
