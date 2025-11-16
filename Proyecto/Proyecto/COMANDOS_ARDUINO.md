# COMANDOS RÁPIDOS - INTEGRACIÓN ARDUINO
## Para el usuario: Xpc

---

## PASO 1: Compilar el proyecto (OBLIGATORIO - Solo una vez)

```cmd
cd C:\Users\Xpc\Documents\GitHub\Compi\proyecto1\gvega_compiladores_interpretes_2025\Proyecto\Proyecto
mvn clean compile
```

---

## PASO 2: Compilar un programa .smp

Ejemplo con el cuadrado:
```cmd
java -cp "target/classes;C:\Users\Xpc\.m2\repository\org\antlr\antlr4-runtime\4.13.2\antlr4-runtime-4.13.2.jar" Compiler test\ejemplo3_tortuga.smp target
```

Ejemplo con la estrella:
```cmd
java -cp "target/classes;C:\Users\Xpc\.m2\repository\org\antlr\antlr4-runtime\4.13.2\antlr4-runtime-4.13.2.jar" Compiler test\estrella.smp target
```

Ejemplo con la espiral:
```cmd
java -cp "target/classes;C:\Users\Xpc\.m2\repository\org\antlr\antlr4-runtime\4.13.2\antlr4-runtime-4.13.2.jar" Compiler test\espiral.smp target
```

---

## PASO 3A: Ejecutar SIN Arduino (solo simulación)

```cmd
java -cp "target/classes;C:\Users\Xpc\.m2\repository\org\antlr\antlr4-runtime\4.13.2\antlr4-runtime-4.13.2.jar;C:\Users\Xpc\.m2\repository\com\fazecast\jSerialComm\2.10.4\jSerialComm-2.10.4.jar" runtime.RuntimePlayer target\out.lobj
```

---

## PASO 3B: Ejecutar CON Arduino (envía comandos al carrito)

⚠️ **IMPORTANTE:** Reemplaza COM3 con el puerto correcto de tu Arduino

```cmd
java -cp "target/classes;C:\Users\Xpc\.m2\repository\org\antlr\antlr4-runtime\4.13.2\antlr4-runtime-4.13.2.jar;C:\Users\Xpc\.m2\repository\com\fazecast\jSerialComm\2.10.4\jSerialComm-2.10.4.jar" runtime.RuntimePlayer target\out.lobj COM3
```

### ¿Cómo saber cuál es mi puerto COM?

1. Conecta el Arduino
2. Abre el **Administrador de Dispositivos** de Windows
3. Busca "Puertos (COM y LPT)"
4. Verás algo como "Arduino Uno (COM3)" o similar
5. Usa ese número en el comando (ej: COM3, COM4, COM5, etc.)

---

## COMANDOS TODO EN UNO

### Para probar el cuadrado con Arduino:

```cmd
cd C:\Users\Xpc\Documents\GitHub\Compi\proyecto1\gvega_compiladores_interpretes_2025\Proyecto\Proyecto

java -cp "target/classes;C:\Users\Xpc\.m2\repository\org\antlr\antlr4-runtime\4.13.2\antlr4-runtime-4.13.2.jar" Compiler test\ejemplo3_tortuga.smp target

java -cp "target/classes;C:\Users\Xpc\.m2\repository\org\antlr\antlr4-runtime\4.13.2\antlr4-runtime-4.13.2.jar;C:\Users\Xpc\.m2\repository\com\fazecast\jSerialComm\2.10.4\jSerialComm-2.10.4.jar" runtime.RuntimePlayer target\out.lobj COM3
```

### Para probar la estrella con Arduino:

```cmd
cd C:\Users\Xpc\Documents\GitHub\Compi\proyecto1\gvega_compiladores_interpretes_2025\Proyecto\Proyecto

java -cp "target/classes;C:\Users\Xpc\.m2\repository\org\antlr\antlr4-runtime\4.13.2\antlr4-runtime-4.13.2.jar" Compiler test\estrella.smp target

java -cp "target/classes;C:\Users\Xpc\.m2\repository\org\antlr\antlr4-runtime\4.13.2\antlr4-runtime-4.13.2.jar;C:\Users\Xpc\.m2\repository\com\fazecast\jSerialComm\2.10.4\jSerialComm-2.10.4.jar" runtime.RuntimePlayer target\out.lobj COM3
```

### Para probar la espiral con Arduino:

```cmd
cd C:\Users\Xpc\Documents\GitHub\Compi\proyecto1\gvega_compiladores_interpretes_2025\Proyecto\Proyecto

java -cp "target/classes;C:\Users\Xpc\.m2\repository\org\antlr\antlr4-runtime\4.13.2\antlr4-runtime-4.13.2.jar" Compiler test\espiral.smp target

java -cp "target/classes;C:\Users\Xpc\.m2\repository\org\antlr\antlr4-runtime\4.13.2\antlr4-runtime-4.13.2.jar;C:\Users\Xpc\.m2\repository\com\fazecast\jSerialComm\2.10.4\jSerialComm-2.10.4.jar" runtime.RuntimePlayer target\out.lobj COM3
```

---

## VERIFICACIÓN: ¿Qué debo ver?

### En la consola de Java:

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
  ...
========================================

✓ Desconectado de Arduino
```

### En el Monitor Serial de Arduino:

⚠️ **IMPORTANTE:** Cierra el Monitor Serial ANTES de ejecutar Java, y ábrelo DESPUÉS

```
========================================
  ARDUINO CARRITO TORTUGA - READY
========================================
Esperando comandos desde Java...

[1] Comando recibido: INICIO
  → Iniciando secuencia de dibujo
[2] Comando recibido: CENTRO
  → Volviendo al centro (posición inicial)
[3] Comando recibido: BAJAR_LAPIZ
  → Bajando lápiz (activando marcador)
[4] Comando recibido: AVANZAR:100
  → Avanzando 100 unidades
...
[15] Comando recibido: FIN
  → Secuencia completada

========================================
  Total de comandos ejecutados: 15
========================================
```

---

## SOLUCIÓN DE PROBLEMAS

### ❌ Error: "Port COM3 not found"

**Solución:**
1. Verifica el puerto correcto en el Administrador de Dispositivos
2. Cierra el Monitor Serial de Arduino IDE
3. Desconecta y reconecta el Arduino

### ❌ Error: "Cannot find or load main class"

**Solución:**
```cmd
mvn clean compile
```

### ❌ Arduino no recibe nada

**Solución:**
1. Asegúrate de que el código de Arduino esté cargado
2. Verifica que la velocidad sea 9600 baud
3. Cierra el Monitor Serial antes de ejecutar Java
4. Prueba con otro puerto USB

---

## ARCHIVO DE ARDUINO

El código del Arduino está en:
```
C:\Users\Xpc\Documents\GitHub\Compi\proyecto1\gvega_compiladores_interpretes_2025\Proyecto\Proyecto\arduino_receiver.ino
```

**Pasos para cargarlo:**
1. Abre el Arduino IDE
2. Abre el archivo `arduino_receiver.ino`
3. Conecta el Arduino
4. Selecciona la placa y puerto correctos
5. Haz clic en "Subir" (→)
6. Espera a que termine de cargar

---

## RESUMEN DE ARCHIVOS IMPORTANTES

- **GUIA_ARDUINO.md** - Documentación completa
- **arduino_receiver.ino** - Código para copiar al Arduino
- **ArduinoController.java** - Clase de comunicación serial
- **TurtleRuntime.java** - Runtime con soporte Arduino
- **RuntimePlayer.java** - Ejecutor con soporte Arduino

---

## ¿NECESITAS AYUDA?

Lee la guía completa en:
```
docs\GUIA_ARDUINO.md
```

