package dto;

public class RecentReservationsDTO {
    private String date;
    private String time;
    private String workspace;
    private String status;

    public RecentReservationsDTO(String date, String time, String workspace, String status) {
        this.date = date;
        this.time = time;
        this.workspace = workspace;
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getWorkspace() {
        return workspace;
    }

    public String getStatus() {
        return status;
    }
}
