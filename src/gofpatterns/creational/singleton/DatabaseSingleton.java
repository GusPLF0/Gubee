package gofpatterns.creational.singleton;

import java.util.ArrayList;
import java.util.List;

public class DatabaseSingleton {
    private static DatabaseSingleton instance;
    private List<User> users = new ArrayList<>();

    private DatabaseSingleton() {
    }

    static DatabaseSingleton getInstance() {
        if (instance == null) {
            instance = new DatabaseSingleton();
        }
        return instance;
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
