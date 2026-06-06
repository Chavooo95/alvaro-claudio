package com.boardgames.catalog.domain;

public abstract class CatalogDomainException extends RuntimeException {
    protected CatalogDomainException(String message) {
        super(message);
    }
}
