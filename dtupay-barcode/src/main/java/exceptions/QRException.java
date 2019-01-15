package exceptions;

public class QRException extends Exception {
    public QRException() {
    }

    public QRException(String message) {
        super(message);
    }

    public QRException(String message, Throwable cause) {
        super(message, cause);
    }
}
