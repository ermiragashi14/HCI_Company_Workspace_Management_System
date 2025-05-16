package service;

import dto.NotificationDTO;
import model.Notification;
import model.User;
import repository.NotificationRepository;
import repository.UserRepository;

import java.util.List;

public class NotificationService {

    private NotificationRepository notificationRepository = new NotificationRepository();
    private UserRepository userRepository = new UserRepository();

    public void sendToAll(NotificationDTO dto) {
        List<User> users = userRepository.getAllUsersExceptInCompany(SessionManager.getInstance().getLoggedInUserId(), SessionManager.getInstance().getLoggedInCompanyId());

        for (User user : users) {
            if (user.getId() != dto.getSenderId()) {
                notificationRepository.createNotification(
                        user.getId(), dto.getSenderId(), dto.getMessage(), dto.getType()
                );
            }
        }
    }

    public void sendToUser(NotificationDTO dto) {
        notificationRepository.createNotification(
                dto.getReceiverId(), dto.getSenderId(), dto.getMessage(), dto.getType()
        );
    }

    public List<Notification> getUserNotifications(int userId) {
        return notificationRepository.getNotificationsForUser(userId);
    }

    public boolean markNotificationAsRead(int notificationId) {
        int userId = SessionManager.getInstance().getLoggedInUserId();
        return notificationRepository.markAsRead(notificationId, userId);
    }

    public List<User> getAllOtherUsersInCompany() {
        int currentUserId = SessionManager.getInstance().getLoggedInUserId();
        int currentCompanyId = SessionManager.getInstance().getLoggedInCompanyId();
        return userRepository.getAllUsersExceptInCompany(currentUserId, currentCompanyId);
    }

    public void sendSystemNotification(int receiverId, String type, String message) {
        int systemSenderId = SessionManager.getInstance().getLoggedInUserId();
        notificationRepository.createNotification(receiverId, systemSenderId, message, type);
    }

    public int countUnreadNotifications(int userId) {
        return notificationRepository.countUnreadNotifications(userId);
    }

}
