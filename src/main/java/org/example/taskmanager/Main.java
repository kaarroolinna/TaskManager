package org.example.taskmanager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/taskmanager/main.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        try {
            Font loadedFont = Font.loadFont(getClass().getResource("/fonts/StartStory.ttf").toExternalForm(), 10);
            if (loadedFont == null) {
                System.err.println("ПОМИЛКА: Шрифт не завантажено. Перевірте назву та шлях.");
            } else {
                System.out.println("Шрифт успішно завантажено в пам'ять.");
            }
        } catch (Exception e) {
            System.err.println("Помилка при завантаженні шрифту:");
            e.printStackTrace();
        }
        stage.setTitle("Task Manager");
        stage.setScene(scene);
        stage.setWidth(600);
        stage.setHeight(400);
        try {
            String iconPath = "/org/example/taskmanager/task_icon.png";
            Image applicationIcon = new Image(getClass().getResourceAsStream(iconPath));
            stage.getIcons().add(applicationIcon);
        } catch (Exception e) {
            System.err.println("Помилка завантаження іконки: Перевірте шлях і існування файлу.");
            e.printStackTrace();
        }
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
