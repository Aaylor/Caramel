package formats;

public class WrongFormatException extends Exception {
    public WrongFormatException() {
        super();
    }

    public WrongFormatException(String message) {
        super("WrongFormatException:\n" + message);
    }
}
