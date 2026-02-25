package com.example.labpractica;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase Task Manager que implementa operaciones CRUD.
 * Utiliza una estructura ArrayList para almacenar las tareas sugeridas.
 *
 * @author Alan Kevin Cano Tenorio
 * @version 1.0
 */
public class TaskManager {

    // Lista que guaradá todas las tareas
    private List<String> taskL;

    /**
     * Constructor por defecto (lista de tareas como un ArrayList vacío)
     */
    public TaskManager() {
        this.taskL = new ArrayList<>();
    }

    // Implementación de las operaciones CRUD

    /**
     * CREATE - Agrega una nueva tarea a la lista.
     *
     * @param task Tarea a agregar
     * @return true si la tarea se agregó correctamente, false si la tarea es nula o vacía
     */
    public boolean addTask(String task) {
        // Se verifica que la tarea es vacía
        if (task == null || task.trim().isEmpty()) {
            System.out.println("Error: La tarea no puede agregarse porque está vacía");
            return false;
        }

        // Se agrega la tarea a la lista
        taskL.add(task);
        System.out.println("La siguiente tarea fue agregada correctamente: " + task);
        return true;
    }

    /**
     * CREATE - Se agregan múltiples tareas a la lista.
     *
     * @param tasks Lista de tareas a agregar
     * @return Número de tareas agregadas correctamente
     */
    public int addMultipleTasks(List<String> tasks) {
        int i = 0;
        if (tasks != null) {
            for (String task : tasks) {
                if (addTask(task)) {
                    i++;
                }
            }
        }
        return i;
    }

    /**
     * READ - Devvuelve una tarea específica por su índice.
     *
     * @param index Índice de la tarea a obtener.
     * @return La tarea si el índice es válido, null en caso contrario.
     */
    public String getTask(int index) {
        if (index >= 0 && index < taskL.size()) {
            return taskL.get(index);
        } else {
            System.out.println("Error: Índice fuera de rango");
            return null;
        }
    }

    /**
     * READ - Obtiene todas las tareas de la lista.
     *
     * @return Lista que contiene todas las tareas.
     */
    public List<String> getAllTasks() {
        return taskL;
    }

    /**
     * READ - Muestra todas las tareas en consola.
     */
    public void displayAllTasks() {
        if (taskL.isEmpty()) {
            System.out.println("No hay tareas disponibles.");
            return;
        }

        System.out.println("Lista de Tareas:");
        for (int i = 0; i < taskL.size(); i++) {
            System.out.println((i + 1) + ". " + taskL.get(i));
        }
        System.out.println("Total de tareas: " + taskL.size() + "\n");
    }

    /**
     * UPDATE - Actualiza una tarea.
     *
     * @param index Índice de la tarea a actualizar.
     * @param newTask Actualizacción de la tarea.
     * @return true si se actualizó correctamente, false en caso contrario.
     */
    public boolean updateTask(int index, String newTask) {
        // Verifica si se está actualizando la tarea con datos válidos.
        if (newTask == null || newTask.trim().isEmpty()) {
            System.out.println("Error: La nueva tarea no debe ser vacía");
            return false;
        }
        // Verifica si el indice brindado es válido
        if (index < 0 || index >= taskL.size()) {
            System.out.println("Error: Índice fuera de rango");
            return false;
        }

        // Actualizar la tarea
        String oldTask = taskL.get(index);
        taskL.set(index, newTask);
        System.out.println("Tarea actualizada correctamente: \n"
                + "Antes: " + oldTask
                + "Ahora: " + newTask);
        return true;
    }

    /**
     * DELETE - Elimina una tarea específica.
     *
     * @param index Índice de la tarea a eliminar.
     * @return true si se eliminó correctamente, false en caso contrario.
     */
    public boolean deleteTask(int index) {
        // Verifica que el indice sea válido.
        if (index < 0 || index >= taskL.size()) {
            System.out.println("Error: Índice fuera de rango");
            return false;
        }

        // Elimina la tarea
        String deletedTask = taskL.remove(index);
        System.out.println("Tarea eliminada correctamente: " + deletedTask);
        return true;
    }

    /**
     * DELETE - Elimina todas las tareas de la lista.
     */
    public void deleteAllTasks() {
        int size = taskL.size();
        taskL.clear();
        System.out.println("Se eliminaron " + size + " tareas correctamente");
    }
}