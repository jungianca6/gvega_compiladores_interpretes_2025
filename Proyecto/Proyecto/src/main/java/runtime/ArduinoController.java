package runtime;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ArduinoController {

    private Socket socket;
    private PrintWriter out;
    private boolean connected = false;
    private List<String> buffer;

    public ArduinoController() {
        buffer = new ArrayList<>();
    }

    // -----------------------------
    // CONEXIÓN WIFI
    // -----------------------------
    public boolean connectWiFi(String host, int port) {
        try {
            socket = new Socket(host, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            connected = true;
            System.out.println("✓ Conectado al ESP32 en " + host + ":" + port);
            return true;
        } catch (Exception e) {
            System.err.println("✗ No se pudo conectar al ESP32: " + e.getMessage());
            return false;
        }
    }

    // -----------------------------
    // ENVÍO DE COMANDOS
    // -----------------------------
    private void sendCommand(String cmd) {
        buffer.add(cmd);
        if (connected && out != null) {
            out.println(cmd);
            System.out.println("→ ESP32: " + cmd);
        } else {
            System.out.println("📝 Buffer: " + cmd + " (no conectado)");
        }
    }

    public void iniciar()       { sendCommand("INICIO"); }
    public void finalizar()     { sendCommand("FIN"); }
    public void avanzar(int d)  { sendCommand("AVANZAR:" + d); }
    public void retroceder(int d) { sendCommand("RETROCEDER:" + d); }
    public void girarDerecha(int g) { sendCommand("GIRAR_DERECHA:" + g); }
    public void girarIzquierda(int g) { sendCommand("GIRAR_IZQUIERDA:" + g); }
    public void bajarLapiz()    { sendCommand("BAJAR_LAPIZ"); }
    public void subirLapiz()    { sendCommand("SUBIR_LAPIZ"); }
    public void cambiarColor(String c) { sendCommand("COLOR:" + c); }
    public void centro()        { sendCommand("CENTRO"); }
    public void esperar(int ms) { sendCommand("ESPERAR:" + ms); }

    public List<String> getCommandBuffer() { return buffer; }

    // -----------------------------
    // NUEVOS MÉTODOS NECESARIOS
    // -----------------------------
    public boolean isConnected() {
        return connected;
    }

    public void printSummary() {
        System.out.println("\n=== RESUMEN DE COMANDOS ENVIADOS AL ESP32 ===");
        for (String cmd : buffer) {
            System.out.println(" → " + cmd);
        }
        System.out.println("Total: " + buffer.size() + " comandos\n");
    }

    // -----------------------------
    // DESCONECTAR
    // -----------------------------
    public void disconnect() {
        try {
            if (socket != null) socket.close();
            connected = false;
            System.out.println("✓ Desconectado del ESP32");
        } catch (Exception e) {}
    }
}


