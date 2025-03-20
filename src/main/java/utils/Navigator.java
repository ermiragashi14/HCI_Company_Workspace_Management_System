package utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Navigator {
    public static void navigateTo(String fxmlFile, Node node) {
        if (node == null || node.getScene() == null) {
            System.err.println("Error: Node is null or has no scene! Navigation failed.");
            return;
        }

        try {
            Stage stage = (Stage) node.getScene().getWindow();
            if (stage == null) {
                System.err.println("Error: Could not retrieve the Stage from node!");
                return;
            }

            Parent root = FXMLLoader.load(Navigator.class.getResource("/views/" + fxmlFile));
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

