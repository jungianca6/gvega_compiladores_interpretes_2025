package ast.Aritmeticos;
import ast.ASTNode;

import java.util.Map;
import java.util.Random;
public class Rand implements ASTNode {
    private ASTNode number;

    public Rand(ASTNode number) {
        super();
        this.number = number;
    }

    @Override
    public Object execute(Map<String, Object> symbolTable){

        Random rand = new Random();

        int num = rand.nextInt((int)number.execute(symbolTable)+1);

        System.out.println(num);
        return num;
    }

}
