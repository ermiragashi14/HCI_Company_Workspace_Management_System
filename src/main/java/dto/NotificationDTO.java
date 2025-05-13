package dto;

public class NotificationDTO {

    private int receiverId;
    private String message;
    private String type;
    private int senderId;

    public NotificationDTO(int receiverId, int senderId, String message, String type) {
        this.receiverId = receiverId;
        this.senderId = senderId;
        this.message = message;
        this.type = type;
    }

    public int getReceiverId() {
        return receiverId;
    }
    public void setReceiverId(int receiverId){
        this.receiverId=receiverId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message){
        this.message=message;
    }

    public String getType() {
        return type;
    }

    public int getSenderId(){
        return senderId;
    }

    public void setSenderId(int senderId){
        this.senderId=senderId;
    }
}
