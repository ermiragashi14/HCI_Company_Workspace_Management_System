package dto.Register;

public class RegisterResultDTO {
    private int userId;
    private int companyId;

    public RegisterResultDTO(int userId, int companyId) {
        this.userId = userId;
        this.companyId = companyId;
    }

    public int getUserId() {
        return userId;
    }

    public int getCompanyId() {
        return companyId;
    }
}
