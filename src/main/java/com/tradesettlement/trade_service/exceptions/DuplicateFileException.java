package com.tradesettlement.trade_service.exceptions;

public class DuplicateFileException extends RuntimeException {
    public DuplicateFileException(String errorMessage) {
        super(errorMessage);
    }
}
