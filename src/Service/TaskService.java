package Service;

import model.Task;
import model.Task.Priority;
import model.Task.Status;
import Util.FileUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TaskService Class
 * Business Logic Layer - handles all task operations
 * Uses ArrayList to manage tasks in memory
 * Interacts with FileUtil for persistence
 */
public class TaskService {

    // List to store all tasks in memory
    private List<Task> taskList;

    // Counter to generate unique task IDs
    private int nextTaskId;

    /**
     * Constructor
     * Loads existing tasks from file when service is initialized
     */
    public TaskService() {
        this.taskList = new ArrayList<>();
        loadTasksFromFile();

        // Set nextTaskId based on existing tasks
        if (taskList.isEmpty()) {
            nextTaskId = 1;
        } else {
            // Find maximum ID and increment
            nextTaskId = taskList.stream()
                    .mapToInt(Task::getTaskId)
                    .max()
                    .orElse(0) + 1;
        }
    }

    /**
     * Create a new task
     * @param title - task title
     * @param description - task description
     * @param priority - HIGH, MEDIUM, or LOW
     * @param dueDate - deadline for the task
     * @return created Task object
     */
    public Task createTask(String title, String description,
                           Priority priority, LocalDate dueDate) {

        // Validation
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Task title cannot be empty!");
        }

        if (dueDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Due date cannot be in the past!");
        }

        // Create new task with auto-generated ID
        Task newTask = new Task(nextTaskId++, title, description,
                priority, dueDate, Status.PENDING);

        // Add to list
        taskList.add(newTask);

        // Save to file
        saveTasksToFile();

        return newTask;
    }

    /**
     * View all tasks
     * @return List of all tasks
     */
    public List<Task> viewAllTasks() {
        return new ArrayList<>(taskList); // Return copy to prevent external modification
    }

    /**
     * View tasks by status
     * @param status - PENDING or COMPLETED
     * @return filtered list of tasks
     */
    public List<Task> viewTasksByStatus(Status status) {
        return taskList.stream()
                .filter(task -> task.getStatus() == status)
                .collect(Collectors.toList());
    }

    /**
     * View tasks by priority
     * @param priority - HIGH, MEDIUM, or LOW
     * @return filtered list of tasks
     */
    public List<Task> viewTasksByPriority(Priority priority) {
        return taskList.stream()
                .filter(task -> task.getPriority() == priority)
                .collect(Collectors.toList());
    }

    /**
     * Find a task by ID
     * @param taskId - unique task identifier
     * @return Task object if found, null otherwise
     */
    public Task findTaskById(int taskId) {
        return taskList.stream()
                .filter(task -> task.getTaskId() == taskId)
                .findFirst()
                .orElse(null);
    }

    /**
     * Update an existing task
     * @param taskId - ID of task to update
     * @param title - new title (null to keep existing)
     * @param description - new description (null to keep existing)
     * @param priority - new priority (null to keep existing)
     * @param dueDate - new due date (null to keep existing)
     * @return true if updated successfully, false if task not found
     */
    public boolean updateTask(int taskId, String title, String description,
                              Priority priority, LocalDate dueDate) {

        Task task = findTaskById(taskId);

        if (task == null) {
            return false;
        }

        // Update only non-null values
        if (title != null && !title.trim().isEmpty()) {
            task.setTitle(title);
        }

        if (description != null) {
            task.setDescription(description);
        }

        if (priority != null) {
            task.setPriority(priority);
        }

        if (dueDate != null) {
            if (dueDate.isBefore(LocalDate.now())) {
                throw new IllegalArgumentException("Due date cannot be in the past!");
            }
            task.setDueDate(dueDate);
        }

        // Save changes to file
        saveTasksToFile();

        return true;
    }

    /**
     * Mark a task as completed
     * @param taskId - ID of task to mark complete
     * @return true if successful, false if task not found
     */
    public boolean markTaskAsCompleted(int taskId) {
        Task task = findTaskById(taskId);

        if (task == null) {
            return false;
        }

        task.setStatus(Status.COMPLETED);
        saveTasksToFile();

        return true;
    }

    /**
     * Delete a task
     * @param taskId - ID of task to delete
     * @return true if deleted successfully, false if task not found
     */
    public boolean deleteTask(int taskId) {
        Task task = findTaskById(taskId);

        if (task == null) {
            return false;
        }

        taskList.remove(task);
        saveTasksToFile();

        return true;
    }

    /**
     * Get tasks that are due today or overdue
     * Used by reminder thread
     * @return list of tasks needing reminders
     */
    public List<Task> getTasksNeedingReminder() {
        LocalDate today = LocalDate.now();

        return taskList.stream()
                .filter(task -> task.getStatus() == Status.PENDING)
                .filter(task -> !task.getDueDate().isAfter(today))
                .collect(Collectors.toList());
    }

    /**
     * Get count of pending tasks
     * @return number of pending tasks
     */
    public int getPendingTaskCount() {
        return (int) taskList.stream()
                .filter(task -> task.getStatus() == Status.PENDING)
                .count();
    }

    /**
     * Get count of completed tasks
     * @return number of completed tasks
     */
    public int getCompletedTaskCount() {
        return (int) taskList.stream()
                .filter(task -> task.getStatus() == Status.COMPLETED)
                .count();
    }

    /**
     * Save all tasks to file using FileUtil
     */
    private void saveTasksToFile() {
        FileUtil.saveTasks(taskList);
    }

    /**
     * Load all tasks from file using FileUtil
     */
    private void loadTasksFromFile() {
        this.taskList = FileUtil.loadTasks();
    }
}