#include <WiFi.h>

const char* ssid     = "TU_SSID";
const char* password = "TU_PASSWORD";

WiFiServer server(5000);  // Puerto TCP

String comandoBuffer = "";
int comandosRecibidos = 0;

const int LED_PIN = 2;

void setup() {
  Serial.begin(115200);
  pinMode(LED_PIN, OUTPUT);

  WiFi.begin(ssid, password);
  Serial.print("Conectando a WiFi");

  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }

  Serial.println("\nWiFi conectado!");
  Serial.print("IP del ESP32: ");
  Serial.println(WiFi.localIP());

  server.begin();
  Serial.println("Servidor TCP iniciado en puerto 5000");
}

void loop() {
  WiFiClient client = server.available();

  if (client) {
    Serial.println("Cliente conectado.");

    comandoBuffer = "";

    while (client.connected()) {
      if (client.available()) {
        char c = client.read();

        // Detectar fin de línea
        if (c == '\n' || c == '\r') {
          if (comandoBuffer.length() > 0) {
            procesarComando(comandoBuffer);
            client.println("OK"); // respuesta opcional
            comandoBuffer = "";
          }
        } else {
          comandoBuffer += c;
        }
      }
    }

    Serial.println("Cliente desconectado.");
  }
}

void procesarComando(String comando) {
  comandosRecibidos++;
  digitalWrite(LED_PIN, HIGH);

  Serial.print("[");
  Serial.print(comandosRecibidos);
  Serial.print("] ");
  Serial.println(comando);

  int sep = comando.indexOf(':');
  String accion = (sep >= 0) ? comando.substring(0, sep) : comando;
  String param  = (sep >= 0) ? comando.substring(sep + 1) : "";

  if (accion == "AVANZAR") {
    Serial.println("→ Avanzando " + param);
  } else if (accion == "RETROCEDER") {
    Serial.println("→ Retrocediendo " + param);
  } else if (accion == "GIRAR_DERECHA") {
    Serial.println("→ Girando derecha " + param);
  } else if (accion == "GIRAR_IZQUIERDA") {
    Serial.println("→ Girando izquierda " + param);
  } else if (accion == "BAJAR_LAPIZ") {
    Serial.println("→ Bajando lápiz");
  } else if (accion == "SUBIR_LAPIZ") {
    Serial.println("→ Subiendo lápiz");
  } else if (accion == "COLOR") {
    Serial.println("→ Cambiando color a " + param);
  } else if (accion == "CENTRO") {
    Serial.println("→ Centro");
  } else if (accion == "ESPERAR") {
    Serial.println("→ Esperando " + param + " ms");
    delay(param.toInt());
  } else if (accion == "INICIO") {
    Serial.println("→ Inicio de secuencia");
  } else if (accion == "FIN") {
    Serial.println("→ Fin de secuencia");
  }

  digitalWrite(LED_PIN, LOW);
}

