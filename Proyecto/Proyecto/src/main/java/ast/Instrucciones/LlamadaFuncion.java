package ast.Instrucciones;

import ast.ASTNode;
import java.util.Map;
import java.util.List;
import java.util.*;

public class LlamadaFuncion implements ASTNode {
    private String nombre;
    private List<ASTNode> parametros;

    public LlamadaFuncion(String nombre, List<ASTNode> parametros) {
        super();
        this.nombre = nombre;
        this.parametros = parametros;
    }

    @Override
    public Object execute(Map<String, Object>  symbolTable)
    {

        Funcion func = (Funcion)symbolTable.get(nombre);
        if (func == null) {
            throw new RuntimeException("La función '" + nombre + "' no está definida");
        }

        Map<String, Object> local_context = new HashMap<>(symbolTable);

        // Vincular parámetros formales con valores reales
        List<String> formalParams = func.getParametros();
        List<ASTNode> actualParams = this.parametros;


        for (int i = 0; i < formalParams.size(); i++) {
            Object value = actualParams.get(i).execute(symbolTable);
            local_context.put(formalParams.get(i), value);
        }

        Object result = null;
        for (ASTNode n : func.getCuerpo()) {
            result = n.execute(local_context);
        }
        return result;
    }

}