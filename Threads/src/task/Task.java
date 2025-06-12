package task;

public class Task {
    private int id;
    private int employeeId;
    private String description;
    private double requiredTime;
    private double spentTime;
    private Status status;

    public Task(int id, int employeeId, String description, double requiredTime, double spentTime, Status status) {
        this.id = id;
        this.employeeId = employeeId;
        this.description = description;
        this.requiredTime = requiredTime;
        this.spentTime = spentTime;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public String getDescription() {
        return description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public double getRequiredTime() {
        return requiredTime;
    }

    public void setRequiredTime(double requiredTime) {
        this.requiredTime = requiredTime;
    }

    public double getSpentTime() {
        return spentTime;
    }

    public void setSpentTime(double spentTime) {
        this.spentTime = spentTime;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }
}
