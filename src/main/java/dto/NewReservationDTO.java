package dto;

public class NewReservationDTO {
    private int workspaceId;
    private String workspaceName;

    public NewReservationDTO(int workspaceId, String workspaceName) {
        this.workspaceId = workspaceId;
        this.workspaceName = workspaceName;
    }

    public int getWorkspaceId() {
        return workspaceId;
    }

    public String getWorkspaceName() {
        return workspaceName;
    }

    @Override
    public String toString() {
        return workspaceName;
    }
}