package service;

import dto.ManagedUserDTO;
import model.User;
import repository.ManageUsersRepository;
import repository.UserRepository;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ManageUsersService {

    private final ManageUsersRepository manageusersRepository = new ManageUsersRepository();
    private final UserRepository userRepository = new UserRepository();
    private final SessionManager sessionManager = SessionManager.getInstance();

    public List<ManagedUserDTO> filterUsers(String name, String email, String status, String role, String createdAt) {
        try {
            int companyId = sessionManager.getLoggedInCompanyId();
            String currentUserRole = sessionManager.getLoggedInUserRole();

            List<User> users = manageusersRepository.getUsersWithFilters(companyId, name, email, status, role, createdAt, currentUserRole);
            return convertToDTO(users);
        } catch (SQLException e) {
            System.err.println("Error fetching users: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public boolean updateUserStatus(int userId, String newStatus) {
        try {
            User targetUser = userRepository.findById(userId);
            if (targetUser == null) return false;

            String currentUserRole = sessionManager.getLoggedInUserRole();

            if ("ADMIN".equalsIgnoreCase(currentUserRole) && !"STAFF".equalsIgnoreCase(targetUser.getRole())) {
                return false;
            }

            return userRepository.updateUserStatus(userId, newStatus);
        } catch (SQLException e) {
            System.err.println("Error updating user status: " + e.getMessage());
            return false;
        }
    }


    private List<ManagedUserDTO> convertToDTO(List<User> users) {
        List<ManagedUserDTO> dtos = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        for (User user : users) {
            dtos.add(new ManagedUserDTO(
                    user.getId(),
                    user.getFullName(),
                    user.getEmail(),
                    user.getPhoneNumber(),
                    user.getRole(),
                    user.getStatus(),
                    user.getCreatedAt() != null ? sdf.format(user.getCreatedAt()) : "",
                    "",// Placeholder for lastLogin if available,
                    user.getAvatarPath()
            ));
        }
        return dtos;
    }
}
