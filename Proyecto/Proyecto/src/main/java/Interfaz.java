import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.List;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import parser.*;
import semantic.*;
import ir.*;
import optimizer.*;
import codegen.*;
import backend.*;
import ast.ASTNode;


class CompiInterfaz extends JFrame {

    private JPanel panel;
    private JLabel etiqueta;
    private JTextField chatTexto;
    private JButton subir,compilar;
    private JTextArea codigo;
    private File archivoSeleccionado;

    public CompiInterfaz(){
        this.setBounds(500,200,600,600);
        setTitle("Interfaz");
        ComponentesCliente();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
    private void ComponentesCliente(){
        panelGUI();
        colocarAreaCodigo();
        etiquetaGUI();
        //colocarCajadeTexto();
        colocarBotones();
    }

    private void panelGUI(){
        panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(Color.pink);
        this.getContentPane().add(panel);
    }

    private void etiquetaGUI(){
        etiqueta = new JLabel("Interfaz del compilador",SwingConstants.CENTER);
        etiqueta.setBounds(200,20,200,25);
        etiqueta.setForeground(Color.WHITE);
        etiqueta.setBackground(Color.BLACK);
        etiqueta.setFont(new Font("times new roman", Font.PLAIN,20));
        etiqueta.setOpaque(true);
        panel.add(etiqueta);
    }

    private void colocarAreaCodigo(){
        codigo = new JTextArea();
        codigo.setBounds(50,100,200,350);
        codigo.setText("Escribe el codigo aqui");
        panel.add(codigo);
        codigo.setEditable(true);
        codigo.setLineWrap(true);


        JScrollPane scroll = new JScrollPane(codigo,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBounds(50,100,200,340);
        panel.add(scroll);
    }

    private void colocarBotones(){
        subir = new JButton("Subir código");
        subir.setBounds(300,340,160,30);
        panel.add(subir);
        subir.setEnabled(true);

        compilar = new JButton("Compilar");
        compilar.setBounds(300,390,160,30);
        panel.add(compilar);
        compilar.setEnabled(true);


        ActionListener subirCodigo = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                subeCodigo();
            }
        };
        subir.addActionListener(subirCodigo);

        ActionListener compilarCodigo = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                compilacion();
            }
        };
        compilar.addActionListener(compilarCodigo);


    }

    private void subeCodigo() {
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);

        int seleccion = fc.showOpenDialog(this);

        if (seleccion == JFileChooser.APPROVE_OPTION) {
            archivoSeleccionado = fc.getSelectedFile();

            if (!archivoSeleccionado.getName().endsWith(".smp")) {
                JOptionPane.showMessageDialog(this,
                        "Seleccione un archivo con extensión .smp",
                        "Error", JOptionPane.ERROR_MESSAGE);
                archivoSeleccionado = null;
                return;
            }

            try {
                // Leer contenido del archivo y mostrarlo
                String contenido = new String(java.nio.file.Files.readAllBytes(
                        archivoSeleccionado.toPath()
                ));

                codigo.setText(contenido); // mostrar solo código, no la ruta

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error al leer el archivo:\n" + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void compilacion() {

        if (archivoSeleccionado == null) {
            JOptionPane.showMessageDialog(this,
                    "Debe subir primero un archivo .smp",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String proyectoRoot = new File("").getAbsolutePath();
            String outputDir = proyectoRoot + "/Proyecto/Proyecto/target";

            Compiler.compile(archivoSeleccionado.getAbsolutePath(), outputDir);


        } catch (Exception ex) {
            System.out.println("❌ Error al compilar por medio de la interfaz:\n");
        }
    }

}

public class Interfaz {
    public static void main(String[] args) {
        CompiInterfaz compiGUI = new CompiInterfaz();
        compiGUI.setVisible(true);
        compiGUI.setResizable(false);
    }
}
