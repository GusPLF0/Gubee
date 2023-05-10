package recordexercise;

public class MainRecord {
    public static void main(String[] args) {
        Person personOne = new Person("Fulano", 19, "Rua dos trovões");
        Person personTwo = new Person("Ciclano", 30, "Rua dos raios");
        Person personThree = new Person("Deltrano", 50, "Rua dos granizos");

        // Testando o toString();
        System.out.println(personOne);
        System.out.println(personTwo);
        System.out.println(personThree);

        System.out.println("------------------------------------------------------");

        // Testando imprimir detalhes
        System.out.println("Age: " + personOne.age() + ", Name: " + personOne.name() + ", Address: " + personOne.address());

        System.out.println("------------------------------------------------------");

        // Testando o Equals/Hashcode
        System.out.println(personTwo.equals(personOne));

        personOne = new Person("Ciclano", 30, "Rua dos raios");

        System.out.println(personTwo.equals(personOne));
    }
}
