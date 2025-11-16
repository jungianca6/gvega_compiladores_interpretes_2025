import javax.swing.*;
import java.awt.*;

class CompiInterfaz extends JFrame {

    private JPanel panel;
    private JLabel etiqueta;
    private JTextField chatTexto;
    private JButton subir,compilar;
    private JTextArea codigo;

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
        colocarBoton();
    }

    private void panelGUI(){
        panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(Color.pink);
        this.getContentPane().add(panel);
    }

    private void etiquetaGUI(){
        etiqueta = new JLabel("Interfaz del compilador",SwingConstants.CENTER);
        panel.add(etiqueta);
        etiqueta.setBounds(200,20,200,25);
        etiqueta.setForeground(Color.WHITE);
        etiqueta.setBackground(Color.BLACK);
        etiqueta.setFont(new Font("times new roman", Font.PLAIN,20));
        etiqueta.setOpaque(true);
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

    private void colocarBoton(){
        subir = new JButton("Subir código");
        subir.setBounds(300,340,160,30);
        panel.add(subir);
        subir.setEnabled(true);

        compilar = new JButton("Compilar");
        compilar.setBounds(300,390,160,30);
        panel.add(compilar);
        compilar.setEnabled(true);
    }

}

public class Interfaz {
    public static void main(String[] args) {
        CompiInterfaz compiGUI = new CompiInterfaz();
        compiGUI.setVisible(true);
        compiGUI.setResizable(false);
    }
}
