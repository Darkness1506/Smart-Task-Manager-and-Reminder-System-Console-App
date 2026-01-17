package Util;

import model.Task;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * FileUtil Class
 * Handles all file I/O operations
 * Saves and loads tasks from a text file in CSV format
 * Uses BufferedReader and BufferedWriter for efficient file handling
 */
public class FileUtil {

    // File name where tasks will be stored
    private static final String FILE_NAME = "tasks.txt";

    /**
     * Save all tasks to file
     * Each task is stored as one line in CSV format
     * @param tasks - List of tasks to save
     */
    public static void saveTasks(List<Task> tasks) {

        // Try-with-resources: automatically closes the writer
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {

            // Write each task as a CSV line
            for (Task task : tasks) {
                writer.write(task.toCSV());
                writer.newLine(); // Add new line after each task
            }

        } catch (IOException e) {
            // Handle file writing errors
            System.err.println("ERROR: Unable to save tasks to file!");
            System.err.println("Reason: " + e.getMessage());
        }
    }

    /**
     * Load all tasks from file
     * Reads CSV lines and converts them back to Task objects
     * @return List of tasks loaded from file
     */
    public static List<Task> loadTasks() {

        List<Task> tasks = new ArrayList<>();

        // Check if file exists
        File file = new File(FILE_NAME);

        if (!file.exists()) {
            // File doesn't exist yet (first run)
            System.out.println("INFO: No existing task file found. Starting fresh.");
            return tasks; // Return empty list
        }

        // Try-with-resources: automatically closes the reader
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {

            String line;

            // Read file line by line
            while ((line = reader.readLine()) != null) {

                // Skip empty lines
                if (line.trim().isEmpty()) {
                    continue;
                }

                try {
                    // Convert CSV line to Task object
                    Task task = Task.fromCSV(line);
                    tasks.add(task);

                } catch (Exception e) {
                    // Handle corrupted line - skip and continue
                    System.err.println("WARNING: Skipping corrupted task data: " + line);
                }
            }

            System.out.println("INFO: Successfully loaded " + tasks.size() + " task(s) from file.");

        } catch (IOException e) {
            // Handle file reading errors
            System.err.println("ERROR: Unable to load tasks from file!");
            System.err.println("Reason: " + e.getMessage());
        }

        return tasks;
    }

    /**
     * Delete the task file (useful for testing or reset)
     * @return true if deleted successfully, false otherwise
     */
    public static boolean deleteTaskFile() {
        File file = new File(FILE_NAME);

        if (file.exists()) {
            return file.delete();
        }

        return false;
    }

    /**
     * Check if task file exists
     * @return true if file exists, false otherwise
     */
    public static boolean taskFileExists() {
        File file = new File(FILE_NAME);
        return file.exists();
    }

    /**
     * Get the number of tasks stored in file
     * @return count of tasks in file
     */
    public static int getTaskCountInFile() {
        int count = 0;

        File file = new File(FILE_NAME);

        if (!file.exists()) {
            return 0;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {

            while (reader.readLine() != null) {
                count++;
            }

        } catch (IOException e) {
            System.err.println("ERROR: Unable to read task file!");
        }

        return count;
    }
}