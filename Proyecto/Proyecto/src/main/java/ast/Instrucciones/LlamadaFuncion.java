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

        Map<String, Object>  local_context = func.getSymbolTable();

        List<String> arguments_list = func.getParametros();

        for(int i=0;i<arguments_list.size();++i)
        {
            local_context.put(arguments_list.get(i) ,parametros.get(i).execute(symbolTable));
        }

        for(ASTNode n:func.getCuerpo())
        {
            Object task = n.execute(local_context);

            if(task!=null) {
                return task;
            }
        }
        return null;
    }

}