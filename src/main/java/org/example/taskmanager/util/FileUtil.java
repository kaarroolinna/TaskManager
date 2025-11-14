package org.example.taskmanager.util;

import org.example.taskmanager.model.Task;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class FileUtil {

    private static final String FILE = "tasks.json";
    private static class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
        private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        @Override
        public void write(JsonWriter out, LocalDateTime value) throws IOException {
            if (value != null) {
                out.value(value.format(formatter));
            } else {
                out.nullValue();
            }
        }

        @Override
        public LocalDateTime read(JsonReader in) throws IOException {
            if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            return LocalDateTime.parse(in.nextString(), formatter);
        }
    }

    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    public static void save(List<Task> tasks) {
        try (Writer writer = new FileWriter(FILE)) {
            gson.toJson(tasks, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Task> load() {
        try (Reader reader = new FileReader(FILE)) {
            return gson.fromJson(reader, new TypeToken<List<Task>>(){}.getType());
        } catch (FileNotFoundException e) {
            System.out.println("No existing tasks.json file found.");
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
