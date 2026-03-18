# Focus Alan - Pomodoro Timer 

## Descripción de la Práctica
La práctica se enfocó en el desarrollo de una herramienta funcional que se basa en la técnica de Pomodoro.

### 📋 Actividades Realizadas
1. **Corrección de Estructura**: Se reestructuró todo el proyecto, ya que al crearlo por primera vez, faltaban muchas dependencias como GRADLE.
2. **Motor del Temporizador**: Se implementó la técnica de Pomodoro con cambio automático entre Enfoque (25 min), Descanso Corto (5 min) y Descanso Largo (15 min).
3. **Interfaz Dinámica**: Creación de un sistema de puntos de progreso mediante un `LinearLayout` que genera `Views` dinámicamente para reflejar las sesiones completadas.
4. **Feedback de Usuario**: Se configuró el `Manifest` de android para habilitar la vibración y el uso de `Toasts` informativos al finalizar cada bloque.
5. **Diseño Material**: Se usó `ChipGroup` para la selección de modos y personalización de estilos (bordes de 2dp y dimensiones en `dimens.xml`).

---

## Preguntas sobre la Implementación

### ¿Cuál fue el mayor reto al gestionar el CountDownTimer y cómo evitaste que se crearan múltiples instancias al presionar el botón repetidamente?
El mayor reto fue entender cómo debían actualizarse los puntos verdes con el final de cada pomodoro, ya que no sabía si el progreso se marcaba antes o después de los 5 minutos de descanso. Al final, lo lógico fue que se pintaran justo al terminar los 25 minutos de enfoque.

Para evitar la creación de varias instancias al presionar el botón de inicio muchas veces, utilicé una validación donde, antes de crear un nuevo timer, se ejecuta un countDownTimer.cancel().

### ¿Por qué es preferible usar un LinearLayout con addView para los puntos de progreso en lugar de declarar 4 ImageViews estáticos en el XML?
Es preferible porque nos da mucha más libertad en el código. Si lo hacemos estático en el XML, estamos amarrados a solo 4 puntos, pero con addView en un bucle for, el programa puede generar la cantidad de puntos que queramos de forma dinámica. Además, es más limpio manejar el dibujo de los círculos desde Java que andar buscando 4 IDs diferentes en el layout.

### Si quisiéramos añadir una función para que el usuario personalice sus propios tiempos de enfoque, ¿qué parte de tu lógica actual tendría que cambiar y cómo lo abordarías?
Se tendría que agregar una función que permita cambiarlos, pero específicamente habría que quitar los valores hardcodeados (como los 1,500,000 ms que tenemos ahorita). Lo abordaría creando variables globales para los tiempos y conectándolas a un menú de configuración o cajones de texto (EditText) para que el usuario ponga sus propios minutos, y que esos valores se guarden para que no se borren al cerrar la app.

### ¿Cómo harían para que el tiempo del temporizador se mantenga si el usuario minimiza la app?
A lo mejor debemos modificar el manifiesto de Android para darle permisos de ejecución en segundo plano. La forma correcta sería usar algo llamado "Foreground Service" (Servicio de primer plano). Esto hace que el temporizador siga viviendo aunque la pantalla esté minimizada, mostrando el tiempo en una notificación para que el sistema de Android no cierre la aplicación por falta de memoria.