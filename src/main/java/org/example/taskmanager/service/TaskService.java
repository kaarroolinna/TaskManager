package org.example.taskmanager.service;

import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import org.example.taskmanager.model.Task;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.Comparator;
import java.util.function.Predicate;

public class TaskService {

    private final ObservableList<Task> tasks = FXCollections.observableArrayList();
    private final FilteredList<Task> filteredTasks = new FilteredList<>(tasks, t -> true);
    private final SortedList<Task> sortedTasks = new SortedList<>(filteredTasks);

    public ObservableList<Task> getTasks() {
        return tasks;
    }

    public void addTask(Task t) {
        tasks.add(t);
    }

    public void removeTask(Task t) {
        tasks.remove(t);
    }

    public SortedList<Task> getSortedTasks() {
        return sortedTasks;
    }

    public void setComparator(Comparator<Task> comparator) {
        sortedTasks.setComparator(comparator);
    }

    public void setFilter(Predicate<Task> predicate) {
        filteredTasks.setPredicate(predicate);
    }
}
