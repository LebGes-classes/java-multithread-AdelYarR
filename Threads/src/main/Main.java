package main;

import employee.Employee;
import manager.TaskManager;
import parser.EmployeeParser;
import parser.TaskParser;
import task.Task;
import timeCounter.TimeCounter;

import java.io.IOException;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        ArrayList<Employee> employees;
        ArrayList<Task> tasks;
        try {
            employees = EmployeeParser.parse("src/data/excel.xlsx", "employee");
        } catch (IOException e) {
            System.out.println("Failed to parse file: " + e.getMessage());
            return;
        }
        try {
            tasks = TaskParser.parse("src/data/excel.xlsx", "task");
        } catch (IOException e) {
            System.out.println("Failed to parse file: " + e.getMessage());
            return;
        }

//        ArrayList<Employee> employees = new ArrayList<>();
//        employees.add(new Employee(1, "Bob"));
//        employees.add(new Employee(2, "Tom"));
//        employees.add(new Employee(3, "Artem"));
//
//        ArrayList<Task> tasks = new ArrayList<>();
//        tasks.add(new Task(6, -1, "Description 1", 4000, 0, Status.NEW));
//        tasks.add(new Task(7, -1, "Description 2", 8000, 0, Status.NEW));
//        tasks.add(new Task(8, -1, "Description 3", 16000, 0, Status.NEW));
//        tasks.add(new Task(9, -1, "Description 4", 1000, 0, Status.NEW));

        TimeCounter timeCounter = new TimeCounter();
        timeCounter.start();

        TaskManager taskManager = new TaskManager(employees, tasks, timeCounter);
        taskManager.startMenu();
    }
}