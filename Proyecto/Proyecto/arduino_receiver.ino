// Pin del LED integrado
const int LED_PIN = 13;

// Variables globales
String comandoBuffer = "";
int comandosRecibidos = 0;

void setup() {
  // Inicializar comunicación serial
  Serial.begin(9600);

  // Configurar LED para indicador visual
  pinMode(LED_PIN, OUTPUT);

  // Parpadear LED para indicar que está listo
  for (int i = 0; i < 3; i++) {
    digitalWrite(LED_PIN, HIGH);
    delay(200);
    digitalWrite(LED_PIN, LOW);
    delay(200);
  }

  // Mensaje de bienvenida
  Serial.println("========================================");
  Serial.println("  ARDUINO CARRITO TORTUGA - READY");
  Serial.println("========================================");
  Serial.println("Esperando comandos desde Java...");
  Serial.println();
}

void loop() {
  // Leer datos disponibles del puerto serial
  while (Serial.available() > 0) {
    char c = Serial.read();

    // Si es fin de línea, procesar comando
    if (c == '\n' || c == '\r') {
      if (comandoBuffer.length() > 0) {
        procesarComando(comandoBuffer);
        comandoBuffer = "";
      }
    } else {
      // Agregar carácter al buffer
      comandoBuffer += c;
    }
  }
}

void procesarComando(String comando) {
  comandosRecibidos++;

  // Parpadear LED para indicar recepción
  digitalWrite(LED_PIN, HIGH);

  // Imprimir encabezado del comando
  Serial.print("[");
  Serial.print(comandosRecibidos);
  Serial.print("] Comando recibido: ");
  Serial.println(comando);

  // Parsear el comando
  int separador = comando.indexOf(':');
  String accion = "";
  String parametro = "";

  if (separador > 0) {
    accion = comando.substring(0, separador);
    parametro = comando.substring(separador + 1);
  } else {
    accion = comando;
  }

  // Procesar según el tipo de comando
  if (accion == "INICIO") {
    Serial.println("  → Iniciando secuencia de dibujo");
    Serial.println("  → Sistema listo para recibir instrucciones");

  } else if (accion == "AVANZAR") {
    int distancia = parametro.toInt();
    Serial.print("  → Avanzando ");
    Serial.print(distancia);
    Serial.println(" unidades");
    // TODO: Aquí irían los comandos para controlar los motores
    // Ejemplo: moverMotores(ADELANTE, distancia);

  } else if (accion == "RETROCEDER") {
    int distancia = parametro.toInt();
    Serial.print("  → Retrocediendo ");
    Serial.print(distancia);
    Serial.println(" unidades");
    // TODO: moverMotores(ATRAS, distancia);

  } else if (accion == "GIRAR_DERECHA") {
    int grados = parametro.toInt();
    Serial.print("  → Girando a la derecha ");
    Serial.print(grados);
    Serial.println(" grados");
    // TODO: girarCarrito(DERECHA, grados);

  } else if (accion == "GIRAR_IZQUIERDA") {
    int grados = parametro.toInt();
    Serial.print("  → Girando a la izquierda ");
    Serial.print(grados);
    Serial.println(" grados");
    // TODO: girarCarrito(IZQUIERDA, grados);

  } else if (accion == "BAJAR_LAPIZ") {
    Serial.println("  → Bajando lápiz (activando marcador)");
    // TODO: bajarMarcador();

  } else if (accion == "SUBIR_LAPIZ") {
    Serial.println("  → Subiendo lápiz (desactivando marcador)");
    // TODO: subirMarcador();

  } else if (accion == "COLOR") {
    Serial.print("  → Cambiando color a: ");
    Serial.println(parametro);
    // TODO: cambiarColorMarcador(parametro);

  } else if (accion == "CENTRO") {
    Serial.println("  → Volviendo al centro (posición inicial)");
    // TODO: volverAlCentro();

  } else if (accion == "ESPERAR") {
    int milisegundos = parametro.toInt();
    Serial.print("  → Esperando ");
    Serial.print(milisegundos);
    Serial.println(" ms");
    delay(milisegundos);

  } else if (accion == "FIN") {
    Serial.println("  → Secuencia completada");
    Serial.println();
    Serial.println("========================================");
    Serial.print("  Total de comandos ejecutados: ");
    Serial.println(comandosRecibidos);
    Serial.println("========================================");
    Serial.println();

    // Parpadear LED para indicar fin
    for (int i = 0; i < 5; i++) {
      digitalWrite(LED_PIN, HIGH);
      delay(100);
      digitalWrite(LED_PIN, LOW);
      delay(100);
    }

  } else {
    Serial.print("  ⚠ Comando desconocido: ");
    Serial.println(accion);
  }

  // Apagar LED
  delay(50);
  digitalWrite(LED_PIN, LOW);
}

// Ejemplo de función para mover motores
// void moverMotores(int direccion, int distancia) {
//   // Configurar la dirección de los motores
//   // Activar motores por el tiempo necesario para recorrer la distancia
//   // Detener motores
// }

// Ejemplo de función para girar
// void girarCarrito(int direccion, int grados) {
//   // Calcular el tiempo necesario para girar los grados especificados
//   // Activar motores en direcciones opuestas
//   // Detener después del tiempo calculado
// }

// Ejemplo de función para bajar marcador
// void bajarMarcador() {
//   // Activar servo para bajar el marcador
// }

// Ejemplo de función para subir marcador
// void subirMarcador() {
//   // Activar servo para subir el marcador
// }

