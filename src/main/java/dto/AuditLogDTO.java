package dto;

public class AuditLogDTO {
    private int id;
    private int userId;
    private int companyId;
    private String userFullName;
    private String companyName;
    private String actionType;
    private String details;
    private String timestamp;

    public AuditLogDTO(int userId, int companyId, String actionType, String details) {
        this.userId = userId;
        this.companyId = companyId;
        this.actionType = actionType;
        this.details = details;
    }

    public AuditLogDTO(int id, int userId, int companyId, String userFullName, String companyName,
                       String actionType, String details, String timestamp) {
        this.id = id;
        this.userId = userId;
        this.companyId = companyId;
        this.userFullName = userFullName;
        this.companyName = companyName;
        this.actionType = actionType;
        this.details = details;
        this.timestamp = timestamp;
    }

    public int getId() { return id; }
    public int getUserId() { return userId; }
    public int getCompanyId() { return companyId; }
    public String getUserFullName() { return userFullName; }
    public String getCompanyName() { return companyName; }
    public String getActionType() { return actionType; }
    public String getDetails() { return details; }
    public String getTimestamp() { return timestamp; }
}
