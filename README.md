# Focus Alan - Alan Kevin Cano Tenorio

**Nombre:** Alan Kevin Cano Tenorio  
**No. de Cuenta:** *321259967*

---

## 1. Descripción General de Tareas Realizadas
El proyecto consistió en el desarrollo de una aplicación de productividad basada en la técnica Pomodoro, siguiendo la arquitectura **MVC** (Modelo-Vista-Controlador). Las tareas principales:

* **Arquitectura y Modularización:** Organización del código en paquetes específicos (`.view`, `.model`, `.data`) para garantizar un código escalable y ordenado.
* **Temporizador Pomodoro:** Implementación de estados: **Enfoque** (25 min), **Descanso Corto** (5 min) y **Descanso Largo** (15 min).
* **Persistencia de Sesiones:** Uso de **SQLite nativo** para el registro de sesiones, asegurando  la persistencia e inmutabilidad
* **Sistema de Preferencias:** Implementación de un sistema bilingüe (Español/Inglés) y soporte para Temas (Claro/Oscuro).
* **Interfaz Dinámica:** Creación de indicadores de progreso dinámicos para reflejar las sesiones completadas de la ronda actual.
* **Integración de Hardware:** Emitir feedback mediante la vibración del dispositivo al finalizar el cronómetro.
---

## 2. Comentarios Adicionales y Desafíos
Lo que más trabajo me costó fue lograr que las preferencias de usuario se aplicaran de manera persistente y, sobre todo, **instantánea**. Al cambiar el idioma o el tema en la `PreferencesActivity`, era necesario que los cambios se vieran reflejados en la `MainActivity` inmediatamente al regresar, por lo que opté por usar el método recreate() para actualizar la pantalla, sin embargo, en algunas ocasiones era de manera momentanea y otras se tardaba 1 minuto en hacerlo.
Asímismo, el evitar usar código hardcodeado, ya que, es la primera vez que utilizo strings.xml y dimens.xml con buenas convenciones, por lo que tuve que volver a revisar todo el código para eliminar los hardcoded.

---

## 3. Futura Versión (v2.0)
Si tuviera que producir una segunda versión, realizaría los siguientes cambios:

* **Simplificación de la Lógica:** Eliminaría la opción de "saltar sesiones" (Skip) y los indicadores de puntos de sesión para crear una experiencia más estricta, eliminando distracciones para el usuario.
* **Ejecución en Segundo Plano:** Habilitaria la opción de ejecutar en segundo plano para que el usuario pueda salirse de la aplicación para realizar sus tareas desde su celular u otras  actividades que deba realizar.

---