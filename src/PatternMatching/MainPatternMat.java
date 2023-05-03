package PatternMatching;

public class MainPatternMat {
    public static void main(String[] args) {

        Person personOne = new Intern("Carlos");


        if (personOne instanceof Employee e) {
            System.out.println("Employee:" + e.getName());
        } else if (personOne instanceof Manager m) {
            System.out.println("Manager:" + m.getName());
        } else if (personOne instanceof Intern i){
            System.out.println("Intern:" + i.getName());
        } else {
            System.out.println("Desconhecido");
        }
    }
}
