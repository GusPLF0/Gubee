package gofpatterns.creational.builder;

public class Main {
    public static void main(String[] args) {

        User newUserWithBuilder = new User.UserBuilder().withAge(19).withName("Gustavo").build();
    }
}
