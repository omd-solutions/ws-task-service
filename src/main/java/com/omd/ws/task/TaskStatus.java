package com.omd.ws.task;

public enum TaskStatus {
    QUEUED("Queued"),
    RUNNING("Running"),
    SUCCESS("Success"),
    ERROR("Error");

    private String friendlyName;

    TaskStatus(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    public String getFriendlyName() {
        return friendlyName;
    }
}
