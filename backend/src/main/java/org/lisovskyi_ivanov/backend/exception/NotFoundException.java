package org.lisovskyi_ivanov.backend.exception;

import java.util.List;

public class NotFoundException extends RuntimeException {

    public NotFoundException(Class<?> clazz, Object id) {
        super(clazz.getSimpleName() + " was not found with id: " + id);
    }

    public NotFoundException(Class<?> clazz, String field, Object value) {
        super(clazz.getSimpleName() + " was not found with " + field + ": " + value);
    }

    public NotFoundException(Class<?> clazz, List<String> fields, List<Object> values) {
        super(buildMessage(clazz, fields, values));
    }

    public NotFoundException(Class<?> clazz, String message) {
        super(clazz.getSimpleName() + " error: " + message);
    }


    private static String buildMessage(Class<?> clazz, List<String> fields, List<Object> values) {
        if (fields.size() != values.size()) {
            throw new IllegalArgumentException("Fields and values must have the same size");
        }
        StringBuilder sb = new StringBuilder(clazz.getSimpleName())
                .append(" was not found with: ");
        for (int i = 0; i < fields.size(); i++) {
            sb.append(fields.get(i)).append("=").append(values.get(i));
            if (i < fields.size() - 1) sb.append(", ");
        }
        return sb.toString();
    }
}
