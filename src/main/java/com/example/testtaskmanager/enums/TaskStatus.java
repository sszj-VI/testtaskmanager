package com.example.testtaskmanager.enums;

public enum TaskStatus {

    TODO,
    DOING,
    DONE;

    public static boolean isValid(String status) {
        if (status == null) {
            return false;
        }

        for (TaskStatus taskStatus : TaskStatus.values()) {
            if (taskStatus.name().equals(status)) {
                return true;
            }
        }

        return false;
    }
}
