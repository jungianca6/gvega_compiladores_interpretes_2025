package ast.Tortuga;

import ast.ASTNode;
import java.util.Map;


public class ColorLapiz implements ASTNode {

    private String color;

    public ColorLapiz(String color) {
        this.color = color;
    }

    @Override
    public Object execute(Map<String, Object> symbolTable) {
        Turtle t = (Turtle) symbolTable.get("turtle");
        if (t == null) {
            throw new RuntimeException("No se inicializó la tortuga");
        }

        t.setColorLapiz(color);
        System.out.println("Color del lápiz cambiado a: " + color);
        return null;
    }
}