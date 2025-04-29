package com.starwars.planetapi.domain.exception;

public class EntityNaoEncontradaException extends RuntimeException {

    public EntityNaoEncontradaException(String message) {
        super(message);
    }
}
