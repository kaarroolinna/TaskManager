module org.example.taskmanager {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires javafx.graphics;

    opens org.example.taskmanager.controller to javafx.fxml;
    opens org.example.taskmanager.model to com.google.gson;

    exports org.example.taskmanager;
    exports org.example.taskmanager.controller;
}
