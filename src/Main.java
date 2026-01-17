import model.Task;
import model.Task.Priority;
import model.Task.Status;
import Service.TaskService;
import Thread.ReminderThread;
import Util.DateUtil;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

/**
 * Main Class
 * Entry point of the Smart Task Manager application
 * Provides console-based menu interface for user interaction
 */
public class Main {

    // Scanner for user input (shared across methods)
    private static final Scanner scanner = new Scanner(System.in);

    // TaskService instance to handle business logic
    private static final TaskService taskService = new TaskService();

    // ReminderThread instance for background reminders
    private static ReminderThread reminderThread;

    /**
     * Main method - application starts here
     */
    public static void main(String[] args) {

        // Display welcome message
        displayWelcomeBanner();

        // Start reminder thread
        startReminderThread();

        // Main application loop
        boolean exit = false;

        while (!exit) {
            try {
                displayMenu();
                int choice = getIntInput("Enter your choice: ");

                switch (choice) {
                    case 1:
                        createTask();
                        break;
                    case 2:
                        viewAllTasks();
                        break;
                    case 3:
                        viewTasksByStatus();
                        break;
                    case 4:
                        viewTasksByPriority();
                        break;
                    case 5:
                        updateTask();
                        break;
                    case 6:
                        markTaskAsCompleted();
                        break;
                    case 7:
                        deleteTask();
                        break;
                    case 8:
                        viewStatistics();
                        break;
                    case 9:
                        exit = true;
                        break;
                    default:
                        System.out.println("\n❌ Invalid choice! Please enter 1-9.\n");
                }

            } catch (Exception e) {
                System.out.println("\n❌ Error: " + e.getMessage() + "\n");
            }
        }

        // Cleanup and exit
        exitApplication();
    }

    /**
     * Display welcome banner
     */
    private static void displayWelcomeBanner() {
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║                                                            ║");
        System.out.println("║        📋 SMART TASK MANAGER & REMINDER SYSTEM 📋         ║");
        System.out.println("║                                                            ║");
        System.out.println("║              Manage Your Tasks Efficiently!                ║");
        System.out.println("║                                                            ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");
    }

    /**
     * Display main menu
     */
    private static void displayMenu() {
        System.out.println("┌────────────────────────────────────────────────────────────┐");
        System.out.println("│                        MAIN MENU                           │");
        System.out.println("├────────────────────────────────────────────────────────────┤");
        System.out.println("│  1. ➕ Create New Task                                     │");
        System.out.println("│  2. 📋 View All Tasks                                      │");
        System.out.println("│  3. 🔍 View Tasks by Status                                │");
        System.out.println("│  4. 🎯 View Tasks by Priority                              │");
        System.out.println("│  5. ✏️  Update Task                                         │");
        System.out.println("│  6. ✅ Mark Task as Completed                              │");
        System.out.println("│  7. 🗑️  Delete Task                                         │");
        System.out.println("│  8. 📊 View Statistics                                     │");
        System.out.println("│  9. 🚪 Exit                                                │");
        System.out.println("└────────────────────────────────────────────────────────────┘");
    }

    /**
     * Start the reminder thread
     */
    private static void startReminderThread() {
        reminderThread = new ReminderThread(taskService);
        reminderThread.start();
        System.out.println("✅ Reminder system activated! (Checking every " +
                ReminderThread.getCheckIntervalSeconds() + " seconds)\n");
    }

    /**
     * Create a new task
     */
    private static void createTask() {
        System.out.println("\n═══════════════ CREATE NEW TASK ═══════════════");

        // Get task details from user
        System.out.print("Enter task title: ");
        String title = scanner.nextLine();

        System.out.print("Enter task description: ");
        String description = scanner.nextLine();

        // Get priority
        Priority priority = getPriorityInput();

        // Get due date
        LocalDate dueDate = getDateInput("Enter due date (dd-MM-yyyy): ");

        try {
            // Create task using service
            Task newTask = taskService.createTask(title, description, priority, dueDate);

            System.out.println("\n✅ Task created successfully!");
            System.out.println(newTask);

        } catch (IllegalArgumentException e) {
            System.out.println("\n❌ Failed to create task: " + e.getMessage());
        }
    }

    /**
     * View all tasks
     */
    private static void viewAllTasks() {
        System.out.println("\n═══════════════ ALL TASKS ═══════════════");

        List<Task> tasks = taskService.viewAllTasks();

        if (tasks.isEmpty()) {
            System.out.println("\n📭 No tasks found. Create your first task!\n");
            return;
        }

        for (Task task : tasks) {
            System.out.println(task);
            System.out.println();
        }

        System.out.println("Total tasks: " + tasks.size());
    }

    /**
     * View tasks filtered by status
     */
    private static void viewTasksByStatus() {
        System.out.println("\n═══════════════ FILTER BY STATUS ═══════════════");
        System.out.println("1. PENDING");
        System.out.println("2. COMPLETED");

        int choice = getIntInput("Select status: ");

        Status status = (choice == 1) ? Status.PENDING : Status.COMPLETED;
        List<Task> tasks = taskService.viewTasksByStatus(status);

        if (tasks.isEmpty()) {
            System.out.println("\n📭 No " + status + " tasks found.\n");
            return;
        }

        System.out.println("\n" + status + " TASKS:");
        for (Task task : tasks) {
            System.out.println(task);
            System.out.println();
        }
    }

    /**
     * View tasks filtered by priority
     */
    private static void viewTasksByPriority() {
        System.out.println("\n═══════════════ FILTER BY PRIORITY ═══════════════");

        Priority priority = getPriorityInput();
        List<Task> tasks = taskService.viewTasksByPriority(priority);

        if (tasks.isEmpty()) {
            System.out.println("\n📭 No " + priority + " priority tasks found.\n");
            return;
        }

        System.out.println("\n" + priority + " PRIORITY TASKS:");
        for (Task task : tasks) {
            System.out.println(task);
            System.out.println();
        }
    }

    /**
     * Update an existing task
     */
    private static void updateTask() {
        System.out.println("\n═══════════════ UPDATE TASK ═══════════════");

        int taskId = getIntInput("Enter task ID to update: ");

        Task existingTask = taskService.findTaskById(taskId);

        if (existingTask == null) {
            System.out.println("\n❌ Task not found with ID: " + taskId);
            return;
        }

        System.out.println("\nCurrent task details:");
        System.out.println(existingTask);

        System.out.println("\nEnter new values (press Enter to keep existing):");

        System.out.print("New title: ");
        String title = scanner.nextLine();
        if (title.isEmpty()) title = null;

        System.out.print("New description: ");
        String description = scanner.nextLine();
        if (description.isEmpty()) description = null;

        System.out.print("Update priority? (y/n): ");
        Priority priority = null;
        if (scanner.nextLine().equalsIgnoreCase("y")) {
            priority = getPriorityInput();
        }

        System.out.print("Update due date? (y/n): ");
        LocalDate dueDate = null;
        if (scanner.nextLine().equalsIgnoreCase("y")) {
            dueDate = getDateInput("Enter new due date (dd-MM-yyyy): ");
        }

        try {
            boolean updated = taskService.updateTask(taskId, title, description, priority, dueDate);

            if (updated) {
                System.out.println("\n✅ Task updated successfully!");
            }

        } catch (IllegalArgumentException e) {
            System.out.println("\n❌ Update failed: " + e.getMessage());
        }
    }

    /**
     * Mark a task as completed
     */
    private static void markTaskAsCompleted() {
        System.out.println("\n═══════════════ MARK AS COMPLETED ═══════════════");

        int taskId = getIntInput("Enter task ID to mark as completed: ");

        boolean success = taskService.markTaskAsCompleted(taskId);

        if (success) {
            System.out.println("\n✅ Task marked as COMPLETED!");
        } else {
            System.out.println("\n❌ Task not found with ID: " + taskId);
        }
    }

    /**
     * Delete a task
     */
    private static void deleteTask() {
        System.out.println("\n═══════════════ DELETE TASK ═══════════════");

        int taskId = getIntInput("Enter task ID to delete: ");

        Task task = taskService.findTaskById(taskId);

        if (task == null) {
            System.out.println("\n❌ Task not found with ID: " + taskId);
            return;
        }

        System.out.println("\nTask to delete:");
        System.out.println(task);

        System.out.print("\nAre you sure? (yes/no): ");
        String confirmation = scanner.nextLine();

        if (confirmation.equalsIgnoreCase("yes")) {
            taskService.deleteTask(taskId);
            System.out.println("\n✅ Task deleted successfully!");
        } else {
            System.out.println("\n❌ Deletion cancelled.");
        }
    }

    /**
     * View task statistics
     */
    private static void viewStatistics() {
        System.out.println("\n═══════════════ TASK STATISTICS ═══════════════");

        int totalTasks = taskService.viewAllTasks().size();
        int pendingTasks = taskService.getPendingTaskCount();
        int completedTasks = taskService.getCompletedTaskCount();

        System.out.println("┌────────────────────────────────────────────────┐");
        System.out.printf("│ Total Tasks      : %-24d │%n", totalTasks);
        System.out.printf("│ Pending Tasks    : %-24d │%n", pendingTasks);
        System.out.printf("│ Completed Tasks  : %-24d │%n", completedTasks);
        System.out.println("└────────────────────────────────────────────────┘\n");
    }

    /**
     * Get integer input from user with validation
     */
    private static int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid input! Please enter a number.");
            }
        }
    }

    /**
     * Get priority input from user
     */
    private static Priority getPriorityInput() {
        System.out.println("\nSelect priority:");
        System.out.println("1. HIGH");
        System.out.println("2. MEDIUM");
        System.out.println("3. LOW");

        int choice = getIntInput("Enter choice (1-3): ");

        return switch (choice) {
            case 1 -> Priority.HIGH;
            case 2 -> Priority.MEDIUM;
            case 3 -> Priority.LOW;
            default -> {
                System.out.println("Invalid choice. Defaulting to MEDIUM.");
                yield Priority.MEDIUM;
            }
        };
    }

    /**
     * Get date input from user with validation
     */
    private static LocalDate getDateInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String dateStr = scanner.nextLine();
                return DateUtil.parseDateUserFormat(dateStr);
            } catch (IllegalArgumentException e) {
                System.out.println("❌ " + e.getMessage());
            }
        }
    }

    /**
     * Exit application gracefully
     */
    private static void exitApplication() {
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║                 👋 THANK YOU! 👋                           ║");
        System.out.println("║                                                            ║");
        System.out.println("║         Your tasks have been saved successfully!           ║");
        System.out.println("║              See you next time! 😊                         ║");
        System.out.println("║                                                            ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");

        // Stop reminder thread
        if (reminderThread != null) {
            reminderThread.stopReminder();
        }

        // Close scanner
        scanner.close();

        System.exit(0);
    }
}