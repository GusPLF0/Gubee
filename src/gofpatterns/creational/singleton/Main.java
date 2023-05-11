package gofpatterns.creational.singleton;

public class Main {
    public static void main(String[] args) {

        // Database db1 = new Database(); --> Impossível de fazer

        Database db1 = Database.getInstance(); // Mesma instância

        db1.addUser(new User("Gustavo", 19));
        db1.addUser(new User("Pedro", 19));
        db1.addUser(new User("Lima", 19));

        Database db2 = Database.getInstance(); // Mesma instância

        db2.showData();
    }
}
