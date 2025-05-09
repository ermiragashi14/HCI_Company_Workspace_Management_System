package dto;

public class UserStatDTO {
    private String fullName;
    private int reservationCount;

    public UserStatDTO(String fullName, int reservationCount) {
        this.fullName = fullName;
        this.reservationCount = reservationCount;
    }

    public String getFullName() {
        return fullName;
    }

    public int getReservationCount() {
        return reservationCount;
    }
}
