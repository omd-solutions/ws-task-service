package com.omd.ws.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

public class SimpleTaskService implements TaskService {

    private long minsToKeepErroredTasks;
    private ExecutorService executor;
    private final List<TaskRunner> taskRunners = new ArrayList<>();
    private BatchedMessageSender<TaskReport> messageSender;

    private static final Logger LOG = LoggerFactory.getLogger(SimpleTaskService.class);

    SimpleTaskService(long minsToKeepErroredTasks, int threadCount,
                             BatchedMessageSender<TaskReport> messageSender) {
        this.minsToKeepErroredTasks = minsToKeepErroredTasks;
        executor = Executors.newFixedThreadPool(threadCount);
        this.messageSender = messageSender;
    }

    @Override
    public Future submit(Task task) {
        TaskRunner runner = new TaskRunner(task, messageSender);
        synchronized (taskRunners) {
            taskRunners.add(runner);
        }
        return executor.submit(runner);
    }

    @Override
    public Future offer(Task task) throws TaskAlreadyRunningException {
        synchronized (taskRunners) {
            if(taskRunners.stream().anyMatch(tr -> task.getId().equals(tr.getTaskId()) && !tr.isComplete())) {
                throw new TaskAlreadyRunningException();
            }
        }
        return submit(task);
    }

    @Override
    public List<TaskReport> report(TaskStatus... status) {
        Set<String> statues = Arrays.stream(status).map(TaskStatus::getFriendlyName).collect(toSet());
        synchronized (taskRunners) {
            return taskRunners.stream().map(TaskRunner::report).filter(tr -> tr.isOfAnyStatus(statues)).collect(toList());
        }
    }

    @Scheduled(initialDelay = 10000, fixedDelay = 10000)
    public void cleanupTaskRunners() {
        long errorCleanupThreshold = System.currentTimeMillis() - (minsToKeepErroredTasks * 60000);
        long successCleanupThreshold = System.currentTimeMillis() - 20000; //10 Seconds
        synchronized (taskRunners) {
            List<TaskRunner> toRemove = taskRunners.stream().filter(t -> {
                if(t.hasErrorOrWarnings()) {
                    return t.completedBefore(errorCleanupThreshold);
                } else {
                    return t.completedBefore(successCleanupThreshold);
                }
            }).collect(toList());
            if(!toRemove.isEmpty()) {
                LOG.info("Discarding " + toRemove.size() + " old completed tasks");
                taskRunners.removeAll(toRemove);
            }
        }
    }
}
