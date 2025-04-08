package dto;

public class LoginResultDTO {
    private final int userId;
    private final int companyId;
    private final String role;

    public LoginResultDTO(int userId, int companyId, String role) {
        this.userId = userId;
        this.companyId = companyId;
        this.role = role;
    }

    public int getUserId() {
        return userId;
    }

    public int getCompanyId() {
        return companyId;
    }

    public String getRole() {
        return role;
    }
}
