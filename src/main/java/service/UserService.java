package service;

import model.User;
import repository.UserRepository;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class UserService {

    private final UserRepository userRepository = new UserRepository();

    public int registerUser(User user, Connection connection) throws SQLException {
        return userRepository.registerUser(user, connection);
    }

    public boolean userExists(String email) throws SQLException {
        return userRepository.userExists(email);
    }

    public Optional<User> getUserByEmail(String email) throws SQLException {
        return userRepository.getUserByEmail(email);
    }

    public User findById(int userId) {
        return userRepository.findById(userId);
    }

    public boolean updateUserStatus(int userId, String newStatus) throws SQLException {
        return userRepository.updateUserStatus(userId, newStatus);
    }

    public void updatePhone(int userId, String newPhoneNumber) {
        userRepository.updatePhone(userId, newPhoneNumber);
    }

    public void updateAvatarPath(int userId, String path) {
        userRepository.updateAvatarPath(userId, path);
    }

    public List<User> getAllUsersExcept(int excludedId, int companyId) {
        return userRepository.getAllUsersExceptInCompany(excludedId, companyId);
    }

    public String getFullNameById(int userId) {
        User user = userRepository.findById(userId);
        return (user != null) ? user.getFullName() : "Unknown User";
    }
}
