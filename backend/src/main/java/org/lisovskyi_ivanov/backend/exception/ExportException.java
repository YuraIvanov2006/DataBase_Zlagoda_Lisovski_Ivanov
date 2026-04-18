package org.lisovskyi_ivanov.backend.exception;

public class ExportException extends RuntimeException {

    public ExportException(String message) {
        super(message);
    }

    public ExportException(String format, Throwable cause) {
        super("Failed to export " + format + " file", cause);
    }
}
