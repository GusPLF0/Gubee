package sealedexercise;

public class MainSealed {
    public static void main(String[] args) {
        Shape shapeOne = new Circle(4);
        Shape shapeTwo = new Rectangle(4, 5);
        Shape shapeThree = new Triangle(3, 5);

        System.out.println(shapeOne.calculateArea());
        System.out.println(shapeTwo.calculateArea());
        System.out.println(shapeThree.calculateArea());
    }
}
