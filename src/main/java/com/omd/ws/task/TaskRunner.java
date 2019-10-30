package com.omd.ws.task;

public class TaskRunner implements Runnable, TaskContext.TaskProgressListener {

    private Task task;
    private TaskContext taskContext;
    private TaskReport taskReport;
    private Long completed;
    private BatchedMessageSender<TaskReport> messageSender;

    TaskRunner(Task task, BatchedMessageSender<TaskReport> messageSender) {
        this.task = task;
        this.taskContext = new TaskContext(this);
        this.taskReport = new TaskReport(task);
        this.messageSender = messageSender;
    }

    @Override
    public void run() {
        taskReport.started();
        messageSender.convertAndSend(taskReport);
        try {
            task.getExecutable().run(taskContext);
            taskReport.success();
            messageSender.convertAndSend(taskReport);
        } catch (Exception e) {
            taskReport.error(e);
            messageSender.convertAndSend(taskReport);
        } finally {
            completed = System.currentTimeMillis();
        }
    }

    @Override
    public void progressUpdate(String message, double percent) {
        taskReport.setProgress(percent);
        if (message != null) {
            taskReport.setStatusMessage(message);
        }
        messageSender.convertAndSend(taskReport);
    }

    @Override
    public void taskWarning(String message) {
        taskReport.addWarning(message);
        messageSender.convertAndSend(taskReport);
    }

    TaskReport report() {
        return taskReport;
    }

    boolean completedBefore(long millis) {
        return completed != null && completed < millis;
    }

    boolean hasErrorOrWarnings() {
        return taskReport.getErrorStack() != null || taskReport.getWarnings().size() > 0;
    }

    String getTaskId() {
        return task.getId();
    }

    String getTaskName() {
        return task.getName();
    }

    boolean isComplete() {
        return completed != null;
    }
}
