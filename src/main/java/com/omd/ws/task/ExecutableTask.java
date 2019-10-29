package com.omd.ws.task;

@FunctionalInterface
public interface ExecutableTask {

    void run(TaskContext context) throws Exception;
}
