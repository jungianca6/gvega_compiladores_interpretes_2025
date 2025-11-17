package runtime;

import java.io.OutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador para comunicación serial con Arduino.
 * Envía comandos de tortuga al Arduino para que un carrito los ejecute.
 */
public class ArduinoController {
    private OutputStream output;
    private InputStream input;
    private boolean connected = false;
    private List<String> commandBuffer;

    public ArduinoController() {
        this.commandBuffer = new ArrayList<>();
    }

    /**
     * Conecta al puerto serial de Arduino
     * @param portName Nombre del puerto (ej: "COM3" en Windows, "/dev/ttyUSB0" en Linux)
     * @return true si la conexión fue exitosa
     */
    public boolean connect(String portName) {
        try {
            // Intentar cargar la librería jSerialComm
            Class<?> serialPortClass = Class.forName("com.fazecast.jSerialComm.SerialPort");
            Object serialPort = serialPortClass.getMethod("getCommPort", String.class)
                .invoke(null, portName);

            // Configurar el puerto: 9600 baud, 8 bits, 1 stop bit, sin paridad
            serialPortClass.getMethod("setComPortParameters", int.class, int.class, int.class, int.class)
                .invoke(serialPort, 9600, 8, 1, 0);

            // Abrir el puerto
            boolean opened = (boolean) serialPortClass.getMethod("openPort").invoke(serialPort);

            if (opened) {
                output = (OutputStream) serialPortClass.getMethod("getOutputStream").invoke(serialPort);
                input = (InputStream) serialPortClass.getMethod("getInputStream").invoke(serialPort);
                connected = true;

                // Esperar a que Arduino se inicialice
                Thread.sleep(2000);

                System.out.println("✓ Conectado a Arduino en " + portName);
                return true;
            } else {
                System.err.println("✗ No se pudo abrir el puerto " + portName);
                return false;
            }

        } catch (ClassNotFoundException e) {
            System.err.println("✗ Error: Librería jSerialComm no encontrada.");
            System.err.println("  Para usar Arduino, agrega esta dependencia al pom.xml:");
            System.err.println("  <dependency>");
            System.err.println("    <groupId>com.fazecast</groupId>");
            System.err.println("    <artifactId>jSerialComm</artifactId>");
            System.err.println("    <version>2.10.4</version>");
            System.err.println("  </dependency>");
            return false;
        } catch (Exception e) {
            System.err.println("✗ Error al conectar con Arduino: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Envía un comando al Arduino
     */
    private void sendCommand(String command) {
        commandBuffer.add(command);

        if (connected && output != null) {
            try {
                String cmd = command + "\n";
                output.write(cmd.getBytes());
                output.flush();
                System.out.println("→ Arduino: " + command);

                // Pequeña pausa para evitar sobrecarga
                Thread.sleep(50);
            } catch (Exception e) {
                System.err.println("✗ Error al enviar comando: " + e.getMessage());
            }
        } else {
            System.out.println("📝 Buffer: " + command + " (no conectado)");
        }
    }

    /**
     * Comandos específicos para el carrito
     */

    public void avanzar(int distancia) {
        sendCommand("AVANZAR:" + distancia);
    }

    public void retroceder(int distancia) {
        sendCommand("RETROCEDER:" + distancia);
    }

    public void girarDerecha(int grados) {
        sendCommand("GIRAR_DERECHA:" + grados);
    }

    public void girarIzquierda(int grados) {
        sendCommand("GIRAR_IZQUIERDA:" + grados);
    }

    public void bajarLapiz() {
        sendCommand("BAJAR_LAPIZ");
    }

    public void subirLapiz() {
        sendCommand("SUBIR_LAPIZ");
    }

    public void cambiarColor(String color) {
        sendCommand("COLOR:" + color);
    }

    public void centro() {
        sendCommand("CENTRO");
    }

    public void esperar(int milisegundos) {
        sendCommand("ESPERAR:" + milisegundos);
    }

    public void iniciar() {
        sendCommand("INICIO");
    }

    public void finalizar() {
        sendCommand("FIN");
        System.out.println("✓ Secuencia de comandos enviada al Arduino");
    }

    /**
     * Obtiene todos los comandos del buffer (útil para debugging)
     */
    public List<String> getCommandBuffer() {
        return new ArrayList<>(commandBuffer);
    }

    /**
     * Limpia el buffer de comandos
     */
    public void clearBuffer() {
        commandBuffer.clear();
    }

    /**
     * Cierra la conexión con Arduino
     */
    public void disconnect() {
        if (connected) {
            try {
                if (output != null) {
                    output.close();
                }
                if (input != null) {
                    input.close();
                }
                connected = false;
                System.out.println("✓ Desconectado de Arduino");
            } catch (Exception e) {
                System.err.println("✗ Error al desconectar: " + e.getMessage());
            }
        }
    }

    /**
     * Verifica si está conectado
     */
    public boolean isConnected() {
        return connected;
    }

    /**
     * Imprime resumen de comandos enviados
     */
    public void printSummary() {
        System.out.println("\n========================================");
        System.out.println("RESUMEN DE COMANDOS ENVIADOS AL ARDUINO");
        System.out.println("========================================");
        System.out.println("Total de comandos: " + commandBuffer.size());
        System.out.println("Estado: " + (connected ? "Conectado" : "No conectado"));
        System.out.println("\nComandos:");
        for (int i = 0; i < commandBuffer.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + commandBuffer.get(i));
        }
        System.out.println("========================================\n");
    }
}

