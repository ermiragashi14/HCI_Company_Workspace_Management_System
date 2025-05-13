package dto;

import javafx.beans.property.SimpleStringProperty;

public class ManageReservationDTO {
    private int id;
    private String userFullName;
    private String workspaceName;
    private String date;
    private String schedule;
    private String status;
    private String previousStatus;
    private String modifiedBy;
    private String modifiedAt;
    private String createdAt;

    public ManageReservationDTO(int id, String userFullName, String workspaceName, String date, String schedule, String status,
                                String previousStatus, String modifiedBy, String modifiedAt, String createdAt) {
        this.id = id;
        this.userFullName = userFullName;
        this.workspaceName = workspaceName;
        this.date = date;
        this.schedule = schedule;
        this.status = status;
        this.previousStatus = previousStatus;
        this.modifiedBy = modifiedBy;
        this.modifiedAt = modifiedAt;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public String getUserFullName() { return userFullName; }
    public String getWorkspaceName() { return workspaceName; }
    public String getDate() { return date; }
    public String getSchedule() { return schedule; }
    public String getStatus() { return status; }
    public String getPreviousStatus() { return previousStatus; }
    public String getModifiedBy() { return modifiedBy; }
    public String getModifiedAt() { return modifiedAt; }
    public String getCreatedAt() { return createdAt; }


    public SimpleStringProperty workspaceNameProperty() {
        return new SimpleStringProperty(workspaceName);
    }

    public SimpleStringProperty dateProperty() {
        return new SimpleStringProperty(date);
    }

    public SimpleStringProperty scheduleProperty() {
        return new SimpleStringProperty(schedule);
    }

    public SimpleStringProperty statusProperty() {
        return new SimpleStringProperty(status);
    }
}
