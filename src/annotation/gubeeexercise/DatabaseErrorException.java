package annotation.gubeeexercise;

public class DatabaseErrorException extends RuntimeException{
    public DatabaseErrorException(String message) {
        super(message);
    }
}
