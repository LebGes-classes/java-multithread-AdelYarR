package manager;

import employee.Employee;
import task.Status;
import task.Task;
import timeCounter.TimeCounter;
import utils.ConsoleCleaner;
import java.util.ArrayList;
import java.util.Scanner;

public class TaskManager {

    private ArrayList<Employee> employees;
    private ArrayList<Task> tasks;
    private ArrayList<String> logs;

    private final int WORKING_DAY_HOURS = 8;
    private final int HOURS_TO_SECONDS = 2;
    private final TimeCounter timeCounter;

    private final Object tasksLock = new Object();
    private final Object logsLock = new Object();

    public TaskManager(ArrayList<Employee> employees, ArrayList<Task> tasks, TimeCounter timeCounter) {
        this.employees = employees;
        this.tasks = tasks;
        this.timeCounter = timeCounter;
        this.logs = new ArrayList<>();

        timeCounter.setTaskManager(this);
        for (Employee employee : employees) {
            employee.setTaskManager(this);
        }
    }

    public void startMenu() {

        for (Employee employee : employees) {
            employee.start();
        }

        Scanner scanner = new Scanner(System.in);

        boolean running = true;
        while (running) {
            ConsoleCleaner.clearConsole();
            System.out.println("Task Manager\n" +
                    "1 - Get info about the working day\n" +
                    "2 - Add task\n" +
                    "q - Quit menu\n"
            );

            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    printInfo();
                    scanner.nextLine();
                    break;
                case "2":
                    addTask();
                    scanner.nextLine();
                    break;
                case "q":
                    running = false;
                    break;
            }
        }
    }

    private void printInfo() {
        ConsoleCleaner.clearConsole();
        System.out.println("Time: " + timeCounter.getGlobalTime() + "\n");
        for (String log : logs) {
            System.out.println(log);
        }
        System.out.println();

        System.out.println("-------------------------------------------------------------------");
        System.out.printf("%-5s | %-7s | %-12s | %-12s | %-15s%n", "ID", "Task ID", "Time At Work", "Active Time", "Inactive time");
        System.out.println("-------------------------------------------------------------------");
        for (Employee employee : employees) {
            String taskId = employee.getCurrentTask() != null ? "" + employee.getCurrentTask().getId() : "EMPTY";
            System.out.printf("%-5s | %-7s | %-12s | %-12s | %-15s%n",
                    employee.getEmployeeId(),
                    taskId,
                    employee.getTimeAtWork(),
                    employee.getTotalActiveTime(),
                    employee.getTotalInactiveTime());
        }
        System.out.println("-------------------------------------------------------------------");
        System.out.println();
        System.out.println("--------------------------------------");
        System.out.printf("%-5s | %-12s | %-12s%n", "ID", "Status", "Required time");
        System.out.println("--------------------------------------");
        for (Task task : tasks) {
            System.out.printf("%-5s | %-12s | %-12s%n",
                    task.getId(),
                    task.getStatus(),
                    task.getRequiredTime());
        }
        System.out.println("--------------------------------------");
        System.out.println("Press any key to return: ");
    }

    private void addTask() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter task description:");
        String description = scanner.nextLine();

        System.out.println("Enter required time to complete the task:");
        double requiredTime = scanner.nextDouble();
        scanner.nextLine();

        if (requiredTime <= 0) {
            System.out.println("Required time must be a positive number. Press any key to return:");
            return;
        }

        int id = tasks.stream()
                .mapToInt(Task::getId)
                .max()
                .orElse(0) + 1;

        Task task = new Task(id, -1, description, requiredTime, 0, Status.NEW);
        synchronized (tasksLock) {
            tasks.add(task);
        }
        synchronized (logsLock) {
            logs.add("Task with the ID: " + id + " added: " + description + ", " + requiredTime + " hours.");
        }
        System.out.println("Task with ID: " + id + " was successfully added. Press any key to return: ");
    }

    public Task getNewTask(int employeeId) {
        synchronized (tasksLock) {
            for (Task task : tasks) {
                if (task.getStatus() == Status.NEW) {
                    task.setStatus(Status.IN_PROCESS);
                    task.setEmployeeId(employeeId);
                    return task;
                }
            }
        }

        return null;
    }

    public int getWorkingDayHours() {
        return WORKING_DAY_HOURS;
    }

    public int getHoursToSeconds() {
        return HOURS_TO_SECONDS;
    }

    public void addToLogs(String log) {
        synchronized (logsLock) {
            logs.add(log);
        }
    }

    public TimeCounter getTimeCounter() {
        return timeCounter;
    }

    public boolean isWorking() {
        return getTimeCounter().getGlobalTime() < 8 * HOURS_TO_SECONDS;
    }

    public ArrayList<Employee> getEmployees() {
        return employees;
    }

    public ArrayList<Task> getTasks() {
        return tasks;
    }
}
