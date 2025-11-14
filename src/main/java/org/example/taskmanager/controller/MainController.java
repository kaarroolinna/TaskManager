package org.example.taskmanager.controller;

import javafx.application.Platform;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.example.taskmanager.model.Task;
import org.example.taskmanager.service.TaskService;
import org.example.taskmanager.util.FileUtil;
import javafx.fxml.FXML;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class MainController {

    @FXML
    private ComboBox<String> sortComboBox;
    @FXML
    private ComboBox<String> filterStatusComboBox;
    @FXML
    private ComboBox<Task.Category> filterCategoryComboBox;
    @FXML
    private ListView<Task> taskList;

    private final TaskService service = new TaskService();

    @FXML
    public void initialize() {

        List<Task> loaded = FileUtil.load();
        if (loaded != null)
            service.getTasks().addAll(loaded);

        SortedList<Task> sortedTasks = service.getSortedTasks();
        taskList.setItems(sortedTasks);

        sortComboBox.getItems().addAll("Creation Date", "Priority", "Alphabetical (Title)");
        sortComboBox.setValue("Creation Date");
        sortComboBox.valueProperty().addListener((obs, oldVal, newVal) -> handleSortChange(newVal));
        handleSortChange("Creation Date");

        filterStatusComboBox.getItems().addAll("All", "Completed", "Pending");
        filterStatusComboBox.setValue("All");
        filterStatusComboBox.valueProperty().addListener((obs, oldVal, newVal) -> handleFilterChange());

        filterCategoryComboBox.getItems().addAll(Task.Category.values());
        filterCategoryComboBox.getItems().add(0, null);
        filterCategoryComboBox.setConverter(new javafx.util.StringConverter<Task.Category>() {
            @Override
            public String toString(Task.Category category) {
                if (category == null) {
                    return "ALL";
                }
                return category.name();
            }

            @Override
            public Task.Category fromString(String string) {
                if ("ALL".equals(string)) {
                    return null;
                }
                try {
                    return Task.Category.valueOf(string);
                } catch (IllegalArgumentException e) {
                    return null;
                }
            }
        });

        filterCategoryComboBox.setValue(null);
        filterCategoryComboBox.valueProperty().addListener((obs, oldVal, newVal) -> handleFilterChange());

        taskList.setCellFactory(listView -> new ListCell<>() {
            private final CheckBox checkBox = new CheckBox();
            {
                checkBox.setOnAction(event -> {
                    Task task = getItem();
                    if (task != null) {
                        task.setCompleted(checkBox.isSelected());
                        taskList.refresh();
                        handleFilterChange();
                    }
                });
            }

            @Override
            protected void updateItem(Task task, boolean empty) {
                super.updateItem(task, empty);
                setStyle(null);
                if (empty || task == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    String colorStyle = "";
                    switch (task.getPriority()) {
                        case HIGH: colorStyle = "-fx-text-fill: red;"; break;
                        case MEDIUM: colorStyle = "-fx-text-fill: orange;"; break;
                        case LOW: colorStyle = "-fx-text-fill: green;"; break;
                        default: colorStyle = "-fx-text-fill: black;"; break;
                    }

                    if (task.isCompleted()) {
                        colorStyle += " -fx-strikethrough: true; -fx-text-fill: gray;";
                    } else {
                        colorStyle += " -fx-strikethrough: false;";
                    }

                    checkBox.setStyle(colorStyle);
                    checkBox.setText(task.toString());
                    checkBox.setSelected(task.isCompleted());
                    setGraphic(checkBox);
                }
            }
        });
    }

    private void handleSortChange(String sortType) {
        Comparator<Task> comparator;

        switch (sortType) {
            case "Priority":
                comparator = Comparator.comparing(Task::getPriority, (p1, p2) -> {
                    // HIGH=3, MEDIUM=2, LOW=1
                    int val1 = p1 == Task.Priority.HIGH ? 3 : p1 == Task.Priority.MEDIUM ? 2 : 1;
                    int val2 = p2 == Task.Priority.HIGH ? 3 : p2 == Task.Priority.MEDIUM ? 2 : 1;
                    return Integer.compare(val2, val1);
                });
                break;
            case "Alphabetical (Title)":
                comparator = Comparator.comparing(Task::getTitle);
                break;
            case "Creation Date":
            default:
                comparator = Comparator.comparing(Task::getCreationDate).reversed();
                break;
        }
        service.setComparator(comparator);
    }

    private void handleFilterChange() {
        Predicate<Task> statusFilter;
        Predicate<Task> categoryFilter;

        String status = filterStatusComboBox.getValue();
        if ("Completed".equals(status)) {
            statusFilter = Task::isCompleted;
        } else if ("Pending".equals(status)) {
            statusFilter = t -> !t.isCompleted();
        } else {
            statusFilter = t -> true;
        }

        Task.Category category = filterCategoryComboBox.getValue();
        if (category != null) {
            categoryFilter = t -> t.getCategory() == category;
        } else {
            categoryFilter = t -> true;
        }
        service.setFilter(statusFilter.and(categoryFilter));
    }

    public void addTask() {
        Dialog<Pair<String, Pair<Task.Priority, Task.Category>>> dialog = new Dialog<>();
        dialog.setTitle("Add New Task");
        dialog.setHeaderText("Enter details for the new task:");

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField titleField = new TextField();
        titleField.setPromptText("Task Title");

        ComboBox<Task.Priority> priorityComboBox = new ComboBox<>();
        priorityComboBox.getItems().addAll(Task.Priority.values());
        priorityComboBox.setValue(Task.Priority.MEDIUM);

        ComboBox<Task.Category> categoryComboBox = new ComboBox<>();
        categoryComboBox.getItems().addAll(Task.Category.values());
        categoryComboBox.setValue(Task.Category.PERSONAL);

        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Priority:"), 0, 1);
        grid.add(priorityComboBox, 1, 1);
        grid.add(new Label("Category:"), 0, 2);
        grid.add(categoryComboBox, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                if (titleField.getText().trim().isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Task title cannot be empty!");
                    alert.showAndWait();
                    return null;
                }

                return new Pair<>(
                        titleField.getText(),
                        new Pair<>(priorityComboBox.getValue(), categoryComboBox.getValue())
                );
            }
            return null;
        });

        Optional<Pair<String, Pair<Task.Priority, Task.Category>>> result = dialog.showAndWait();

        result.ifPresent(taskDetails -> {
            String title = taskDetails.getKey();
            Task.Priority priority = taskDetails.getValue().getKey();
            Task.Category category = taskDetails.getValue().getValue();

            service.addTask(new Task(title, "", Task.Status.TODO, priority, category));
        });
    }

    public void editTask() {
        Task task = taskList.getSelectionModel().getSelectedItem();
        if (task == null) return;

        TextInputDialog dialog = new TextInputDialog(task.getTitle());
        dialog.setHeaderText("Edit title");
        String newTitle = dialog.showAndWait().orElse(null);

        if (newTitle == null) return;

        task.setTitle(newTitle);
        taskList.refresh();
    }

    public void deleteTask() {
        Task task = taskList.getSelectionModel().getSelectedItem();
        if (task != null)
            service.removeTask(task);
    }

    public void saveTasks() {
        FileUtil.save(service.getTasks());
    }

    @FXML
    private void handleExit() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Exit");
        alert.setHeaderText("Ви впевнені, що хочете вийти?");
        alert.setContentText("Усі незбережені дані будуть втрачені, якщо ви не натиснете 'Save'.");
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/org/example/taskmanager/exit.png")));

        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            Platform.exit();
        }
    }
}
