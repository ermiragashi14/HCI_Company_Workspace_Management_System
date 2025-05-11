package utils;

import javafx.scene.image.Image;

import java.io.File;

public class ImageUtils {

    public static Image loadProfileImage(String avatarPath) {

        File file;

        if (avatarPath != null && new File(avatarPath).exists()) {
            file = new File(avatarPath);
        } else {
            file = new File("user_photos/default.png");
        }

        return new Image(file.toURI().toString());
    }
}
