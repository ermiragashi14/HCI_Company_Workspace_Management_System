package service;

public class SessionManager {
    private static SessionManager instance;
    private int loggedInUserId;
    private String loggedInUserRole;
    private int loggedInCompanyId;
    private boolean notificationAlertShown = false;

    private SessionManager() {
        this.loggedInUserId = -1;
        this.loggedInUserRole = null;
        this.loggedInCompanyId = -1;
    }

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void setLoggedInUser(int userId, String role, int companyId) {
        this.loggedInUserId = userId;
        this.loggedInUserRole = role;
        this.loggedInCompanyId = companyId;

    }

    public int getLoggedInUserId() {
        return loggedInUserId;
    }

    public String getLoggedInUserRole() {
        return loggedInUserRole;
    }

    public int getLoggedInCompanyId(){
        return loggedInCompanyId;
    }
    public boolean isNotificationAlertShown() {
        return notificationAlertShown;
    }

    public void setNotificationAlertShown(boolean notificationAlertShown) {
        this.notificationAlertShown = notificationAlertShown;
    }

    public void clearSession() {
        this.loggedInUserId = -1;
        this.loggedInUserRole = null;
        this.loggedInCompanyId = -1;
        this.notificationAlertShown = false;
    }
}
