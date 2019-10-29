package com.omd.ws.task;

import java.util.Objects;
import java.util.UUID;

public class Task {

    private String id;
    private String name;
    private ExecutableTask executable;

    public Task(String name, ExecutableTask executable) {
        this(UUID.randomUUID().toString(), name, executable);
    }

    public Task(String id, String name, ExecutableTask executable) {
        this.id = id;
        this.name = name;
        this.executable = executable;
    }

    String getId() {
        return id;
    }

    String getName() {
        return name;
    }

    ExecutableTask getExecutable() {
        return executable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id.equals(task.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
