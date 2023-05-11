package gofpatterns.creational.prototype;

public class Main {
    public static void main(String[] args) {
        Circle circleOne = new Circle(4, "Red", "BobCircle", 20, 20);

        Circle clone = circleOne.clone();

        circleOne.setColor("Green");

        System.out.println(circleOne);
        System.out.println(clone);
    }
}
