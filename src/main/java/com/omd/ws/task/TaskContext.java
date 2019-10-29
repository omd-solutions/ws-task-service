package com.omd.ws.task;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class TaskContext {

    interface TaskProgressListener {
        void progressUpdate(String message, double percent);
        void taskWarning(String message);
    }

    private TaskProgressListener listener;
    private String message;
    private double progress;
    private double totalPercent = 100d;

    TaskContext(TaskProgressListener listener) {
        this.listener = listener;
    }

    private TaskContext(TaskProgressListener listener, String message, double progress, double totalPercent) {
        this.listener = listener;
        this.message = message;
        this.progress = progress;
        this.totalPercent = totalPercent;
    }

    public void setProgress(String message) {
        setProgress(message, progress);
    }

    public void setProgress(double percent) {
        setProgress(null, percent);
    }

    public void setProgress(String message, double percent) {
        double newProgress = Math.min(percent, 100d);
        newProgress = Math.max(newProgress, 0d);
        newProgress = (newProgress / 100d) * totalPercent;
        progress = BigDecimal.valueOf(newProgress)
                .setScale(2, RoundingMode.HALF_EVEN)
                .doubleValue();
        this.message = message == null ? this.message : message;
        listener.progressUpdate(this.message, progress);
    }

    public void addProgress(double percent) {
        setProgress(null, progress + percent);
    }

    public void addProgress(String message, double percent) {
        setProgress(message, progress + percent);
    }

    public void addWarning(String warning) {
        listener.taskWarning(warning);
    }

    public TaskContext createSubContext(double percent) {
        return new TaskContext(listener, message, progress, percent);
    }
}
