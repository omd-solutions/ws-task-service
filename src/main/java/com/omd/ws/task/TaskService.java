package com.omd.ws.task;

import java.util.List;
import java.util.concurrent.Future;

public interface TaskService {

    Future submit(Task task);

    Future offer(Task task) throws TaskAlreadyRunningException;

    List<TaskReport> report(TaskStatus... status);
}
