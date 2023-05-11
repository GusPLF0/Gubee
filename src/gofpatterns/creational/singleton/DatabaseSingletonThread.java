package gofpatterns.creational.singleton;

import java.util.ArrayList;
import java.util.List;

public class DatabaseSingletonThread {
    private static DatabaseSingletonThread instance;
    private List<User> users = new ArrayList<>();

    private DatabaseSingletonThread() {
    }

    static DatabaseSingletonThread getInstance() {
        DatabaseSingletonThread databaseSingletonThread = instance;

        if (instance != null) {
            return instance;
        }

        synchronized (DatabaseSingletonThread.class) {
            if (instance == null) {
                instance = new DatabaseSingletonThread();
            }
            return instance;
        }

    }

    void addUser(User user) {
        this.users.add(user);
    }

    void removeUser(String name) {
        this.users.removeIf(user -> user.getName().equals(name));
    }

    void showData() {
        for (User user : users) {
            System.out.println(user);
        }
    }
}
