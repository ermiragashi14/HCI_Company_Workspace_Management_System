package utils;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TitledPane;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.List;

public class KeyboardNavigator {

    public static void enableFullNavigation(Pane navbar, Pane ribbon, Pane content) {
        List<Node> focusables = new ArrayList<>();
        focusables.addAll(getFocusableNodes(navbar));
        focusables.addAll(getFocusableNodes(ribbon));
        focusables.addAll(getFocusableNodes(content));

        addArrowKeyNavigation(focusables);

        if (!focusables.isEmpty()) {
            Platform.runLater(() -> focusables.get(0).requestFocus());
        }
    }

    public static void enableAdvancedNavigation(Pane navbar, Pane ribbon, Pane content, TitledPane advancedPane, Pane advancedFiltersContent) {
        List<Node> focusables = new ArrayList<>();
        focusables.addAll(getFocusableNodes(navbar));
        focusables.addAll(getFocusableNodes(ribbon));
        focusables.addAll(getFocusableNodes(content));

        addArrowKeyNavigation(focusables);

        if (!focusables.isEmpty()) {
            Platform.runLater(() -> focusables.get(0).requestFocus());
        }

        advancedPane.setFocusTraversable(true);
        advancedPane.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.SPACE) {
                boolean shouldExpand = !advancedPane.isExpanded();
                advancedPane.setExpanded(shouldExpand);
                event.consume();

                if (shouldExpand) {
                    Platform.runLater(() -> {
                        List<Node> advancedFocusables = getFocusableNodes(advancedFiltersContent);
                        if (!advancedFocusables.isEmpty()) {
                            advancedFocusables.get(0).requestFocus();
                        }
                        addArrowKeyNavigation(advancedFocusables);
                    });
                }
            }
        });
    }

    public static void enableNavigation(Pane... containers) {
        List<Node> focusables = new ArrayList<>();
        for (Pane container : containers) {
            focusables.addAll(getFocusableNodes(container));
        }

        addArrowKeyNavigation(focusables);

        if (!focusables.isEmpty()) {
            Platform.runLater(() -> focusables.get(0).requestFocus());
        }
    }

    public static void enableTitledPaneKeyboardSupport(TitledPane titledPane, Pane contentPane) {
        titledPane.setFocusTraversable(true);

        titledPane.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.SPACE) {
                boolean shouldExpand = !titledPane.isExpanded();
                titledPane.setExpanded(shouldExpand);
                event.consume();

                if (shouldExpand) {
                    Platform.runLater(() -> {
                        List<Node> focusables = getFocusableNodes(contentPane);
                        if (!focusables.isEmpty()) {
                            focusables.get(0).requestFocus();
                        }
                        addArrowKeyNavigation(focusables);
                    });
                }
            }
        });
    }

    public static List<Node> getFocusableNodes(Parent parent) {
        List<Node> focusables = new ArrayList<>();
        traverseFocusableNodes(parent, focusables);
        return focusables;
    }

    private static void traverseFocusableNodes(Node node, List<Node> result) {
        if (node.isFocusTraversable() && !(node instanceof Pane)) {
            result.add(node);
        }

        if (node instanceof Parent parent) {
            for (Node child : parent.getChildrenUnmodifiable()) {
                traverseFocusableNodes(child, result);
            }
        }
    }

    private static void addArrowKeyNavigation(List<Node> focusables) {
        for (int i = 0; i < focusables.size(); i++) {
            Node currentNode = focusables.get(i);
            final int currentIndex = i;

            currentNode.setOnKeyPressed(event -> {
                switch (event.getCode()) {
                    case RIGHT, DOWN -> {
                        int next = (currentIndex + 1) % focusables.size();
                        focusables.get(next).requestFocus();
                        event.consume();
                    }
                    case LEFT, UP -> {
                        int prev = (currentIndex - 1 + focusables.size()) % focusables.size();
                        focusables.get(prev).requestFocus();
                        event.consume();
                    }
                    case ENTER -> {
                        if (currentNode instanceof ComboBox<?> combo) {
                            combo.show();
                        } else {
                            currentNode.fireEvent(new javafx.event.ActionEvent());
                        }
                        event.consume();
                    }
                    case SPACE -> {
                        if (currentNode instanceof ComboBox<?> combo) {
                            combo.show();
                            event.consume();
                        }
                    }
                }
            });
        }
    }
}
