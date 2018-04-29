package models;

import enums.NotificationStatus;

import java.util.HashMap;
import java.util.Map;

public class Notification {
    private NotificationStatus status;
    private String message;
    private Map<String, String> validators;

    public Notification(){
        validators = new HashMap<String, String>();
    }

    public NotificationStatus getStatus() {
        return status;
    }

    public void setStatus(NotificationStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, String> getValidators() {
        return validators;
    }

    public void setValidators(Map<String, String> validators) {
        this.validators = validators;
    }
}
