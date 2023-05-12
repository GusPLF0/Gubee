package annotation.gubeeexerciseproxyandpattern;

public class DatabaseErrorException extends RuntimeException{
    public DatabaseErrorException(String message) {
        super(message);
    }
}
