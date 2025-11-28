# Guía de Integración con Arduino

## Descripción General

Este sistema permite enviar las instrucciones de dibujo de la tortuga a un Arduino para que un carrito las ejecute físicamente. La comunicación se realiza mediante puerto serial (USB).

---

## PARTE 1: Configuración del Código Java

### Archivos Modificados/Creados:

1. **ArduinoController.java** - Nueva clase para comunicación serial
   - Ubicación: `src/main/java/runtime/ArduinoController.java`
   - Maneja la conexión y envío de comandos al Arduino

2. **TurtleRuntime.java** - Modificado para integrar Arduino
   - Ahora envía comandos al Arduino en cada operación de la tortuga
   - Métodos agregados: `conectarArduino()`, `desconectarArduino()`, `mostrarResumenArduino()`

3. **RuntimePlayer.java** - Modificado para aceptar puerto Arduino
   - Nuevo parámetro opcional: puerto Arduino (ej: COM3)

4. **pom.xml** - Actualizado con dependencia de comunicación serial
   - Agregada: `jSerialComm` versión 2.10.4

---

## PARTE 2: Compilar el Proyecto

### Paso 1: Instalar las dependencias
```bash
cd C:\Users\Xpc\Documents\GitHub\Compi\proyecto1\gvega_compiladores_interpretes_2025\Proyecto\Proyecto
mvn clean install
```

Esto descargará la librería jSerialComm necesaria para la comunicación serial.

---

## PARTE 3: Configuración del Arduino

### Paso 1: Cargar el código en Arduino

1. Abre el **Arduino IDE**
2. Copia todo el contenido del archivo `arduino_receiver.ino`
3. Conéctate a tu Arduino
4. Sube el código al Arduino
5. Abre el **Monitor Serial** (115200 o 9600 baud)
6. Deberías ver el mensaje:
   ```
   ========================================
     ARDUINO CARRITO TORTUGA - READY
   ========================================
   Esperando comandos desde Java...
   ```

### Paso 2: Identificar el puerto COM

**En Windows:**
- Abre el Administrador de Dispositivos
- Busca "Puertos (COM y LPT)"
- Identifica tu Arduino (ej: COM3, COM4, etc.)

**En Linux/Mac:**
- Ejecuta: `ls /dev/tty*`
- Busca algo como `/dev/ttyUSB0` o `/dev/ttyACM0`

---

## PARTE 4: Uso del Sistema

### Modo 1: Sin Arduino (Solo Simulación)

Ejecuta el programa normalmente, SIN especificar puerto Arduino:

```bash
java -cp "target/classes;C:\Users\Xpc\.m2\repository\org\antlr\antlr4-runtime\4.13.2\antlr4-runtime-4.13.2.jar;C:\Users\Xpc\.m2\repository\com\fazecast\jSerialComm\2.10.4\jSerialComm-2.10.4.jar" runtime.RuntimePlayer target\out.lobj
```

Los comandos se guardarán en buffer pero NO se enviarán al Arduino.

### Modo 2: Con Arduino (Envío de Comandos)

Ejecuta especificando el puerto Arduino:

```bash
java -cp "target/classes;C:\Users\Xpc\.m2\repository\org\antlr\antlr4-runtime\4.13.2\antlr4-runtime-4.13.2.jar;C:\Users\Xpc\.m2\repository\com\fazecast\jSerialComm\2.10.4\jSerialComm-2.10.4.jar" runtime.RuntimePlayer target\out.lobj COM3
```

**Reemplaza COM3 con tu puerto real!**

---

## PARTE 5: Verificar la Comunicación

### Paso 1: Compilar un programa de prueba

```bash
java -cp "target/classes;C:\Users\Xpc\.m2\repository\org\antlr\antlr4-runtime\4.13.2\antlr4-runtime-4.13.2.jar" Compiler test\ejemplo3_tortuga.smp target
```

### Paso 2: Ejecutar con Arduino conectado

Asegúrate de que:
1. El Arduino esté conectado
2. El código esté cargado en el Arduino
3. El Monitor Serial del Arduino IDE esté CERRADO (solo un programa puede usar el puerto)

```bash
java -cp "target/classes;C:\Users\Xpc\.m2\repository\org\antlr\antlr4-runtime\4.13.2\antlr4-runtime-4.13.2.jar;C:\Users\Xpc\.m2\repository\com\fazecast\jSerialComm\2.10.4\jSerialComm-2.10.4.jar" runtime.RuntimePlayer target\out.lobj COM3
```

### Paso 3: Verificar salida en Java

Deberías ver algo como:

```
=== CONECTANDO CON ARDUINO ===
✓ Conectado a Arduino en COM3
→ Arduino: INICIO

=== EXECUTING COMPILED PROGRAM ===
Tortuga en el centro: (0, 0). Rumbo restablecido a 0°.
→ Arduino: CENTRO
Lápiz bajado - comenzando a dibujar
→ Arduino: BAJAR_LAPIZ
...

=== EXECUTION COMPLETE ===

========================================
RESUMEN DE COMANDOS ENVIADOS AL ARDUINO
========================================
Total de comandos: 15
Estado: Conectado

Comandos:
  1. INICIO
  2. CENTRO
  3. BAJAR_LAPIZ
  4. COLOR:negro
  5. AVANZAR:100
  6. GIRAR_DERECHA:90
  ...
========================================

✓ Desconectado de Arduino
```

### Paso 4: Verificar salida en Arduino

Abre el Monitor Serial del Arduino IDE después de que termine la ejecución de Java.
Deberías ver:

```
========================================
  ARDUINO CARRITO TORTUGA - READY
========================================
Esperando comandos desde Java...

[1] Comando recibido: INICIO
  → Iniciando secuencia de dibujo
  → Sistema listo para recibir instrucciones
[2] Comando recibido: CENTRO
  → Volviendo al centro (posición inicial)
[3] Comando recibido: BAJAR_LAPIZ
  → Bajando lápiz (activando marcador)
[4] Comando recibido: COLOR:negro
  → Cambiando color a: negro
[5] Comando recibido: AVANZAR:100
  → Avanzando 100 unidades
[6] Comando recibido: GIRAR_DERECHA:90
  → Girando a la derecha 90 grados
...
[15] Comando recibido: FIN
  → Secuencia completada

========================================
  Total de comandos ejecutados: 15
========================================
```

---

## PARTE 6: Formato de Comandos

Los comandos enviados al Arduino son:

| Comando | Formato | Descripción |
|---------|---------|-------------|
| INICIO | `INICIO` | Inicia secuencia de dibujo |
| AVANZAR | `AVANZAR:distancia` | Avanzar N unidades |
| RETROCEDER | `RETROCEDER:distancia` | Retroceder N unidades |
| GIRAR_DERECHA | `GIRAR_DERECHA:grados` | Girar a la derecha N grados |
| GIRAR_IZQUIERDA | `GIRAR_IZQUIERDA:grados` | Girar a la izquierda N grados |
| BAJAR_LAPIZ | `BAJAR_LAPIZ` | Activar marcador |
| SUBIR_LAPIZ | `SUBIR_LAPIZ` | Desactivar marcador |
| COLOR | `COLOR:nombre` | Cambiar color del marcador |
| CENTRO | `CENTRO` | Volver a posición inicial |
| ESPERAR | `ESPERAR:milisegundos` | Pausa temporal |
| FIN | `FIN` | Fin de secuencia |

---

## PARTE 7: Solución de Problemas

### Error: "Cannot find or load main class runtime.RuntimePlayer"

**Solución:** Recompila el proyecto
```bash
mvn clean compile
```

### Error: "NoClassDefFoundError: com/fazecast/jSerialComm"

**Solución:** Asegúrate de incluir el JAR en el classpath:
```bash
-cp "target/classes;...\jSerialComm-2.10.4.jar"
```

O ejecuta:
```bash
mvn clean install
```

### Error: "Port COM3 not found" o "Permission denied"

**Soluciones:**
1. Verifica que el puerto sea correcto (usa Administrador de Dispositivos)
2. Cierra el Monitor Serial de Arduino IDE
3. Desconecta y reconecta el Arduino
4. En Linux, agrega tu usuario al grupo dialout:
   ```bash
   sudo usermod -a -G dialout $USER
   ```

### Arduino no recibe comandos

**Verifica:**
1. Velocidad serial correcta (9600 baud)
2. Puerto correcto en Java
3. Monitor Serial cerrado
4. Cable USB funcionando

### Los comandos llegan corruptos

**Solución:** Aumenta el delay en ArduinoController.java:
- Línea ~70: Cambia `Thread.sleep(50)` a `Thread.sleep(100)`

---

## PARTE 8: Próximos Pasos (Implementar Hardware)

El código de Arduino actual solo imprime los comandos. Para controlar un carrito real:

1. **Conectar motores DC** (con driver L298N o similar)
2. **Implementar las funciones:**
   - `moverMotores(direccion, distancia)`
   - `girarCarrito(direccion, grados)`
3. **Agregar servo** para el marcador (lápiz):
   - `bajarMarcador()`
   - `subirMarcador()`
4. **Calibrar movimientos:**
   - Relación distancia ↔ tiempo de motores
   - Relación grados ↔ tiempo de giro

---

## Ejemplos de Uso Completo

### Ejemplo 1: Dibujar un cuadrado

```bash
# Compilar
mvn clean compile
java -cp "target/classes;..." Compiler test\ejemplo3_tortuga.smp target

# Ejecutar con Arduino
java -cp "target/classes;...;jSerialComm-2.10.4.jar" runtime.RuntimePlayer target\out.lobj COM3
```

### Ejemplo 2: Dibujar una estrella

```bash
java -cp "target/classes;..." Compiler test\estrella.smp target
java -cp "target/classes;...;jSerialComm-2.10.4.jar" runtime.RuntimePlayer target\out.lobj COM3
```

---

## Contacto y Soporte

Si encuentras problemas:
1. Verifica que todos los pasos anteriores se hayan seguido correctamente
2. Revisa el Monitor Serial del Arduino para ver si llegan los comandos
3. Prueba primero sin Arduino para asegurar que el compilador funciona

¡Éxito con tu proyecto!

