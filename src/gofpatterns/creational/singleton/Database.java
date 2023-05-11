package gofpatterns.creational.singleton;

import java.util.ArrayList;
import java.util.List;

public class Database {
    private static Database instance;
    private List<User> users = new ArrayList<>();

    private Database() {
    }

    static Database getInstance() {
        if (instance == null) {
            instance = new Database();
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
