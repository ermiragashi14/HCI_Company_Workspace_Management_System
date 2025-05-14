package dto;

public class UpcomingReservationDTO {
    private String date;
    private String time;
    private String workspace;

    public UpcomingReservationDTO(String date, String time, String workspace) {
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
