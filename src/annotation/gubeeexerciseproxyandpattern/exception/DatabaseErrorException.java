package annotation.gubeeexerciseproxyandpattern.exception;

public class DatabaseErrorException extends RuntimeException{
    public DatabaseErrorException(String message) {
        super(message);
    }
}
