package com.boardgames.catalog.domain;

public class InvalidGameException extends CatalogDomainException {
    public InvalidGameException(String message) {
        super(message);
    }
}
