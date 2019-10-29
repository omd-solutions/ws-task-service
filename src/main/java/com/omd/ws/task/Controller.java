package com.omd.ws.task;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public class Controller {

    private TaskService taskService;

    Controller(TaskService taskService) {
        this.taskService = taskService;
    }

    @RequestMapping(value = "/api/tasks", method = RequestMethod.GET)
    public List<TaskReport> getTasks(@RequestParam TaskStatus... status) {
        return taskService.report(status);
    }
}
