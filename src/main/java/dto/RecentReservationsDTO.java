package dto;

public class RecentReservationsDTO {
    private String date;
    private String time;
    private String workspace;

    public RecentReservationsDTO(String date, String time, String workspace) {
        this.date = date;
        this.time = time;
        this.workspace = workspace;
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
}
