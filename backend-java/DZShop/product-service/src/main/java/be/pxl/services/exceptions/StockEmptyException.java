package be.pxl.services.exceptions;

public class StockEmptyException extends RuntimeException {
    public StockEmptyException(String message) {
        super(message);
    }
}
