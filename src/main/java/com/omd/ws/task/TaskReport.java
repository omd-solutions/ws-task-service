package com.omd.ws.task;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class TaskReport {

    private String taskId;
    private String taskName;
    private String status;
    private String statusMessage;
    private double progress;
    private long queuedAt;
    private long startedAt;
    private long finishedAt;
    private List<String> warnings;
    private String errorStack;

    TaskReport(Task task) {
        taskId = task.getId();
        taskName = task.getName();
        status = TaskStatus.QUEUED.getFriendlyName();
        statusMessage = "Not Started";
        progress = 0d;
        queuedAt = System.currentTimeMillis();
        warnings = new ArrayList<>();
    }

    void started() {
        status = TaskStatus.RUNNING.getFriendlyName();
        statusMessage = "Started";
        startedAt = System.currentTimeMillis();
    }

    void success() {
        status = TaskStatus.SUCCESS.getFriendlyName();
        statusMessage = "Finished";
        finishedAt = System.currentTimeMillis();
    }

    void error(Exception e) {
        status = TaskStatus.ERROR.getFriendlyName();
        statusMessage = "Error";
        finishedAt = System.currentTimeMillis();
        errorStack = stringFromException(e);
    }

    String stringFromException(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }

    public String getTaskId() {
        return taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public String getStatus() {
        return status;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public double getProgress() {
        return progress;
    }

    void setProgress(double progress) {
        this.progress = progress;
    }

    public long getQueuedAt() {
        return queuedAt;
    }

    public long getStartedAt() {
        return startedAt;
    }

    public long getFinishedAt() {
        return finishedAt;
    }

    public List<String> getWarnings() {
        return warnings;
    }

    void addWarning(String warning) {
        warnings.add(warning);
    }

    public String getErrorStack() {
        return errorStack;
    }

    boolean isOfAnyStatus(Set<String> statues) {
        return statues.contains(status);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskReport that = (TaskReport) o;
        return taskId.equals(that.taskId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskId);
    }

}
