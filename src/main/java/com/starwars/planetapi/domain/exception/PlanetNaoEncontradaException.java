package com.starwars.planetapi.domain.exception;

public class PlanetNaoEncontradaException extends EntityNaoEncontradaException {

    private static final String MESSAGE = "Planeta com o ID %d não encontrado.";

    public PlanetNaoEncontradaException(Long idPlanet) {
        super(String.format(MESSAGE, idPlanet));
    }
}
