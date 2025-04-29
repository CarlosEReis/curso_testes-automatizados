package com.starwars.planetapi.common;

import com.starwars.planetapi.domain.Planet;

import java.util.List;

public class PlanetConstants {
    public static final Planet PLANET = new Planet("name", "climate", "terrain");
    public static final Planet INVALID_PLANET = new Planet("", "", "");

    public static final Planet TATOOINE = new Planet(1L, "Tatooine", "arid", "desert");
    public static final Planet ALDERAAN = new Planet(2L, "Alderaan", "temperate", "grassl");
    public static final Planet YAVINIV = new Planet(2L, "Yavin IV", "temperate, tropical", "grassl");
    public static final List<Planet> PLANETS = List.of(TATOOINE, ALDERAAN, YAVINIV);
}