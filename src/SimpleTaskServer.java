
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class SimpleTaskServer implements HttpHandler {

    private static final int PORT = 8080;
    private static final String PUBLIC_DIR = "public";
    private static final String DATA_FILE = "tasks.dat";

    private static List<HashMap<String, String>> tasks = new ArrayList<>();
    private static final AtomicInteger idCounter = new AtomicInteger(1);

    public static void main(String[] args) throws IOException {
        loadTasks();
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/", new SimpleTaskServer());
        server.setExecutor(null);
        System.out.println("Server started on http://localhost:" + PORT);
        server.start();
    }

    @Override
    public void handle(HttpExchange t) throws IOException {
        String uri = t.getRequestURI().getPath();
        if (uri.startsWith("/api/tasks")) {
            handleApi(t);
        } else {
            handleStatic(t);
        }
    }

    private void handleStatic(HttpExchange t) throws IOException {
        String uri = t.getRequestURI().getPath();
        if (uri.equals("/"))
            uri = "/index.html";

        if (uri.startsWith("/")) {
            uri = uri.substring(1);
        }
        Path path = Paths.get(PUBLIC_DIR, uri);
        if (!Files.exists(path)) {
            String response = "404 (Not Found)\n";
            t.sendResponseHeaders(404, response.length());
            try (OutputStream os = t.getResponseBody()) {
                os.write(response.getBytes());
            }
        } else {
            String contentType = "text/html";
            if (uri.endsWith(".css"))
                contentType = "text/css";
            else if (uri.endsWith(".js"))
                contentType = "application/javascript";
            else if (uri.endsWith(".json"))
                contentType = "application/json";

            t.getResponseHeaders().set("Content-Type", contentType);
            t.sendResponseHeaders(200, Files.size(path));
            try (OutputStream os = t.getResponseBody()) {
                Files.copy(path, os);
            }
        }
    }

    private void handleApi(HttpExchange t) throws IOException {
        String method = t.getRequestMethod();
        if (method.equals("GET")) {
            String json = "[" + tasks.stream().map(this::taskToJson).collect(Collectors.joining(",")) + "]";
            sendJson(t, json, 200);
        } else if (method.equals("POST")) {
            String body = readBody(t);
            String title = extractValue(body, "title");
            if (title != null && !title.isEmpty()) {
                HashMap<String, String> newTask = new HashMap<>();
                newTask.put("id", String.valueOf(idCounter.getAndIncrement()));
                newTask.put("title", title);
                newTask.put("status", "ongoing");
                tasks.add(newTask);
                saveTasks();
                sendJson(t, taskToJson(newTask), 200);
            } else {
                sendJson(t, "{}", 400);
            }
        } else if (method.equals("PUT")) {
            String path = t.getRequestURI().getPath();
            try {
                String id = path.substring(path.lastIndexOf('/') + 1);
                String body = readBody(t);
                String status = extractValue(body, "status");
                String title = extractValue(body, "title");
                for (HashMap<String, String> task : tasks) {
                    if (task.get("id").equals(id)) {
                        if (status != null && !status.isEmpty())
                            task.put("status", status);
                        if (title != null && !title.isEmpty())
                            task.put("title", title);

                        if (title != null && !title.isEmpty())
                            task.put("title", title);

                        saveTasks();
                        sendJson(t, taskToJson(task), 200);
                        return;
                    }
                }
                sendJson(t, "{}", 404);
            } catch (Exception e) {
                sendJson(t, "{}", 400);
            }
        } else if (method.equals("DELETE")) {
            String path = t.getRequestURI().getPath();
            try {
                String id = path.substring(path.lastIndexOf('/') + 1);
                if (tasks.removeIf(task -> task.get("id").equals(id))) {
                    saveTasks();
                    sendJson(t, "{}", 200);
                } else {
                    sendJson(t, "{}", 404);
                }
            } catch (Exception e) {
                sendJson(t, "{}", 400); //// exception cases !
            }
        } else {
            t.sendResponseHeaders(405, -1);
        }
    }

    private String taskToJson(HashMap<String, String> task) {
        return String.format("{\"id\":%s,\"title\":\"%s\",\"status\":\"%s\"}", task.get("id"),
                task.get("title").replace("\"", "\\\""), task.get("status"));
    }

    private String readBody(HttpExchange t) {
        try (java.util.Scanner s = new java.util.Scanner(t.getRequestBody()).useDelimiter("\\A")) {
            return s.hasNext() ? s.next() : "";
        }
    }

    private void sendJson(HttpExchange t, String json, int code) throws IOException {
        t.getResponseHeaders().set("Content-Type", "application/json");
        byte[] bytes = json.getBytes("UTF-8");
        t.sendResponseHeaders(code, bytes.length);
        try (OutputStream os = t.getResponseBody()) {
            os.write(bytes);
        }
    }

    private String extractValue(String json, String key) {
        String keyPattern = "\"" + key + "\":\"";
        int start = json.indexOf(keyPattern);
        if (start == -1)
            return null;
        start += keyPattern.length();
        int end = json.indexOf("\"", start);
        if (end == -1)
            return null;
        return json.substring(start, end);
    }

    private static void saveTasks() {
        try (java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(new java.io.FileOutputStream(DATA_FILE))) {
            oos.writeObject(tasks);
            oos.writeObject(idCounter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private static void loadTasks() {
        if (Files.exists(Paths.get(DATA_FILE))) {
            try (java.io.ObjectInputStream ois = new java.io.ObjectInputStream(
                    new java.io.FileInputStream(DATA_FILE))) {
                tasks = (List<HashMap<String, String>>) ois.readObject();
                idCounter.set(((AtomicInteger) ois.readObject()).get());
            } catch (Exception e) {
                e.printStackTrace();
            } // unchecked exception
        }
    }
}
