package service;

public class SessionManager {
    private static SessionManager instance;
    private int loggedInUserId;
    private String loggedInUserRole;

    private SessionManager() {
        this.loggedInUserId = -1;
        this.loggedInUserRole = null;
    }

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void setLoggedInUser(int userId, String role) {
        this.loggedInUserId = userId;
        this.loggedInUserRole = role;
    }

    public int getLoggedInUserId() {
        return loggedInUserId;
    }

    public String getLoggedInUserRole() {
        return loggedInUserRole;
    }

    public void clearSession() {
        this.loggedInUserId = -1;
        this.loggedInUserRole = null;
    }
}
