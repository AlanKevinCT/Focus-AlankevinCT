## Descripción de la Práctica
La práctica consistió en configurar el entorno de desarrollo 'Android Studio' para poder realizar aplicaciones móviles.
Además, se implementó la clase 'TaskManager' con las operaciones CRUD para manipular tareas y lista de tareas.

### 📋 Actividades Realizadas
1. **Configuración del Entorno**: Instalación de Android Studio y componentes necesarios (SDK Tools, Emulator, Platform-Tools).
2. **Configuración de AVDs**: Creación de emuladores para smartphone (API 34) y tablet (API 26).
3. **Control de Versiones**: Implementación de Git con manejo de ramas y resolución de conflictos.
4. **Lógica de Programación**: Desarrollo de la clase `TaskManager.java` con operaciones CRUD.

---

## Preguntas sobre la Implementación

### ¿Tuviste problemas con la aceleración de hardware o la creación de los AVD? Describe la solución.
Sí, durante la configuración de los emuladores AVD (Android Virtual Device) me encontré con problemas de aceleración de hardware. Mi laptop original no contaba con los recursos suficientes para ejecutar los emuladores de manera fluida, presentaba errores de rendimiento y la aplicación se estancaba al intentar abrirla.

La solución fue adquirir un nuevo equipo con mejores especificaciones técnicas que sí soportara la aceleración por hardware: Procesador Intel Core i5, RAM de 16 GB y Almacenamiento de SSD 512 GB.
Además, en las opciones del AVD, cambié los gráficos a "Hardware - GLES 2.0" para mejor rendimiento.

Después de estos cambios, los emuladores funcionaron correctamente sin problemas de rendimiento.

### ¿Por qué elegiste ArrayList sobre otras opciones?
Elegí `ArrayList` como estructura de datos principal porque Necesitaba poder obtener tareas por su índice de manera eficiente (por ejemplo, para actualizar o eliminar una tarea específica). ArrayList ofrece tiempo constante O(1) para esto.
Además, como no sabía cuántas tareas se agregarían, necesitaba una estructura que creciera dinámicamente.

### Si las tareas se guardaran en un servidor remoto, ¿qué cambiaría en el manejo de excepciones de tu función?
Si las tareas se almacenaran en un servidor remoto, el manejo de excepciones sería mucho más complejo. Estos son los cambios que realizaría:
1. Implementaría formas para manejar diferentes tipos de excepciones como: excepciones de red, HTTP, entre otros.
2. Guardarías las tareas de manera local cuando no se cuente con conexión para posteriormente sincronizarlas con el servidor.
