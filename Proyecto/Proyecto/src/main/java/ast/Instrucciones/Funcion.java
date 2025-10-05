package ast.Instrucciones;

import ast.ASTNode;


import java.util.Map;
import java.util.List;
import java.util.*;

public class Funcion implements ASTNode {
    private String nombre;
    private List<String> argumentos;
    private Map<String, Object>  symbolTable;
    private List<ASTNode> cuerpo;

    public Funcion(String nombre, List<String> argumentos, List<ASTNode> cuerpo) {
        super();
        this.nombre = nombre;
        this.argumentos = argumentos;
        this.cuerpo = cuerpo;
    }

    public String getNombre() {
        return nombre;
    }

    public List<String> getParametros() {
        return argumentos;
    }

    public Map<String, Object>  getSymbolTable() {
        return symbolTable;
    }

    public List<ASTNode> getCuerpo() {
        return cuerpo;
    }

    @Override
    public Object execute(Map<String, Object>  symbolTable)
    {
        this.symbolTable = symbolTable;
        this.symbolTable.put(nombre, this);

        return null;
    }



}