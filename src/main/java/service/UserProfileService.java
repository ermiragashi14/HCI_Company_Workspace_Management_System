package service;

import dto.UserProfileDTO;
import model.Company;
import model.User;
import repository.CompanyRepository;
import repository.UserRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class UserProfileService {

    private final UserRepository userRepository = new UserRepository();
    private final CompanyRepository companyRepository = new CompanyRepository();

    public UserProfileDTO getUserProfile(int userId) {
        User user = userRepository.findById(userId);
        if (user == null) return null;

        Company company = companyRepository.findById(user.getCompanyId());
        String companyName = company != null ? company.getName() : "";
        String companyEmail = company != null ? company.getEmail() : "";

        return new UserProfileDTO(
                user.getFullName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getAvatarPath(),
                companyName,
                companyEmail
        );
    }

    public void updatePhone(int userId, String phoneNumber) {
        userRepository.updatePhone(userId, phoneNumber);
    }

    public void updateAvatar(int userId, File file) throws IOException {
        File destDir = new File("user_photos");
        if (!destDir.exists()) destDir.mkdirs();
        File destFile = new File(destDir, "user_" + userId + ".png");
        Files.copy(file.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        userRepository.updateAvatarPath(userId, destFile.getAbsolutePath());
    }

    public void removeAvatar(int userId) {
        File avatar = new File("user_photos/user_" + userId + ".png");
        if (avatar.exists()) avatar.delete();
        userRepository.updateAvatarPath(userId, null);
    }

    public void disableUser(int userId) {
        try {
            userRepository.updateUserStatus(userId, "DISABLED");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}