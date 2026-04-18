package org.lisovskyi_ivanov.backend.exception;

public class InsufficientStockException extends RuntimeException {

    public InsufficientStockException(String upc) {
        super("Insufficient stock for product with UPC: " + upc);
    }

    public InsufficientStockException(String upc, int available, int requested) {
        super("Insufficient stock for product with UPC: " + upc +
                ". Available: " + available +
                ", requested: " + requested);
    }
}