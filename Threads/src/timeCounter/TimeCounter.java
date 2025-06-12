package timeCounter;

import manager.TaskManager;
import parser.EmployeeParser;
import parser.TaskParser;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class TimeCounter {
    public final AtomicInteger globalTime = new AtomicInteger(0);
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private TaskManager taskManager;

    public void start() {

        scheduler.scheduleAtFixedRate(
                () -> {
                    int currentTime = globalTime.incrementAndGet();
                    if (currentTime == taskManager.getWorkingDayHours() * taskManager.getHoursToSeconds() + 1) {
                        new Thread(() -> {
                            try {
                                EmployeeParser.serialize("src/data/excel.xlsx", "employee", taskManager.getEmployees());
                                TaskParser.serialize("src/data/excel.xlsx", "task", taskManager.getTasks());
                            } catch (IOException e) {
                                System.out.println("Failed to serialize file: " + e.getMessage());
                            }
                        }).start();
                    }
                    if (currentTime >= 24 * taskManager.getHoursToSeconds()) {
                        globalTime.set(0);
                    }
                },
                1, 1, TimeUnit.SECONDS);

    }

    public int getGlobalTime() {
        return globalTime.get();
    }

    public void setTaskManager(TaskManager taskManager) {
        this.taskManager = taskManager;
    }
}
