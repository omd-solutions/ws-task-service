package com.omd.ws.task;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class WsTaskController {

    private TaskService taskService;

    WsTaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @RequestMapping(value = "/api/tasks", method = RequestMethod.GET)
    public List<TaskReport> getTasks(@RequestParam TaskStatus... status) {
        return taskService.report(status);
    }
}
