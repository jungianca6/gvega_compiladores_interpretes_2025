package ast.Instrucciones;

public class Turtle {
    private int x, y;
    private int angle; // 0 = derecha, 90 = arriba, etc.
    private boolean visible;

    public Turtle(int startX, int startY, int startAngle) {
        this.x = startX;
        this.y = startY;
        this.angle = startAngle;
        this.visible = true;
    }

    public void moveForward(int pasos) {
        x += (int)(pasos * Math.cos(Math.toRadians(angle)));
        y -= (int)(pasos * Math.sin(Math.toRadians(angle)));
        System.out.println("Nueva posición: (" + x + ", " + y + ")");
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
